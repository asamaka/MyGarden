package com.example.android.virtualpot;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import static com.example.android.virtualpot.provider.PlantContract.BASE_CONTENT_URI;
import static com.example.android.virtualpot.provider.PlantContract.PATH_PLANTS;
import static com.example.android.virtualpot.provider.PlantContract.PlantEntry;

public class MainActivity
        extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private PlantListAdapter mAdapter;
    private RecyclerView mRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.plants_list_recycler_view);
        mRecyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );
        mAdapter = new PlantListAdapter(this, null);
        mRecyclerView.setAdapter(mAdapter);
        getSupportLoaderManager().initLoader(1, null, this);
    }


    public void onAddButtonClicked(View view) {
        // When the button is clicked, create a new plant and set the start time
        //TODO: handle multiple  plant types
        int plantType = 1;
        long timeNow = System.currentTimeMillis();

        // Insert the new plant into DB
        ContentValues contentValues = new ContentValues();
        contentValues.put(PlantEntry.COLUMN_PLANT_TYPE, plantType);
        contentValues.put(PlantEntry.COLUMN_CREATION_TIME, timeNow);
        contentValues.put(PlantEntry.COLUMN_LAST_WATERED_TIME, timeNow);
        contentValues.put(PlantEntry.COLUMN_IS_DEAD, false);
        getContentResolver().insert(PlantEntry.CONTENT_URI, contentValues);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        Uri CONTACT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLANTS).build();
        return new CursorLoader(this, CONTACT_URI, null,
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        cursor.moveToFirst();
        mAdapter.swapCursor(cursor);
        //mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    public void onPlantClick(View view) {
        ImageView imgView = (ImageView)view.findViewById(R.id.plant_list_item_image);
        long plantId = (long) imgView.getTag();
        Intent intent = new Intent(getBaseContext(), PlantDetail.class);
        intent.putExtra("EXTRA_PLANT_ID", plantId);
        startActivity(intent);
    }
}
