package com.example.sudokusolver;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.SwitchPreference;
import android.preference.EditTextPreference;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import java.util.List;

public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener, Application.ActivityLifecycleCallbacks {

    public static Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.app_preferences);
        PreferenceManager.setDefaultValues(this, R.xml.app_preferences, false);
        init();
    }

    public void init() {
        context = this;
        darkModeHandler(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("darkmode", false));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !((getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)==Configuration.UI_MODE_NIGHT_YES)) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
            MainActivity.setSystemBarTheme(this, false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
        darkModeHandler(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("darkmode", false));
    }

    /**
     * switches the dark mode off/on
     * @param darkMode the desired dark mode value
     */
    public void darkModeHandler(boolean darkMode) {
        boolean curDarkMode = false;
        switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_YES: curDarkMode=true; break;
        }
        if(curDarkMode!=darkMode) {
            if (darkMode) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
            }
            recreate();
        }
    }

    /**
     * Assign a value to a key and write it in persistent storage (background)
     * @param key the key assigned in the XML file to the graphical object
     * @param value value to bind to the key
     */
    public static void setDefaults(String key, String value) {
        if(context==null) throw new java.lang.RuntimeException("Context of the Settings are null");
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(key);
        editor.putString(key, value);
        editor.apply();
        //editor.commit() writes immediately while apply handles it in the background
    }

    public static String getDefaults(String key) {
        if(context==null) throw new java.lang.RuntimeException("Context is wrong");
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }
    public static int getDefaults(String key, int defValue) {
        if(context==null) throw new java.lang.RuntimeException("Context is wrong");
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getInt(key, defValue);
    }
    public static boolean getDefaults(String key, boolean defValue) {
        if(context==null) throw new java.lang.RuntimeException("Context is wrong");
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(key, defValue);
    }

    public void onAbout(View view) {

    }

    /**
     * Set the default values as summaries
     * @param sharedPreferences
     * @param key
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        /*switch(key) {
            case "refreshTime":
                EditTextPreference connectionPref = (EditTextPreference) findPreference(key);
                //connectionPref.setSummary(sharedPreferences.getString(key, ""));
                SettingsActivity.setDefaults(key,sharedPreferences.getString(key, null));
                //refreshTime = Integer.getInteger(sharedPreferences.getString(key, null));
                break;
        }*/
    }


    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        getApplication().registerActivityLifecycleCallbacks(this);
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        getApplication().unregisterActivityLifecycleCallbacks(this);
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
