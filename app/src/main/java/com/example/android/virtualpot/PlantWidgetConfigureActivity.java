package com.example.android.virtualpot;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

/**
 * The configuration screen for the {@link PlantWidget PlantWidget} AppWidget.
 */
public class PlantWidgetConfigureActivity extends AppCompatActivity {
    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private TextView mAppWidgetCreatedText;
    private TextView mAppWidgetWateredText;
    private TextView mAppWidgetCreatedLabel;
    private TextView mAppWidgetWateredLabel;

    private Button mAppWidgetAddButton;
    private ImageView mPlantImage;

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
        mPlantImage = (ImageView) findViewById(R.id.plant_config_image);
        mAppWidgetCreatedLabel = (TextView) findViewById(R.id.seed_planted_text);
        mAppWidgetWateredLabel = (TextView) findViewById(R.id.plant_watered_text);

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

        //get the correct plant image
        long createdAt = SharedPrefUtils.loadStartTime(this, mAppWidgetId);
        long wateredAt = SharedPrefUtils.loadWaterTime(this, mAppWidgetId);
        long now = System.currentTimeMillis();
        long plantAge = now - createdAt;
        long waterAge = now - wateredAt;
        if(createdAt>0) {
            mPlantImage.setImageResource(PlantUtils.getPlantImageRes(this,plantAge, waterAge,0));
            String aliveFor = DateUtils.getRelativeTimeSpanString(
                    createdAt,
                    System.currentTimeMillis(),
                    DateUtils.MINUTE_IN_MILLIS)
                    .toString();
            mAppWidgetCreatedText.setText(aliveFor);
            mAppWidgetAddButton.setText(getString(R.string.reset_widget));
            mAppWidgetCreatedLabel.setVisibility(View.VISIBLE);
        }
        if(wateredAt>0) {
            mAppWidgetWateredText.setText(DateUtils.getRelativeTimeSpanString(wateredAt));
            mAppWidgetWateredLabel.setVisibility(View.VISIBLE);
        }
    }

    public void onAddButtonClicked(View view) {

        // When the button is clicked, create a new plane and set the start time
        SharedPrefUtils.saveStartTime(this, mAppWidgetId, System.currentTimeMillis());
        SharedPrefUtils.saveWaterTime(this, mAppWidgetId, System.currentTimeMillis());

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
        // It is the responsibility of the configuration activity to update the app widget
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        PlantWidget.updateAppWidget(this, appWidgetManager, mAppWidgetId);
        setResult(RESULT_CANCELED);
        finish();
    }
}

