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
    public static final String ACTION_SET_PLANT_DEAD = "com.example.android.virtualpot.action.SET_PLANT_DEAD";
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
    public static void startActionUpdatePlant(Context context) {
        Intent intent = new Intent(context, PlantWateringService.class);
        intent.setAction(ACTION_UPDATE_PLANT);
        context.startService(intent);
    }

    public static void startActionSetPlantDead (Context context, long plantId) {
        Intent intent = new Intent(context, PlantWateringService.class);
        intent.setAction(ACTION_SET_PLANT_DEAD);
        intent.putExtra(EXTRA_PLANT_ID, plantId);
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
            else if (ACTION_UPDATE_PLANT.equals(action)) {
                handleActionGetPlantImgRes();
            }
            else if(ACTION_SET_PLANT_DEAD.equals(action)){
                final long plantId = intent.getLongExtra(EXTRA_PLANT_ID,
                        PlantContract.INVALID_PLANT_ID);
                handleActionSetPlantDead(plantId);
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
        if(cursor.getInt(cursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_IS_DEAD))!=0)
            return; // plant already dead

        ContentValues contentValues = new ContentValues();
        long timeNow = System.currentTimeMillis();
        // Update the watered timestamp
        contentValues.put(PlantContract.PlantEntry.COLUMN_LAST_WATERED_TIME, timeNow);
        getContentResolver().update(SINGLE_PLANT_URI, contentValues, null, null);
        handleActionGetPlantImgRes();
    }

    /**
     * Handle action SetPlantDead in the provided background thread with the provided
     * parameters.
     */
    private void handleActionSetPlantDead(long plantId) {
        //TODO: need to call this in the right update time when plants die
        Uri SINGLE_PLANT_URI = ContentUris.withAppendedId(
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLANTS).build(), plantId);
        ContentValues contentValues = new ContentValues();
        // Update the "is_dead" col
        contentValues.put(PlantContract.PlantEntry.COLUMN_IS_DEAD, true);
        getContentResolver().update(SINGLE_PLANT_URI, contentValues, null, null);
        handleActionGetPlantImgRes();
    }


    private void handleActionGetPlantImgRes() {
        //TODO: do i need to keep doing this on each update?
        Uri PLANT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLANTS).build();
        Cursor cursor = getContentResolver().query(
                PLANT_URI,
                null,
                null,
                null,
                PlantContract.PlantEntry.COLUMN_LAST_WATERED_TIME
        );
        if(cursor == null || cursor.getCount()==0) return;
        cursor.moveToFirst();
        int idIndex = cursor.getColumnIndex(PlantContract.PlantEntry._ID);
        int createTimeIndex = cursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_CREATION_TIME);
        int waterTimeIndex = cursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_LAST_WATERED_TIME);
        int plantTypeIndex = cursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_PLANT_TYPE);

        long plantId = cursor.getLong(idIndex);
        int plantType = cursor.getInt(plantTypeIndex);
        long createdAt = cursor.getLong(createTimeIndex);
        long wateredAt = cursor.getLong(waterTimeIndex);
        cursor.close();

        long timeNow = System.currentTimeMillis();

        int imgRes = PlantUtils.getPlantImageRes(this,timeNow-createdAt, timeNow-wateredAt,plantType);
        boolean inDanger = ((timeNow-wateredAt) > PlantUtils.DANGER_AGE_WITHOUT_WATER);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] widgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,PlantWidget.class));
        PlantWidget.updatePlantWidgets(this, appWidgetManager, imgRes, plantId, inDanger, widgetIds);

    }
}
