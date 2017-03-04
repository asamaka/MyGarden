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

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = PlantWidgetConfigureActivity.loadTitlePref(context, appWidgetId);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.plant_widget);
        views.setTextViewText(R.id.appwidget_text, widgetText);
        // Set the click handler to open the configuration activity
        Intent configIntent = new Intent(context, PlantWidgetConfigureActivity.class);
        Uri data = Uri.parse(URI_SCHEME + "://widget/id/"+appWidgetId);
        configIntent.setData(data);
        configIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, configIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.appwidget_image, pendingIntent);

        //update image if plant is old enough
        Date createdAt = PlantWidgetConfigureActivity.loadStartTimePref(context,appWidgetId);
        long mills = new Date().getTime() - createdAt.getTime();
        long hours = mills/(1000 * 60 * 60);
        if(hours>3) {
            Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.pot_3);
            views.setImageViewBitmap(R.id.appwidget_image, icon);
        }else if(hours>2) {
            Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.pot_2);
            views.setImageViewBitmap(R.id.appwidget_image, icon);
        }else if(hours>1) {
            Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.pot_1);
            views.setImageViewBitmap(R.id.appwidget_image, icon);
        }
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

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
            PlantWidgetConfigureActivity.deleteTitlePref(context, appWidgetId);
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
}

