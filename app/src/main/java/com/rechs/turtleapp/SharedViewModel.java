package com.rechs.turtleapp;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    // LiveData that will keep track of directionNum
    private MutableLiveData<Integer> directionNum = new MutableLiveData<>();

    // LiveData that will keep track of electromagnet things
    private MutableLiveData<Integer> electromagnetPowerNum = new MutableLiveData<>();
    private MutableLiveData<Integer> electromagnetDirectionNum = new MutableLiveData<>();

    // Live Data that will keep track of sensor things
    private MutableLiveData<Float> temperatureReading = new MutableLiveData<>();
    private MutableLiveData<Integer> humidityReading = new MutableLiveData<>();
    private MutableLiveData<Integer> vibrationReading = new MutableLiveData<>();

    /**
     * Direction Things
     */

    public void setDirectionNum(Integer input) {
        directionNum.setValue(input);
    }

    public LiveData<Integer> getDirectionNum() {
        return directionNum;
    }




    /**
     * Electromagnet Things
     */

    public void setElectromagnetToggleNum(Integer input) {electromagnetPowerNum.setValue(input);}

    public LiveData<Integer> getElectromagnetPowerNum() {return electromagnetPowerNum;}


    public void setElectromagnetDirectionNum(Integer input) {electromagnetDirectionNum.setValue(input);}

    public LiveData<Integer> getElectromagnetDirectionNum() {return electromagnetDirectionNum;}




    /**
     * Sensor Things
     */

    public void setTemperatureReading(Float input) {temperatureReading.setValue(input);}

    public LiveData<Float> getTemperatureReading() {return temperatureReading;}


    public void setHumidityReading(Integer input) {humidityReading.setValue(input);}

    public LiveData<Integer> getHumidityReading() {return humidityReading;}


    public void setVibrationReading(Integer input) {vibrationReading.setValue(input);}

    public LiveData<Integer> getVibrationReading() {return vibrationReading;}
}
