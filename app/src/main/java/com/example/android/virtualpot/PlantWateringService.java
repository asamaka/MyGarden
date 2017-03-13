package com.example.android.virtualpot;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.example.android.virtualpot.provider.PlantContract;

import static com.example.android.virtualpot.provider.PlantContract.BASE_CONTENT_URI;
import static com.example.android.virtualpot.provider.PlantContract.PATH_PLANTS;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class PlantWateringService extends IntentService {
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String ACTION_WATER_PLANT = "com.example.android.virtualpot.action.WATER_PLANT";
    public static final String ACTION_UPDATE_PLANT = "com.example.android.virtualpot.action.UPDATE_PLANT";
    public static final String EXTRA_WIDGET_ID = "com.example.android.virtualpot.extra.WIDGET_ID";
    public static final String EXTRA_PLANT_ID = "com.example.android.virtualpot.extra.PLANT_ID";

    public PlantWateringService() {
        super("PlantWateringService");
    }

    /**
     * Starts this service to perform action with the given parameters. If
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

    /**
     * Starts this service to perform action with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionUpdatePlant(Context context, int[] widgetIds) {
        Intent intent = new Intent(context, PlantWateringService.class);
        intent.setAction(ACTION_UPDATE_PLANT);
        intent.putExtra(EXTRA_WIDGET_ID, widgetIds);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(this.getClass().getSimpleName(), "onHandleIntent");
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_WATER_PLANT.equals(action)) {
                final int widgetId = intent.getIntExtra(EXTRA_WIDGET_ID,
                        AppWidgetManager.INVALID_APPWIDGET_ID);
                handleActionWaterPlant(widgetId);
            }
            else if (ACTION_UPDATE_PLANT.equals(action)) {
                final int[] widgetIds = intent.getIntArrayExtra(EXTRA_WIDGET_ID);
                handleActionGetPlantImgRes(widgetIds);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionWaterPlant(int plantId) {
        //TODO: update watered time in database

        //TODO: update all widgets to reflect the water action

        // It is the responsibility of the configuration activity to update the app widget

    }


    private void handleActionGetPlantImgRes(int[] widgetIds) {
        Log.d(this.getClass().getSimpleName(),"handleActionGetPlantImgRes");
        Uri PLANT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLANTS).build();
        Cursor cursor = getContentResolver().query(
                PLANT_URI,
                null,
                null,
                null,
                PlantContract.PlantEntry.COLUMN_LAST_WATERED_TIME
        );
        if(cursor == null || cursor.getCount()==0) return;
        Log.d(this.getClass().getSimpleName(),"cursor has something");
        cursor.moveToFirst();
        int idIndex = cursor.getColumnIndex(PlantContract.PlantEntry._ID);
        int createTimeIndex = cursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_CREATION_TIME);
        int waterTimeIndex = cursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_LAST_WATERED_TIME);
        int plantTypeIndex = cursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_PLANT_TYPE);

        long plantId = cursor.getLong(idIndex);
        int plantType = cursor.getInt(plantTypeIndex);
        long createdAt = cursor.getLong(createTimeIndex);
        long wateredAt = cursor.getLong(waterTimeIndex);
        long timeNow = System.currentTimeMillis();

        int imgRes = PlantUtils.getPlantImageRes(this,timeNow-createdAt, timeNow-wateredAt,plantType);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        Log.d(this.getClass().getSimpleName(),"img res = "+imgRes);
        for (int widgetId : widgetIds) {
            PlantWidget.updatePlantWidget(this, appWidgetManager, imgRes, plantId, widgetId);
        }
    }
}
