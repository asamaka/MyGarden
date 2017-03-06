package com.example.android.virtualpot;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Date;

public class SharedPrefUtils {

    private static final String PREFS_NAME = "com.example.android.virtualpot.PlantWidget";
    private static final String PREF_PREFIX_CREATED = "created_";
    private static final String PREF_PREFIX_WATERED = "watered_";


    static void saveStartTime(Context context, int appWidgetId, Date date) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putLong(PREF_PREFIX_CREATED + appWidgetId, date.getTime());
        prefs.apply();
    }

    static void saveWaterTime(Context context, int appWidgetId, Date date) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putLong(PREF_PREFIX_WATERED + appWidgetId, date.getTime());
        prefs.apply();
    }

    static Date loadStartTime(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        long lastClicked = prefs.getLong(PREF_PREFIX_CREATED + appWidgetId, 0);
        if (lastClicked != 0) {
            return new Date(lastClicked);
        } else {
            return null;
        }
    }

    static Date loadWaterTime(Context context, int appWidgetId) {
        Log.d(SharedPrefUtils.class.getSimpleName(),"loading water time");
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        long timestamp = prefs.getLong(PREF_PREFIX_WATERED + appWidgetId, 0);
        Log.d(SharedPrefUtils.class.getSimpleName(),"timestamp="+timestamp);
        if (timestamp != 0) {
            return new Date(timestamp);
        } else {
            return null;
        }
    }

    static void deleteAll(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_CREATED + appWidgetId);
        prefs.remove(PREF_PREFIX_WATERED + appWidgetId);
        prefs.apply();
    }
}
