package com.example.android.mygarden;

/*
* Copyright (C) 2017 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*  	http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;

public class PlantUtils {

    private static final long MINUTE_MILLISECONDS = 1000 * 60;
    private static final long HOUR_MILLISECONDS = MINUTE_MILLISECONDS;//* 60;
    private static final long DAY_MILLISECONDS = HOUR_MILLISECONDS * 24;

    public static final long MIN_AGE_BETWEEN_WATER = HOUR_MILLISECONDS * 2; // 2 hours
    public static final long DANGER_AGE_WITHOUT_WATER = HOUR_MILLISECONDS * 6; // 6 hours
    public static final long MAX_AGE_WITHOUT_WATER = HOUR_MILLISECONDS * 12; // 12 hours
    public static final long TINY_AGE = DAY_MILLISECONDS * 0; // 0 days
    public static final long JUVENILE_AGE = DAY_MILLISECONDS * 1; // 1 days
    public static final long FULLY_GROWN_AGE = DAY_MILLISECONDS * 2; // 2 days


    enum PlantStatus {ALIVE, DYING, DEAD}

    ;

    enum PlantSize {TINY, JUVENILE, FULLY_GROWN}

    ;

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
        PlantStatus status = PlantStatus.ALIVE;
        if (waterAge > MAX_AGE_WITHOUT_WATER) status = PlantStatus.DEAD;
        else if (waterAge > DANGER_AGE_WITHOUT_WATER) status = PlantStatus.DYING;

        //Update image if old enough
        if (plantAge > FULLY_GROWN_AGE) {
            return getPlantImgRes(context, type, status, PlantSize.FULLY_GROWN);
        } else if (plantAge > JUVENILE_AGE) {
            return getPlantImgRes(context, type, status, PlantSize.JUVENILE);
        } else if (plantAge > TINY_AGE) {
            return getPlantImgRes(context, type, status, PlantSize.TINY);
        } else {
            return R.drawable.empty_pot;
        }
    }

    public static int getPlantImgRes(Context context, int type, PlantStatus status, PlantSize size) {
        Resources res = context.getResources();
        TypedArray plantTypes = res.obtainTypedArray(R.array.plant_types);
        String resName = plantTypes.getString(type);
        if (status == PlantStatus.DYING) resName += "_danger";
        else if (status == PlantStatus.DEAD) resName += "_dead";
        if (size == PlantSize.TINY) resName += "_1";
        else if (size == PlantSize.JUVENILE) resName += "_2";
        else if (size == PlantSize.FULLY_GROWN) resName += "_3";
        return context.getResources().getIdentifier(resName, "drawable", context.getPackageName());
    }

    public static String getPlantTypeName(Context context, int type) {
        Resources res = context.getResources();
        TypedArray plantTypes = res.obtainTypedArray(R.array.plant_types);
        return plantTypes.getString(type);
    }

    public static int getEmptyImgeRes() {
        return R.drawable.grass;
    }

    /**
     * Returns the corresponding image resource of the water meter given the last time the plant was watered
     *
     * @param waterAge Time (in milliseconds) since it was last watered
     * @return Image Resource to the correct plant image
     */
    public static int getWaterImageRes(long waterAge) {
        //plant is still alive! update image if old enough
        int hours = (int) (waterAge / HOUR_MILLISECONDS);
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

    public static int getDisplayAgeInt(long milliSeconds) {
        int days = (int) (milliSeconds / DAY_MILLISECONDS);
        if (days >= 1) return days;
        int hours = (int) (milliSeconds / HOUR_MILLISECONDS);
        if (hours >= 1) return hours;
        return (int) (milliSeconds / MINUTE_MILLISECONDS);
    }

    public static String getDisplayAgeUnit(Context context, long milliSeconds) {
        int days = (int) (milliSeconds / DAY_MILLISECONDS);
        if (days >= 1) return context.getString(R.string.days);
        int hours = (int) (milliSeconds / HOUR_MILLISECONDS);
        if (hours >= 1) return context.getString(R.string.hours);
        return context.getString(R.string.minutes);
    }
}