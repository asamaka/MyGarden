package com.example.android.virtualpot;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Date;

/**
 * The configuration screen for the {@link PlantWidget PlantWidget} AppWidget.
 */
public class PlantWidgetConfigureActivity extends Activity {
    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private TextView mAppWidgetCreatedText;
    private TextView mAppWidgetWateredText;
    private Button mAppWidgetAddButton;

    public PlantWidgetConfigureActivity() {
        super();
    }


    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.plant_widget_configure);
        mAppWidgetAddButton = (Button) findViewById(R.id.add_button);
        mAppWidgetCreatedText = (TextView) findViewById(R.id.created_time_text);
        mAppWidgetWateredText = (TextView) findViewById(R.id.watered_time_text);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        Date createdAt = SharedPrefUtils.loadStartTime(this, mAppWidgetId);
        if (createdAt != null) {
            mAppWidgetCreatedText.setText(createdAt.toString());
            mAppWidgetAddButton.setText(getString(R.string.reset_widget));
        }

        Date wateredAt = SharedPrefUtils.loadWaterTime(this, mAppWidgetId);
        if (wateredAt != null) {
            mAppWidgetWateredText.setText(wateredAt.toString());
        }
    }

    public void onAddButtonClicked(View view) {

        // When the button is clicked, create a new plane and set the start time
        SharedPrefUtils.saveStartTime(this, mAppWidgetId, new Date());

        // It is the responsibility of the configuration activity to update the app widget
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        PlantWidget.updateAppWidget(this, appWidgetManager, mAppWidgetId);

        // Make sure we pass back the original appWidgetId
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }

    public void onCancelButtonClicked(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }
}

