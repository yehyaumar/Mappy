package com.yehyaumar.mappy;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;

/**
 * Created by YehyaUmar on 8/5/2017.
 */

class PrefManager {
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Context _context;

    private static final String PREF_NAME = "set-mappy";
    private static final String IS_FIRST_TIME_LAUNCH = "isFirstTimeLaunch";

    PrefManager(Context context){
        this._context = context;
        int PRIVATE_MODE = 0;
        preferences = this._context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = preferences.edit();
    }

    void setFirstTimeLaunch(boolean isFirstTimeLaunch){
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTimeLaunch);
        editor.commit();
    }

    boolean isFirstTimeLaunch(){
        return preferences.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }


    boolean isVerificationEnabled(){
        return PreferenceManager.getDefaultSharedPreferences(_context).getBoolean("verification", true);
    }
}
