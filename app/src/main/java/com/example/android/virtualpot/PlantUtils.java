package com.example.android.virtualpot;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by asser on 3/6/17.
 */

public class PlantUtils {
    /**
     * Returns the corresponding image resource of the plant given the plant's age in milliseconds
     * @param milliSeconds Plant's age
     * @return Integer value of the image resource
     */
    public static int getImageResourceByAge(long milliSeconds){
        double minutes = milliSeconds / (1000.0 * 60);
        if (minutes > 90) {
            return R.drawable.pot_3;
        } else if (minutes > 60) {
            return R.drawable.pot_2;
        } else if (minutes > 30) {
            return R.drawable.pot_1;
        } else {
            return R.drawable.pot_0;
        }
    }
}
