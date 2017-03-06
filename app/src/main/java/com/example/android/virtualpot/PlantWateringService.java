package com.example.android.virtualpot;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Date;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class PlantWateringService extends IntentService {
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String ACTION_WATER_PLANT = "com.example.android.virtualpot.action.WATER_PLANT";
    public static final String EXTRA_WIDGET_ID = "com.example.android.virtualpot.extra.WIDGET_ID";

    public PlantWateringService() {
        super("PlantWateringService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionWaterPlant(Context context, int widgetId) {
        Intent intent = new Intent(context, PlantWateringService.class);
        intent.setAction(ACTION_WATER_PLANT);
        intent.putExtra(EXTRA_WIDGET_ID, widgetId);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(this.getClass().getSimpleName(),"onHandleIntent");
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_WATER_PLANT.equals(action)) {
                Log.d(this.getClass().getSimpleName(),"action correct");
                final int widgetId = intent.getIntExtra(EXTRA_WIDGET_ID,
                        AppWidgetManager.INVALID_APPWIDGET_ID);
                Log.d(this.getClass().getSimpleName(),"widgetId="+widgetId);
                handleActionWaterPlant(widgetId);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionWaterPlant(int widgetId) {
        Log.d(this.getClass().getSimpleName(),"saving water time");
        SharedPrefUtils.saveWaterTime(this, widgetId, new Date());
    }
}
