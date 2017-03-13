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
        //TODO: use dying plant images instead of (or alongside with) water meter
        boolean plantDead = waterAge > NO_WATER_LIFE;
        //plant is still alive! update image if old enough
        double hours = plantAge / (1000.0 * 60 * 60 );
        if (hours > 10) {
            return plantDead ? R.drawable.empty_pot : getPlantImgResName(context,type,3);
        } else if (hours > 5) {
            return plantDead ? R.drawable.empty_pot : getPlantImgResName(context,type,2);
        } else if (hours > 1) {
            return plantDead ? R.drawable.empty_pot : getPlantImgResName(context,type,1);
        } else {
            return plantDead ? R.drawable.empty_pot : R.drawable.empty_pot;
        }
    }

    public static int getPlantImgResName(Context context, int type, int level){
        Resources res = context.getResources();
        TypedArray plantTypes = res.obtainTypedArray(R.array.plant_types);
        String typeName = plantTypes.getString(type);
        return context.getResources().
                getIdentifier(typeName+"_"+level, "drawable", context.getPackageName());
    }

    /**
     * Returns the corresponding image resource of the water meter given the last time the plant was watered
     * @param waterAge Time (in milliseconds) since it was last watered
     * @return Image Resource to the correct plant image
     */
    public static int getWaterImageRes(long waterAge) {
        //plant is still alive! update image if old enough
        double hours = waterAge / (1000.0 * 60 * 60 );
        if (hours > 10) {
            return R.drawable.water_empty;
        } else if (hours > 4) {
            return R.drawable.water_1;
        } else if (hours > 3) {
            return R.drawable.water_2;
        } else if (hours > 2) {
            return R.drawable.water_3;
        } else if (hours > 1) {
            return R.drawable.water_4;
        } else {
            return R.drawable.water_full;
        }
    }

    public static int getDisplayAgeInt(long milliSeconds){
        int days = (int) (milliSeconds / (1000.0 * 60 * 60 * 24));
        if(days>=1) return days;
        int hours = (int) (milliSeconds / (1000.0 * 60 * 60));
        if(hours>=1) return hours;
        return (int) (milliSeconds / (1000.0 * 60 ));
    }

    public static String getDisplayAgeUnit(Context context, long milliSeconds){
        int days = (int) (milliSeconds / (1000.0 * 60 * 60 * 24));
        if(days>=1) return context.getString(R.string.days);
        int hours = (int) (milliSeconds / (1000.0 * 60 * 60));
        if(hours>=1) return context.getString(R.string.hours);
        return context.getString(R.string.minutes);
    }
}
