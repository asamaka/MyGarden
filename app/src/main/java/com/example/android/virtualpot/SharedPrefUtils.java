package com.example.android.virtualpot;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Date;

public class SharedPrefUtils {

    private static final String PREFS_NAME = "com.example.android.virtualpot.PlantWidget";
    private static final String PREF_PREFIX_CREATED = "created_";
    private static final String PREF_PREFIX_WATERED = "watered_";


    static void saveStartTime(Context context, int appWidgetId, long currentTimeMills) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putLong(PREF_PREFIX_CREATED + appWidgetId, currentTimeMills);
        prefs.apply();
    }

    static void saveWaterTime(Context context, int appWidgetId, long currentTimeMills) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putLong(PREF_PREFIX_WATERED + appWidgetId, currentTimeMills);
        prefs.apply();
    }

    static long loadStartTime(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getLong(PREF_PREFIX_CREATED + appWidgetId, 0);
    }

    static long loadWaterTime(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getLong(PREF_PREFIX_WATERED + appWidgetId, 0);
    }

    static void deleteAll(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_CREATED + appWidgetId);
        prefs.remove(PREF_PREFIX_WATERED + appWidgetId);
        prefs.apply();
    }
}
