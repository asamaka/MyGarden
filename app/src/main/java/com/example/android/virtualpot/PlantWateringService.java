package com.example.android.virtualpot;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import com.example.android.virtualpot.provider.PlantContract;

import static com.example.android.virtualpot.provider.PlantContract.BASE_CONTENT_URI;
import static com.example.android.virtualpot.provider.PlantContract.INVALID_PLANT_ID;
import static com.example.android.virtualpot.provider.PlantContract.PATH_PLANTS;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class PlantWateringService extends IntentService {
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String ACTION_WATER_PLANT = "com.example.android.virtualpot.action.water_plant";
    public static final String ACTION_UPDATE_PLANT_WIDGETS = "com.example.android.virtualpot.action.update_plant_widgets";
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
    public static void startActionWaterPlant(Context context, long plantId) {
        Intent intent = new Intent(context, PlantWateringService.class);
        intent.setAction(ACTION_WATER_PLANT);
        intent.putExtra(EXTRA_PLANT_ID, plantId);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionUpdatePlantWidgets(Context context) {
        Intent intent = new Intent(context, PlantWateringService.class);
        intent.setAction(ACTION_UPDATE_PLANT_WIDGETS);
        context.startService(intent);
    }

    /**
     *
     * @param intent
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_WATER_PLANT.equals(action)) {
                final long plantId = intent.getLongExtra(EXTRA_PLANT_ID,
                        PlantContract.INVALID_PLANT_ID);
                handleActionWaterPlant(plantId);
            }
            else if (ACTION_UPDATE_PLANT_WIDGETS.equals(action)) {
                handleActionUpdatePlantWidgets();
            }
        }
    }

    /**
     * Handle action WaterPlant in the provided background thread with the provided
     * parameters.
     */
    private void handleActionWaterPlant(long plantId) {
        //check if already dead then can't water
        Uri SINGLE_PLANT_URI = ContentUris.withAppendedId(
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLANTS).build(), plantId);
        Cursor cursor = getContentResolver().query(SINGLE_PLANT_URI,null,null,null,null);
        if(cursor == null || cursor.getCount()<1) return; //can't find this plant!
        cursor.moveToFirst();
        long lastWatered = cursor.getLong(cursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_LAST_WATERED_TIME));
        long timeNow = System.currentTimeMillis();
        if((timeNow-lastWatered)>PlantUtils.MAX_AGE_WITHOUT_WATER)
            return; // plant already dead

        ContentValues contentValues = new ContentValues();
        // Update the watered timestamp
        contentValues.put(PlantContract.PlantEntry.COLUMN_LAST_WATERED_TIME, timeNow);
        getContentResolver().update(SINGLE_PLANT_URI, contentValues, null, null);
        handleActionUpdatePlantWidgets();
    }

    private void handleActionUpdatePlantWidgets() {
        //Query to get the plant that's most in need for water (last watered)
        Uri PLANT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLANTS).build();
        Cursor cursor = getContentResolver().query(
                PLANT_URI,
                null,
                null,
                null,
                PlantContract.PlantEntry.COLUMN_LAST_WATERED_TIME
        );
        long plantId = INVALID_PLANT_ID;
        int plantType = 0;
        long createdAt = System.currentTimeMillis();
        long wateredAt = System.currentTimeMillis();
        long timeNow = System.currentTimeMillis();
        int imgRes = PlantUtils.getEmptyImgeRes();
        if(cursor != null && cursor.getCount()>0) {
            cursor.moveToFirst();
            int idIndex = cursor.getColumnIndex(PlantContract.PlantEntry._ID);
            int createTimeIndex = cursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_CREATION_TIME);
            int waterTimeIndex = cursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_LAST_WATERED_TIME);
            int plantTypeIndex = cursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_PLANT_TYPE);

            plantId = cursor.getLong(idIndex);
            plantType = cursor.getInt(plantTypeIndex);
            createdAt = cursor.getLong(createTimeIndex);
            wateredAt = cursor.getLong(waterTimeIndex);
            cursor.close();
            imgRes = PlantUtils.getPlantImageRes(this,timeNow-createdAt, timeNow-wateredAt,plantType);
        }

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] widgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,PlantWidget.class));
        //Trigger data update to handle the stackview widgets and force a data refresh
        appWidgetManager.notifyAppWidgetViewDataChanged(widgetIds,R.id.stack_view);
        //Now update all widgets
        boolean needWater = ((timeNow-wateredAt) > PlantUtils.MIN_AGE_BETWEEN_WATER);
        PlantWidget.updatePlantWidgets(this, appWidgetManager, imgRes, plantId, needWater, widgetIds);

    }
}
