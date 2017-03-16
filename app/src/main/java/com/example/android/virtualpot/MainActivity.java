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
import static com.example.android.virtualpot.provider.PlantContract.INVALID_PLANT_ID;
import static com.example.android.virtualpot.provider.PlantContract.PATH_PLANTS;
import static com.example.android.virtualpot.provider.PlantContract.PlantEntry;

public class MainActivity
        extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private PlantListAdapter mAdapter;

    private RecyclerView mGardenRecyclerView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGardenRecyclerView = (RecyclerView) findViewById(R.id.plants_list_recycler_view);
        mGardenRecyclerView.setLayoutManager(
                new GridLayoutManager(this, 4)
        );
        mAdapter = new PlantListAdapter(this, null);
        mGardenRecyclerView.setAdapter(mAdapter);

        getSupportLoaderManager().initLoader(1, null, this);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        Uri PLANT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLANTS).build();
        return new CursorLoader(this, PLANT_URI, null,
                null, null, PlantEntry.COLUMN_CREATION_TIME);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        cursor.moveToFirst();
        mAdapter.swapCursor(cursor);
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


    public void onAddFabClick(View view) {
        Intent intent = new Intent(this, AddPlantActivity.class);
        startActivity(intent);
    }
}
