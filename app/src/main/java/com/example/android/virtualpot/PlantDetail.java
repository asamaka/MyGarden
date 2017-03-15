package com.example.android.virtualpot;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.virtualpot.provider.PlantContract;

import static com.example.android.virtualpot.provider.PlantContract.BASE_CONTENT_URI;
import static com.example.android.virtualpot.provider.PlantContract.PATH_PLANTS;

public class PlantDetail extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    long mPlantId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPlantId = getIntent().getLongExtra("EXTRA_PLANT_ID", 0);
        setContentView(R.layout.activity_plant_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportLoaderManager().initLoader(1, null, this);
    }

    public void onBackButtonClick(View view) {
        finish();
    }

    public void onWaterButtonClick(View view) {
        PlantWateringService.startActionWaterPlant(this,mPlantId);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri SINGLE_PLANT_URI = ContentUris.withAppendedId(
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLANTS).build(), mPlantId);
        return new CursorLoader(this, SINGLE_PLANT_URI, null,
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) return;
        cursor.moveToFirst();
        int createTimeIndex = cursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_CREATION_TIME);
        int waterTimeIndex = cursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_LAST_WATERED_TIME);
        int planTypeIndex = cursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_PLANT_TYPE);

        int plantType = cursor.getInt(planTypeIndex);
        long createdAt = cursor.getLong(createTimeIndex);
        long wateredAt = cursor.getLong(waterTimeIndex);
        long timeNow = System.currentTimeMillis();

        int plantImgRes = PlantUtils.getPlantImageRes(this, timeNow - createdAt, timeNow - wateredAt, plantType);
        int waterImgRes = PlantUtils.getWaterImageRes(timeNow - wateredAt);

        ((ImageView) findViewById(R.id.plant_detail_image)).setImageResource(plantImgRes);
        ((ImageView) findViewById(R.id.water_detail_image)).setImageResource(waterImgRes);
        ((TextView) findViewById(R.id.plant_age_number)).setText(
                String.valueOf(PlantUtils.getDisplayAgeInt(timeNow - createdAt))
        );
        ((TextView) findViewById(R.id.plant_age_unit)).setText(
                PlantUtils.getDisplayAgeUnit(this, timeNow - createdAt)
        );
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public void onCutButtonClick(View view) {
        Uri SINGLE_PLANT_URI = ContentUris.withAppendedId(
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLANTS).build(), mPlantId);
        getContentResolver().delete(SINGLE_PLANT_URI, null, null);
        finish();
    }
}
