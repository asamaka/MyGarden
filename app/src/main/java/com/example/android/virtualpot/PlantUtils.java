package com.example.android.virtualpot;

/**
 * Created by asser on 3/6/17.
 */

public class PlantUtils {

    private static final long NO_WATER_LIFE = 1000 * 60 * 60 * 48; // 48 hours

    /**
     * Returns the corresponding image resource of the plant given the plant's age and
     * when it was last watered
     *
     * @param plantAge Time (in milliseconds) the plant has been alive
     * @param waterAge Time (in milliseconds) since it was last watered
     * @return Image Resource to the correct plant image
     */
    public static int getPlantImageRes(long plantAge, long waterAge) {
        //check if plant is dead first
        if (waterAge > NO_WATER_LIFE) {
            return R.drawable.pot_0;
        }
        //plant is still alive! update image if old enough
        long days = plantAge / (1000 * 60 * 60 * 24);
        if (days > 5) {
            return R.drawable.pot_3;
        } else if (days > 2) {
            return R.drawable.pot_2;
        } else if (days > 1) {
            return R.drawable.pot_1;
        } else {
            return R.drawable.pot_0;
        }
    }

    /**
     * Returns the corresponding image resource of the cloud given the time since it was last watered
     *
     * @param milliSeconds Time since last watered milliseconds
     * @return Integer value of the image resource
     */
    public static int getCloudImageRes(long milliSeconds) {
        double minutes = milliSeconds / (1000.0 * 60);
        int imgRes = R.drawable.cloud_0;
        if (minutes > 90) {
            imgRes = R.drawable.cloud_0;
        } else if (minutes > 60) {
            imgRes = R.drawable.cloud_0;
        } else if (minutes > 30) {
            imgRes = R.drawable.cloud_0;
        }
        return imgRes;
    }
}
