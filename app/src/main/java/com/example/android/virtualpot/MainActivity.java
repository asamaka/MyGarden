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
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import static com.example.android.virtualpot.provider.PlantContract.BASE_CONTENT_URI;
import static com.example.android.virtualpot.provider.PlantContract.PATH_PLANTS;
import static com.example.android.virtualpot.provider.PlantContract.PlantEntry;

public class MainActivity
        extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private PlantListAdapter mAdapter;
    private PlantTypesAdapter mTypesAdapter;
    private RecyclerView mGardenRecyclerView;
    private RecyclerView mTypesRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGardenRecyclerView = (RecyclerView) findViewById(R.id.plants_list_recycler_view);
        mTypesRecyclerView =  (RecyclerView) findViewById(R.id.plant_types_recycler_view);
        mGardenRecyclerView.setLayoutManager(
                new GridLayoutManager(this, 4)
        );
        mTypesRecyclerView.setLayoutManager(
                new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        );
        mAdapter = new PlantListAdapter(this, null);
        mTypesAdapter = new PlantTypesAdapter(this);
        mGardenRecyclerView.setAdapter(mAdapter);
        mTypesRecyclerView.setAdapter(mTypesAdapter);
        getSupportLoaderManager().initLoader(1, null, this);
    }


    public void onAddButtonClicked(View view) {

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
        ImageView imgView = (ImageView) view.findViewById(R.id.plant_list_item_image);
        long plantId = (long) imgView.getTag();
        Intent intent = new Intent(getBaseContext(), PlantDetail.class);
        intent.putExtra("EXTRA_PLANT_ID", plantId);
        startActivity(intent);
    }

    public void onAddButtonClick(View view) {
        // When the button is clicked, create a new plant and set the start time
        //get the plant type from the tag
        ImageView imgView = (ImageView) view.findViewById(R.id.plant_type_image);
        int plantType = (int) imgView.getTag();

        long timeNow = System.currentTimeMillis();

        // Insert the new plant into DB
        ContentValues contentValues = new ContentValues();
        contentValues.put(PlantEntry.COLUMN_PLANT_TYPE, plantType);
        contentValues.put(PlantEntry.COLUMN_CREATION_TIME, timeNow);
        contentValues.put(PlantEntry.COLUMN_LAST_WATERED_TIME, timeNow);
        contentValues.put(PlantEntry.COLUMN_IS_DEAD, false);
        getContentResolver().insert(PlantEntry.CONTENT_URI, contentValues);
    }
}
