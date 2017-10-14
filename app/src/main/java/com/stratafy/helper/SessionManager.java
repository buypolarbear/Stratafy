package com.stratafy.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class SessionManager {

    private static String TAG = SessionManager.class.getSimpleName();
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "STRATAFY";
    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }
 
    public void setLogin(boolean isLoggedIn) {
        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);
        editor.commit();
    }

    public void setIdToken(@NonNull String token) {
        editor.putString(KEY_REFRESH_TOKEN, token);
        editor.apply();
    }

    @Nullable
    public String getIdToken() {
        return pref.getString(KEY_REFRESH_TOKEN, null);
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }

}