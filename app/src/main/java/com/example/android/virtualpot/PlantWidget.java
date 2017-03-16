package com.example.android.virtualpot;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class PlantWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        //Start the intent service update action, the service takes care of updating the widgets UI
        PlantWateringService.startActionUpdatePlantWidgets(context);

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

    private static RemoteViews getSinglePlantRemoteView(Context context, int imgRes, long plantId, boolean showWater){
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
        waterIntent.putExtra(PlantWateringService.EXTRA_PLANT_ID, plantId);
        //waterIntent.putExtra(PlantWateringService.EXTRA_WIDGET_ID, widgetIds);
        PendingIntent wateringPendingIntent = PendingIntent.getService(context, 0, waterIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.water_button, wateringPendingIntent);
        //update image
        views.setImageViewResource(R.id.plant_image, imgRes);
        if(showWater) views.setViewVisibility(R.id.water_button, View.VISIBLE);
        else views.setViewVisibility(R.id.water_button, View.INVISIBLE);
        return views;
    }

    private static RemoteViews getGardenStackRemoteView(Context context){
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.garden_stack_view);
        // set the StackWidgetService intent to act as the adapter for the stack view
        Intent intent = new Intent(context, StackWidgetService.class);
        views.setRemoteAdapter(R.id.stack_view, intent);

        Intent appIntent = new Intent(context, MainActivity.class);
        PendingIntent appPendingIntent = PendingIntent.getActivity(context, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.stack_view, appPendingIntent);

        views.setEmptyView(R.id.stack_view, R.id.empty_view);

        return views;
    }

    /**
     * Updates a specific widget instance given the corresponding widget Id
     *
     * @param context          The calling context
     * @param appWidgetManager The widget manager
     * @param imgRes           The image resource for the plant ImageView
     * @param plantId          The database ID for that plant
     * @param widgetIds        The Id of the widget to be updated
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void updatePlantWidgets(Context context, AppWidgetManager appWidgetManager,
                                          int imgRes, long plantId, boolean showWater, int[] widgetIds) {

        for (int widgetId : widgetIds) {

            // Get min width to decide on single plant vs garden stack view
            Bundle options = appWidgetManager.getAppWidgetOptions(widgetId);
            int width = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);

            RemoteViews rv;
            if(width < 70 ) {
                rv = getSinglePlantRemoteView(context, imgRes, plantId, showWater);
            }
            else {
                rv = getGardenStackRemoteView(context);
                //refresh data in case new plants added/removed or watered
                //appWidgetManager.notifyAppWidgetViewDataChanged(widgetId,R.id.stack_view);
            }

            // Obtain appropriate widget and update it.
            appWidgetManager.updateAppWidget(widgetId, rv);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager,
                                          int appWidgetId, Bundle newOptions) {

        PlantWateringService.startActionUpdatePlantWidgets(context);
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }



}

