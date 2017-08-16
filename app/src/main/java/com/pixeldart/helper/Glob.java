package com.pixeldart.helper;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by cn on 8/14/2017.
 */

public class Glob {

    public static final String API_BUILDING_MANAGE = "http://ec2-52-26-76-39.us-west-2.compute.amazonaws.com/buildings/manage/395009.json";

    public static Typeface avenir(Context context){
        return Typeface.createFromAsset(context.getAssets(), "font/Avenir_45_Book.ttf");
    }

}
