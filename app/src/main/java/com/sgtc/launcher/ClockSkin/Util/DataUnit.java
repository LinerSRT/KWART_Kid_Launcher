package com.sgtc.launcher.ClockSkin.Util;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.sgtc.launcher.Config;
import com.sgtc.launcher.util.PM;


public class DataUnit {
    public static int steps;
    public static int targetSteps;
    public static int calories;
    public static int targetCalories;
    public static int distance;
    public static int targetDistance;
    public static int weatherTemp;
    public static int weatherIcon;
    public static int batteryLevel;
    public static boolean isCharging;

    public static void tick(Context context) {
        steps = PM.get(Config.CURRENT_STEPS, 0);
        targetSteps = PM.get(Config.TARGET_STEPS, 0);
        calories = PM.get(Config.CALORIES, 0);
        targetCalories = PM.get(Config.TARGET_CALORIES, 0);
        distance = PM.get(Config.DISTANCE, 0);
        targetDistance = PM.get(Config.TARGET_DISTANCE, 0);
        weatherIcon = PM.get(Config.WEATHER_ICON, 0);
        weatherTemp = PM.get(Config.WEATHER_TEMP, 0);
        Intent batteryStatus = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        if (batteryStatus != null) {
            int status = batteryStatus.getIntExtra("status", -1);
            isCharging = status == 2 || status == 5;
            batteryLevel = batteryStatus.getIntExtra("level", -1);
        } else {
            isCharging = false;
            batteryLevel = 50;
        }

    }

}
