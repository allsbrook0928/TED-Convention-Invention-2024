package com.rechs.turtleapp;

import android.content.SharedPreferences;

public class SensorLog {
    // Instance Variables
    final private SharedPreferences SP;
    final private String NAME;

    private float sundayAverage;
    private float mondayAverage;
    private float tuesdayAverage;
    private float wednesdayAverage;
    private float thursdayAverage;
    private float fridayAverage;
    private float saturdayAverage;

    private float week1Average;
    private float week2Average;
    private float week3Average;
    private float week4Average;

    /**** Constructor ****/
    public SensorLog(SharedPreferences s, String n) {
        // Initialize instance variables
        SP = s;
        NAME = n;

        sundayAverage = SP.getFloat(NAME + "sundayAverage", 0);
        mondayAverage = SP.getFloat(NAME + "mondayAverage", 0);
        tuesdayAverage = SP.getFloat(NAME + "tuesdayAverage", 0);
        wednesdayAverage = SP.getFloat(NAME + "wednesdayAverage", 0);
        thursdayAverage = SP.getFloat(NAME + "thursdayAverage", 0);
        fridayAverage = SP.getFloat(NAME + "fridayAverage", 0);
        saturdayAverage = SP.getFloat(NAME + "saturdayAverage", 0);

        week1Average = SP.getFloat(NAME + "week1Average", 0);
        week2Average = SP.getFloat(NAME + "week2Average", 0);
        week3Average = SP.getFloat(NAME + "week3Average", 0);
        week4Average = SP.getFloat(NAME + "week4Average", 0);
    }

    /**** Getters ****/
    public float getDayAverage(int day) {
        switch (day) {
            case 1:
                return sundayAverage;
            case 2:
                return mondayAverage;
            case 3:
                return tuesdayAverage;
            case 4:
                return wednesdayAverage;
            case 5:
                return thursdayAverage;
            case 6:
                return fridayAverage;
            case 7:
                return saturdayAverage;
        }
        return 0;
    }

    public float getWeekAverage(int week) {
        switch (week) {
            case 1:
                return week1Average;
            case 2:
                return week2Average;
            case 3:
                return week3Average;
            case 4:
                return week4Average;
        }
        return 0;
    }

    /**** Setters ****/
    public void setDayAverage(int day, float value) {
        switch (day) {
            case 1:
                sundayAverage = value;
                SP.edit().putFloat(NAME + "sundayAverage", sundayAverage).apply();
            case 2:
                this.mondayAverage = value;
                SP.edit().putFloat(NAME + "mondayAverage", mondayAverage).apply();
            case 3:
                this.tuesdayAverage = value;
                SP.edit().putFloat(NAME + "tuesdayAverage", tuesdayAverage).apply();
            case 4:
                this.wednesdayAverage = value;
                SP.edit().putFloat(NAME + "wednesdayAverage", wednesdayAverage).apply();
            case 5:
                this.thursdayAverage = value;
                SP.edit().putFloat(NAME + "thursdayAverage", thursdayAverage).apply();
            case 6:
                this.fridayAverage = value;
                SP.edit().putFloat(NAME + "fridayAverage", fridayAverage).apply();
            case 7:
                this.saturdayAverage = value;
                SP.edit().putFloat(NAME + "saturdayAverage", saturdayAverage).apply();
        }
    }

    public void setWeekAverage(int week) {
        switch (week) {
            case 1:
                week1Average = calculateWeeklyAverage();
                SP.edit().putFloat(NAME + "week1Average", week1Average).apply();
                break;
            case 2:
                week2Average = calculateWeeklyAverage();
                SP.edit().putFloat(NAME + "week2Average", week2Average).apply();
                break;
            case 3:
                week3Average = calculateWeeklyAverage();
                SP.edit().putFloat(NAME + "week3Average", week3Average).apply();
                break;
            case 4:
                week4Average = calculateWeeklyAverage();
                SP.edit().putFloat(NAME + "week4Average", week4Average).apply();
                break;
        }
    }


    /**** Other Methods ****/
    private float calculateWeeklyAverage() {
        float average =
                sundayAverage +
                mondayAverage +
                tuesdayAverage +
                wednesdayAverage +
                thursdayAverage +
                fridayAverage +
                saturdayAverage;

        return average / 7;
    }

    public static int previousDay(int day) {return day == 1 ? 7 : day - 1;}

    public static int previousWeek(int week) {return week == 1 ? 4 : week - 1;}

}
