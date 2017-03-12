package com.example.android.virtualpot;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;

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
    public static int getPlantImageRes(Context context, long plantAge, long waterAge, int type) {
        //check if plant is dead first
        //TODO: use dead plant images instead of just empty_pot
        boolean plantDead = waterAge > NO_WATER_LIFE;
        //plant is still alive! update image if old enough
        double hours = plantAge / (1000.0 * 60 * 60 );
        if (hours > 10) {
            return plantDead ? R.drawable.empty_pot : getImgResName(context,type,3);
        } else if (hours > 5) {
            return plantDead ? R.drawable.empty_pot : getImgResName(context,type,2);
        } else if (hours > 1) {
            return plantDead ? R.drawable.empty_pot : getImgResName(context,type,1);
        } else {
            return plantDead ? R.drawable.empty_pot : R.drawable.empty_pot;
        }
    }

    private static int getImgResName(Context context, int type, int level){
        Resources res = context.getResources();
        TypedArray plantTypes = res.obtainTypedArray(R.array.plant_types);
        String typeName = plantTypes.getString(type);
        return context.getResources().
                getIdentifier(typeName+"_"+level, "drawable", context.getPackageName());
    }

    /**
     * Returns the corresponding image resource of the cloud given the time since it was last watered
     *
     * @param milliSeconds Time since last watered milliseconds
     * @return Integer value of the image resource
     */
    public static int getCloudImageRes(long milliSeconds) {
        double minutes = milliSeconds / (1000.0 * 60);
        if (minutes > 120) {
            return R.drawable.cloud_3;
        } else if (minutes > 90) {
            return R.drawable.cloud_2;
        } else if (minutes > 60) {
            return R.drawable.cloud_1;
        } else if (minutes > 30) {
            return R.drawable.cloud_0;
        } else {
            //don't show any clouds
            return android.R.color.transparent;
        }
    }
}
