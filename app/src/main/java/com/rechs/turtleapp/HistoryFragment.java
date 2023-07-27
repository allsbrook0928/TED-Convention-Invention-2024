package com.rechs.turtleapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryFragment extends Fragment {
    private SharedPreferences sp;



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HistoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HistoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HistoryFragment newInstance(String param1, String param2) {
        HistoryFragment fragment = new HistoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sp = requireActivity().getSharedPreferences("AppData", Context.MODE_PRIVATE); // Initialize shared preferences


        LineChart dailyTemperatureLineChart;
        ArrayList<Entry> dailyTemperatureLineList;
        LineDataSet dailyTemperatureLineDataSet;
        LineData dailyTemperatureLineData;

        LineChart dailyHumidityLineChart;
        ArrayList<Entry> dailyHumidityLineList;
        LineDataSet dailyHumidityLineDataSet;
        LineData dailyHumidityLineData;

        LineChart weeklyTemperatureLineChart;
        ArrayList<Entry> weeklyTemperatureLineList;
        LineDataSet weeklyTemperatureLineDataSet;
        LineData weeklyTemperatureLineData;

        LineChart weeklyHumidityLineChart;
        ArrayList<Entry> weeklyHumidityLineList;
        LineDataSet weeklyHumidityLineDataSet;
        LineData weeklyHumidityLineData;



        /** Initialize/link views **/
        dailyTemperatureLineChart = view.findViewById(R.id.linechart_temperature_daily_average);
        dailyHumidityLineChart = view.findViewById(R.id.linechart_humidity_daily_average);
        weeklyTemperatureLineChart = view.findViewById(R.id.linechart_temperature_weekly_average);
        weeklyHumidityLineChart = view.findViewById(R.id.linechart_humidity_weekly_average);


        /**** Setup daily temperature graph *****/
        dailyTemperatureLineList = setDailyLineList("temperature"); // Get List of entries
        ArrayList<Entry> tempDailyList = new ArrayList<>();
        tempDailyList.add(new Entry(1f, 72f));
        tempDailyList.add(new Entry(2f, 69f));
        tempDailyList.add(new Entry(3f, 79f));
        tempDailyList.add(new Entry(4f, 81f));
        tempDailyList.add(new Entry(5f, 75f));
        tempDailyList.add(new Entry(6f, 71f));
        tempDailyList.add(new Entry(7f, 66f));
        dailyTemperatureLineDataSet = new LineDataSet(tempDailyList, "Average Temperature (°F)");
        //dailyTemperatureLineDataSet = new LineDataSet(dailyTemperatureLineList, "Average Temperature (°F)");

        dailyTemperatureLineData = new LineData(dailyTemperatureLineDataSet); // Set line data

        dailyTemperatureLineChart.setData(dailyTemperatureLineData); // Pass data to chart
        setupLineChart(dailyTemperatureLineChart, dailyTemperatureLineDataSet);
        dailyTemperatureLineChart.getAxisLeft().setAxisMaximum(120f);

        dailyTemperatureLineChart.invalidate();


        /**** Setup daily humidity graph ****/
        dailyHumidityLineList = setDailyLineList("humidity");
        ArrayList<Entry> humDailyList = new ArrayList<>();
        humDailyList.add(new Entry(1f, 26f));
        humDailyList.add(new Entry(2f, 35f));
        humDailyList.add(new Entry(3f, 31f));
        humDailyList.add(new Entry(4f, 40f));
        humDailyList.add(new Entry(5f, 37f));
        humDailyList.add(new Entry(6f, 29f));
        humDailyList.add(new Entry(7f, 25f));
        dailyHumidityLineDataSet = new LineDataSet(humDailyList, "Average Humidity (%)");

        //dailyHumidityLineDataSet = new LineDataSet(dailyHumidityLineList, "Average Humidity (%)");

        dailyHumidityLineData = new LineData(dailyHumidityLineDataSet);

        dailyHumidityLineChart.setData(dailyHumidityLineData);
        setupLineChart(dailyHumidityLineChart, dailyHumidityLineDataSet);

        dailyHumidityLineDataSet.setColor(Color.rgb(10, 120, 235));
        dailyHumidityLineDataSet.setCircleColor(Color.rgb(0, 115, 230));
        dailyHumidityLineDataSet.setFillColor(Color.rgb(65, 150, 240));

        dailyHumidityLineChart.getAxisLeft().setAxisMaximum(100f);

        dailyHumidityLineChart.invalidate();


        /**** Setup weekly temperature graph ****/
        weeklyTemperatureLineList = setWeeklyLineList("temperature");
        ArrayList<Entry> tempWeeklyList = new ArrayList<>();
        tempWeeklyList.add(new Entry(1f, 71f));
        tempWeeklyList.add(new Entry(2f, 68f));
        tempWeeklyList.add(new Entry(3f, 66f));
        tempWeeklyList.add(new Entry(4f, 69f));
        weeklyTemperatureLineDataSet = new LineDataSet(tempWeeklyList, "Average Temperature (°F)");


        //weeklyTemperatureLineDataSet = new LineDataSet(weeklyTemperatureLineList, "Average Temperature (°F)");

        weeklyTemperatureLineData = new LineData(weeklyTemperatureLineDataSet);

        weeklyTemperatureLineChart.setData(weeklyTemperatureLineData);
        setupLineChart(weeklyTemperatureLineChart, weeklyTemperatureLineDataSet);
        weeklyTemperatureLineChart.getAxisLeft().setAxisMaximum(120f);

        weeklyTemperatureLineChart.getXAxis().setValueFormatter(new WeeklyXAxisFormatter());
        weeklyTemperatureLineChart.invalidate();


        /**** Setup weekly humidity graph ****/
        weeklyHumidityLineList = setWeeklyLineList("humidity");
        ArrayList<Entry> humWeeklyList = new ArrayList<>();
        humWeeklyList.add(new Entry(1f, 35f));
        humWeeklyList.add(new Entry(2f, 33f));
        humWeeklyList.add(new Entry(3f, 28f));
        humWeeklyList.add(new Entry(4f, 29f));
        weeklyHumidityLineDataSet = new LineDataSet(humWeeklyList, "Average Humidity (%)");


        //weeklyHumidityLineDataSet = new LineDataSet(weeklyHumidityLineList, "Average Humidity (%)");

        weeklyHumidityLineData = new LineData(weeklyHumidityLineDataSet);

        weeklyHumidityLineChart.setData(weeklyHumidityLineData);
        setupLineChart(weeklyHumidityLineChart, weeklyHumidityLineDataSet);

        weeklyHumidityLineDataSet.setColor(Color.rgb(10, 120, 235));
        weeklyHumidityLineDataSet.setCircleColor(Color.rgb(0, 115, 230));
        weeklyHumidityLineDataSet.setFillColor(Color.rgb(65, 150, 240));

        weeklyHumidityLineChart.getAxisLeft().setAxisMaximum(100f);

        weeklyHumidityLineChart.getXAxis().setValueFormatter(new WeeklyXAxisFormatter());
        weeklyHumidityLineChart.invalidate();


    }

    private ArrayList<Entry> setDailyLineList(String name) {
        ArrayList<Entry> list = new ArrayList<>();

        list.add(new Entry(1f, sp.getFloat(name + "sundayAverage", 0)));
        list.add(new Entry(2f, sp.getFloat(name + "mondayAverage", 0)));
        list.add(new Entry(3f, sp.getFloat(name + "tuesdayAverage", 0)));
        list.add(new Entry(4f, sp.getFloat(name + "wednesdayAverage", 0)));
        list.add(new Entry(5f, sp.getFloat(name + "thursdayAverage", 0)));
        list.add(new Entry(6f, sp.getFloat(name + "fridayAverage", 0)));
        list.add(new Entry(7f, sp.getFloat(name + "saturdayAverage", 0)));

        return list;
    }

    private ArrayList<Entry> setWeeklyLineList(String name) {
        ArrayList<Entry> list = new ArrayList<>();

        list.add(new Entry(1f, sp.getFloat(name + "week1Average", 0)));
        list.add(new Entry(2f, sp.getFloat(name + "week2Average", 0)));
        list.add(new Entry(3f, sp.getFloat(name + "week3Average", 0)));
        list.add(new Entry(4f, sp.getFloat(name + "week4Average", 0)));

        return list;
    }

    private void setupLineChart(LineChart chart, LineDataSet dataSet) {
        dataSet.setColor(Color.rgb(240,80,20));
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(10f);
        dataSet.setLineWidth(5f);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.rgb(240, 100, 50));
        dataSet.setCircleColor(Color.rgb(230, 75, 15));


        chart.getAxisRight().setEnabled(false);
        chart.getDescription().setEnabled(false);

        chart.getXAxis().setValueFormatter(new DailyXAxisFormatter());

        chart.getAxisLeft().setAxisMinimum(0f);

        chart.setScaleEnabled(false);
        chart.setDoubleTapToZoomEnabled(false);

        chart.setBackgroundColor(Color.WHITE);

        chart.setBorderColor(Color.BLACK);
    }

    private static class DailyXAxisFormatter extends ValueFormatter {
        private final String[] days = {"", "Sun", "Mon", "Tue", "Wed", "Thr", "Fri", "Sat"};

        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            if ((int) value >= days.length) {
                return String.format(Locale.US, "%d", (int) value);
            } else {
                return days[(int) value];
            }
        }
    }

    private static class WeeklyXAxisFormatter extends ValueFormatter {
        private final String[] weeks = {"", "Week 1", "Week 2", "Week 3", "Week 4"};

        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            if ((int) value >= weeks.length || value != (int) value) {
                return String.format(Locale.US, "");
            } else {
                return weeks[(int) value];
            }
        }
    }

}