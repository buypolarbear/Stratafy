package com.stratafy.helper;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by cn on 8/14/2017.
 */

public class Glob {

    public static final String MAIN_DOMAIN = "http://ec2-52-24-15-193.us-west-2.compute.amazonaws.com/";
  //  public static final String MAIN_DOMAIN = "http://ec2-35-166-240-31.us-west-2.compute.amazonaws.com/";

    public static final String API_BUILDING_MANAGE = MAIN_DOMAIN + "buildings/manage/";
    public static final String API_LOGIN = MAIN_DOMAIN + "login.json";
    public static final String API_REGISTER = MAIN_DOMAIN + "register.json";
    public static final String API_GET_LAWS = MAIN_DOMAIN + "laws/get_law/";
    public static final String API_GET_DOCUMENT = MAIN_DOMAIN + "documents/index/";
    public static final String API_GET_LOG = MAIN_DOMAIN + "logs/index/";
    public static final String API_POST_LOG = MAIN_DOMAIN + "logs/add_log/";
    public static final String API_GET_NOTIFICATION = MAIN_DOMAIN + "notifications/notificationlist/";
    public static final String API_GET_DIRECTORY = MAIN_DOMAIN + "buildings/directory/";
    public static final String API_POST_EVENT = MAIN_DOMAIN + "events/add/";
    public static final String API_GET_EVENT = MAIN_DOMAIN + "events/index/";
    public static final String API_EDIT_PROFILE = MAIN_DOMAIN + "buildings/edituser/";
    public static final String API_GOOGLE_PLACES = MAIN_DOMAIN + "google_places/index/";

    public static Typeface avenir(Context context){
        return Typeface.createFromAsset(context.getAssets(), "font/Avenir_45_Book.ttf");
    }

}
