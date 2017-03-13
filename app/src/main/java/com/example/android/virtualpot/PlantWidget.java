package com.example.android.virtualpot;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class PlantWidget extends AppWidgetProvider {

    private static String URI_SCHEME = "widget_uri";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        //Start the intent service update action, the service takes care of updating the widgets UI
        PlantWateringService.startActionUpdatePlant(context, appWidgetIds);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {

    }

    @Override
    public void onEnabled(Context context) {
    }

    @Override
    public void onDisabled(Context context) {
    }

    /**
     * Updates a specific widget instance given the corresponding widget Id
     * @param context           The calling context
     * @param appWidgetManager  The widget manager
     * @param imgRes            The image resource for the plant ImageView
     * @param plantId           The database ID for that plant
     * @param widgetIds          The Id of the widget to be updated
     */
    public static void updatePlantWidgets(Context context, AppWidgetManager appWidgetManager,
                                         int imgRes, long plantId, int[] widgetIds) {
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.plant_widget);

        // Set the click handler to open the main activity
        Intent appIntent = new Intent(context, MainActivity.class);
        PendingIntent appPendingIntent = PendingIntent.getActivity(context, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.plant_image, appPendingIntent);

        // Set the click handler to water the plant
        // Usually you would set the button image tag to the plant ID but since widgets can only deal
        // with pending intents for click events anyway, so it makes sense to set the plant ID as an extra
        Intent waterIntent = new Intent(context, PlantWateringService.class);
        waterIntent.setAction(PlantWateringService.ACTION_WATER_PLANT);
        Log.d(PlantWidget.class.getSimpleName(),"plantId = "+plantId);
        waterIntent.putExtra(PlantWateringService.EXTRA_PLANT_ID, plantId);
        waterIntent.putExtra(PlantWateringService.EXTRA_WIDGET_ID, widgetIds);
        PendingIntent wateringPendingIntent = PendingIntent.getService(context, 0, waterIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.water_button, wateringPendingIntent);
        //update image
        views.setImageViewResource(R.id.plant_image, imgRes);
        // Instruct the widget manager to update the widgets
        for (int widgetId : widgetIds) {
            appWidgetManager.updateAppWidget(widgetId, views);
        }
    }
}

