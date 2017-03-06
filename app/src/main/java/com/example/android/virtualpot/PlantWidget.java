package com.example.android.virtualpot;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.RemoteViews;

import java.util.Date;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link PlantWidgetConfigureActivity PlantWidgetConfigureActivity}
 */
public class PlantWidget extends AppWidgetProvider {

    private static String URI_SCHEME = "widget_uri";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            SharedPrefUtils.deleteAll(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }


    /**
     * Updates a specific widget instance given the corresponding widget Id
     *
     * @param context          The calling context
     * @param appWidgetManager The widget manager
     * @param appWidgetId      The Id of the widget to be updated
     */
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.plant_widget);

        // Set the click handler to open the configuration activity
        Intent configIntent = new Intent(context, PlantWidgetConfigureActivity.class);
        Uri data = Uri.parse(URI_SCHEME + "://widget/id/" + appWidgetId);
        configIntent.setData(data);
        configIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        PendingIntent configPendingIntent = PendingIntent.getActivity(context, 0, configIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.pot_image, configPendingIntent);

        // Set the click handler to water the plant
        Intent waterIntent = new Intent(context, PlantWateringService.class);
        waterIntent.setAction(PlantWateringService.ACTION_WATER_PLANT);
        waterIntent.putExtra(PlantWateringService.EXTRA_WIDGET_ID, appWidgetId);
        PendingIntent wateringPendingIntent = PendingIntent.getService(context, 0, waterIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.cloud_image, wateringPendingIntent);


        //update image if plant is old enough
        Date createdAt = SharedPrefUtils.loadStartTime(context, appWidgetId);
        if (createdAt != null) {
            long mills = new Date().getTime() - createdAt.getTime();
            Bitmap image = BitmapFactory.decodeResource(context.getResources(),
                    PlantUtils.getImageResourceByAge(mills));
            views.setImageViewBitmap(R.id.pot_image, image);
        }
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}

