package com.rechs.turtleapp;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.navigation.NavigationView;

import com.rechs.turtleapp.ble.ConnectionManager;

import java.util.Calendar;
import java.util.List;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private SharedViewModel viewModel; // Variable for sharedViewModel
    private SharedPreferences sp; // Variable for sharedPreferences, stores local data

    private Handler mainHandler = new Handler(); // Variable for handler, used to communicate from a background thread to main UI to make changes

    private volatile Context mContext;

    private DrawerLayout drawer; // NavigationView

    NavigationView navigationView; // NavigationView

    // return codes
    final int RESULT_ROBOT_DEVICE_NAME = 101;
    final int RESULT_DOME_DEVICE_NAME = 102;

    // BLE Devices for robot and dome
    BluetoothDevice robotDevice;
    BluetoothDevice domeDevice;

    // booleans to check if robot/dome as been configured
    boolean isRobotConfigured = false;
    boolean isDomeConfigured = false;

    List<BluetoothGattCharacteristic> robotCharList; // List that has all BLE Characteristics
    int robotCharSize; // Size of characteristics list

    List<BluetoothGattCharacteristic> domeCharList; // List all BLE Chars
    int domeCharSize; // Size of char list

    Integer[] domeSensorArr = {0, 0, 0}; // Array for sensor readings
    Integer[] domeAverageSensorArr = {0, 0}; // Array for average sensor readings

    SensorLog temperatureSensorLog; // Log for keeping track of temperature sensor history
    SensorLog humiditySensorLog; // Log for keeping track of humidity sensor history

    private Calendar calendar;

    private int cachedDay;
    private int cachedWeek;

    private boolean isDaySent = false;





    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main); // Inflate main layout


        /**
         * Initial setup things
         */
        // Set up Timber Logging
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        viewModel = new ViewModelProvider(this).get(SharedViewModel.class); // Set up viewModel in MainActivity Scope

        sp = getSharedPreferences("AppData", Context.MODE_PRIVATE); // Set up shared preferences to handle history

        temperatureSensorLog = new SensorLog(sp, "temperature");
        humiditySensorLog = new SensorLog(sp, "humidity");

        calendar = Calendar.getInstance();

        // Update cached day
        if (sp.getInt("day", 0) == 0) {
            cachedDay = calendar.get(Calendar.DAY_OF_WEEK);
            sp.edit().putInt("day", cachedDay).apply();
        } else {
            cachedDay = sp.getInt("day", 0);
        }

        // Update cached week
        if (sp.getInt("week", 0) == 0) {
            cachedWeek = calendar.get(Calendar.WEEK_OF_MONTH);
            sp.edit().putInt("week", cachedWeek).apply();
        } else {
            cachedWeek = sp.getInt("week", 0);
        }


        /**
         * Navigation View things
         */
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            navigationView.setCheckedItem(R.id.nav_information);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new InformationFragment()).commit();
        }


        /**
         * ViewModel things
         */
        mContext = this; // Get context for activity

        // Set up viewModel to observe directionNum, runs lambda function when there is a change
        viewModel.getDirectionNum().observe(this, item -> {
            if(isRobotConfigured) {
                // Write to characteristic, pass the direction that is being passed to feather
                ConnectionManager.INSTANCE.writeCharacteristic(robotDevice, robotCharList.get(robotCharSize - 1), TurtleListeners.Companion.hexToBytes(Integer.toHexString(item)));
            }
        });

        // Set up viewModel to observe electromagnetToggleNum
        viewModel.getElectromagnetPowerNum().observe(this, item -> {
            Timber.tag("E-MAG").e(item.toString());
            if(isRobotConfigured && robotCharList != null) {
                // Write to characteristic, pass the toggle that is being passed to feather
                ConnectionManager.INSTANCE.writeCharacteristic(robotDevice, robotCharList.get(robotCharSize - 2), TurtleListeners.Companion.hexToBytes(Integer.toHexString(item)));
            }
        });

        viewModel.getElectromagnetDirectionNum().observe(this, item -> {
            if(isRobotConfigured) {
                // Write to characteristic, pass the direction that is being passed to feather
                ConnectionManager.INSTANCE.writeCharacteristic(robotDevice, robotCharList.get(robotCharSize - 3), TurtleListeners.Companion.hexToBytes(Integer.toHexString(item)));
            }
        });

        /**** Background Thread to read BLE characteristics for new data ****/
        new Thread(() -> {

            while(true) {
                if(isDomeConfigured) {
                    // Send day if not sent, just started app
                    if (!isDaySent) {
                        ConnectionManager.INSTANCE.writeCharacteristic(domeDevice, domeCharList.get(domeCharSize - 6), TurtleListeners.Companion.hexToBytes(Integer.toHexString(cachedDay)));
                        isDaySent = true;
                    }

                    if (cachedDay != calendar.get(Calendar.DAY_OF_WEEK)) {
                        // Update cachedDay
                        cachedDay = calendar.get(Calendar.DAY_OF_WEEK);
                        sp.edit().putInt("day", cachedDay).apply();

                        // Send new day
                        ConnectionManager.INSTANCE.writeCharacteristic(domeDevice, domeCharList.get(domeCharSize - 6), TurtleListeners.Companion.hexToBytes(Integer.toHexString(cachedDay)));

                        SystemClock.sleep(5000); // Wait for feather to receive and return average

                        // Get averages
                        ConnectionManager.INSTANCE.readCharacteristic(domeDevice, domeCharList.get(domeCharSize - 4));
                        ConnectionManager.INSTANCE.readCharacteristic(domeDevice, domeCharList.get(domeCharSize - 5));

                        SystemClock.sleep(5000); // Wait

                        // Update daily averages
                        temperatureSensorLog.setDayAverage(SensorLog.previousDay(cachedDay), domeAverageSensorArr[0]);
                        humiditySensorLog.setDayAverage(SensorLog.previousDay(cachedDay), domeAverageSensorArr[1]);
                    }

                    if (cachedWeek != calendar.get(Calendar.WEEK_OF_MONTH)) {
                        // Update cachedWeek
                        cachedWeek = calendar.get(Calendar.WEEK_OF_MONTH);
                        sp.edit().putInt("week", cachedWeek).apply();

                        // Update weekly averages
                        temperatureSensorLog.setWeekAverage(SensorLog.previousWeek(cachedWeek));
                        humiditySensorLog.setWeekAverage(SensorLog.previousWeek(cachedWeek));
                    }



                    // Get sensor info and update
                    ConnectionManager.INSTANCE.readCharacteristic(domeDevice, domeCharList.get(domeCharSize - 1));
                    ConnectionManager.INSTANCE.readCharacteristic(domeDevice, domeCharList.get(domeCharSize - 2));
                    ConnectionManager.INSTANCE.readCharacteristic(domeDevice, domeCharList.get(domeCharSize - 3));

                    mainHandler.post(() -> {
                        viewModel.setTemperatureReading(domeSensorArr[0].floatValue() / 10); // Humidity is stored as 3 digits, so it can be divided by 10 to get true float value
                        viewModel.setHumidityReading(domeSensorArr[1]);
                        viewModel.setVibrationReading(domeSensorArr[2]);
                    });
                }

                SystemClock.sleep(1000);
            }
        }).start();

    }

    // Navigation Item Select, handles when a new fragment is selected
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_information:
                navigationView.setCheckedItem(R.id.nav_information);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new InformationFragment()).commit();
                break;
            case R.id.nav_controller:
                navigationView.setCheckedItem(R.id.nav_controller);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ControllerFragment()).commit();
                break;
            case R.id.nav_sensor_information:
                navigationView.setCheckedItem(R.id.nav_sensor_information);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new SensorFragment()).commit();
                break;
            case R.id.nav_sensor_history:
                navigationView.setCheckedItem(R.id.nav_sensor_history);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new HistoryFragment()).commit();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // Create menu bar (3 dots, top right)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_board_selection, menu); // Inflate menu layout
        return true;
    }

    /** Actions for when a menu bar option is chosen
     * @param item item/option on menu, use getItemId()
     * @return returns true
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){ // Switch on item id
            case R.id.config_robot: // If "Configure Robot" option
                Intent robotIntent = new Intent(MainActivity.this, ScanActivity.class);
                startActivityForResult(robotIntent, RESULT_ROBOT_DEVICE_NAME); // Start ScanActivity to pair w/BLE Device and return device object (feather in robot)
                break;
            case R.id.config_dome: // If "Configure Dome" option
                Intent domeIntent = new Intent(MainActivity.this, ScanActivity.class);
                startActivityForResult(domeIntent, RESULT_DOME_DEVICE_NAME); // Start ScanActivity to pair w/BLE Device and return device object (feather on dome)
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    /** Handle results from startActivityForResult (from: onOptionsItemSelected, )
     * @param requestCode request code returned
     * @param resultCode result code returned (RESULT_OK, RESULT_CANCELLED, RESULT_FIRST_USER)
     * @param data Copy of previous Activity's Intent, usually use getExtra() to get data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) { // If result code was OK
            switch (requestCode) { // Switch on request code
                case RESULT_ROBOT_DEVICE_NAME: // If robot device request code
                    try {
                        Timber.tag("ScanActivity Result").d("Trying to get Device");
                        if (data != null) {
                            robotDevice = data.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE); // Assign robot BLE Device
                            robotCharList = TurtleListeners.Companion.getCharacteristicsList(robotDevice);
                            robotCharSize = robotCharList.size();
                            ConnectionManager.INSTANCE.registerListener(TurtleListeners.Companion.robotListener(mContext, robotDevice)); // Register BLE Listener
                            isRobotConfigured = true;
                            Toast.makeText(this, "Robot Configured", Toast.LENGTH_SHORT).show(); // Alert user if pairing was successful
                        }
                    } catch (IllegalStateException e) {
                        Timber.tag("ScanActivity Result").e("getExtra was null");
                        Toast.makeText(this, "Pairing was not successful", Toast.LENGTH_SHORT).show(); // Alert user if paring wasn't successful
                    }
                    break;
                case RESULT_DOME_DEVICE_NAME:
                    try {
                        Timber.tag("ScanActivity Result").d("Trying to get Device");
                        if (data != null) {
                            domeDevice = data.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE); // Assign dome BLE device
                            domeCharList = TurtleListeners.Companion.getCharacteristicsList(domeDevice);
                            domeCharSize = domeCharList.size();
                            ConnectionManager.INSTANCE.registerListener(TurtleListeners.Companion.domeListener(mContext, domeDevice, domeSensorArr, domeAverageSensorArr));
                            isDomeConfigured = true;
                            Toast.makeText(this, "Dome Configured", Toast.LENGTH_SHORT).show(); // Alert user if pairing was successful
                        }
                    } catch (IllegalStateException e) {
                        Timber.tag("ScanActivity Result").e("getExtra was null");
                        Toast.makeText(this, "Pairing was not successful", Toast.LENGTH_SHORT).show(); // Alert user if paring wasn't successful
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
