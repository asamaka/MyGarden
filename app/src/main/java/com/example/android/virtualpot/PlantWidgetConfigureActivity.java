package com.example.android.virtualpot;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Date;

/**
 * The configuration screen for the {@link PlantWidget PlantWidget} AppWidget.
 */
public class PlantWidgetConfigureActivity extends Activity {

    private static final String PREFS_NAME = "com.example.android.virtualpot.PlantWidget";
    private static final String PREF_PREFIX_NAME = "name_";
    private static final String PREF_PREFIX_DATETIME = "last_clicked_";
    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private EditText mAppWidgetText;
    private TextView mAppWidgetLastClicked;
    private Button mAppWidgetAddButton;
    private Button mAppWidgetCancelButton;
    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = PlantWidgetConfigureActivity.this;

            // When the button is clicked, store the string locally
            String widgetText = mAppWidgetText.getText().toString();
            saveTitlePref(context, mAppWidgetId, widgetText);
            saveStartTimePref(context,mAppWidgetId, new Date());

            // It is the responsibility of the configuration activity to update the app widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            PlantWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };

    View.OnClickListener mOnCancelClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            setResult(RESULT_CANCELED);
            finish();
        }
    };

    public PlantWidgetConfigureActivity() {
        super();
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void saveTitlePref(Context context, int appWidgetId, String text) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_NAME + appWidgetId, text);
        prefs.apply();
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void saveStartTimePref(Context context, int appWidgetId, Date date) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        Log.d("saving to pref","Widget Id = "+appWidgetId);
        prefs.putLong(PREF_PREFIX_DATETIME + appWidgetId, date.getTime());
        prefs.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String loadTitlePref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        Log.d("Reading pref","Widget Id = "+appWidgetId);
        String titleValue = prefs.getString(PREF_PREFIX_NAME + appWidgetId, null);
        if (titleValue != null) {
            return titleValue;
        } else {
            return context.getString(R.string.appwidget_text);
        }
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static Date loadStartTimePref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        long lastClicked = prefs.getLong(PREF_PREFIX_DATETIME + appWidgetId, 0);
        if (lastClicked != 0) {
            return new Date(lastClicked);
        } else {
            return null;
        }
    }

    static void deleteTitlePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_NAME + appWidgetId);
        prefs.apply();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.plant_widget_configure);
        mAppWidgetText = (EditText) findViewById(R.id.appwidget_text);
        mAppWidgetAddButton = (Button) findViewById(R.id.add_button);
        mAppWidgetCancelButton = (Button) findViewById(R.id.cancel_button);
        mAppWidgetLastClicked = (TextView) findViewById(R.id.appwidget_last_clicked);
        mAppWidgetAddButton.setOnClickListener(mOnClickListener);
        mAppWidgetCancelButton.setOnClickListener(mOnCancelClickListener);

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

        Date createdAt = loadStartTimePref(PlantWidgetConfigureActivity.this, mAppWidgetId);
        mAppWidgetText.setText(loadTitlePref(PlantWidgetConfigureActivity.this, mAppWidgetId));
        if(createdAt != null) {
            mAppWidgetLastClicked.setText(createdAt.toString());
            mAppWidgetAddButton.setText(getString(R.string.reset_widget));
        }
    }
}

