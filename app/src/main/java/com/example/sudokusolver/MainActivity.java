package com.example.sudokusolver;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private boolean isNightMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.BaseTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //PreferenceManager.setDefaultValues(this, R.xml.app_preferences, false);
        isNightMode = ((getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)==Configuration.UI_MODE_NIGHT_YES);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !isNightMode) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
            setSystemBarTheme(this, isNightMode);
        }
        darkModeHandler(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("darkmode", false));
    }

    @Override
    protected void onResume() {
        super.onResume();
        darkModeHandler(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("darkmode", false));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static final void setSystemBarTheme(final Activity pActivity, final boolean pIsDark) {
        // Fetch the current flags.
        final int lFlags = pActivity.getWindow().getDecorView().getSystemUiVisibility();
        // Update the SystemUiVisibility dependening on whether we want a Light or Dark theme.
        pActivity.getWindow().getDecorView().setSystemUiVisibility(pIsDark ? (lFlags & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR) : (lFlags | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR));
    }

    /**
     * @see SolvingActivity
     * @param darkMode
     */
    public void darkModeHandler(boolean darkMode) {
        if(isNightMode!=darkMode) {
            isNightMode=!isNightMode;
            System.out.println("reached");
            if (darkMode) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
            }
            recreate();
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonSetting:
                startActivity(new Intent(this, SettingsActivity.class));
                darkModeHandler(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("darkmode", false));
                break;
            case R.id.buttonComplete:
                startActivity(new Intent(this, SolvingActivity.class));
                break;
            case R.id.buttonRules:
                startActivity(new Intent(this, RulesActivity.class));
                break;
            case R.id.buttonStep:
                Intent intent = new Intent(this, SolvingActivity.class);
                intent.putExtra("step", true);
                startActivity(intent);
                break;
            case R.id.aboutNav:
                displayAbout(this);
                break;
        }
    }

    public static void displayAbout(Context context) {
        new AlertDialog.Builder(context)
                .setTitle("About")
                .setMessage("SoSu.Me - Solving Sudokus. Made easy.\n\n" +
                        "The app was developed by Armin Stanitzok. I appreciate every rating and review in the Play Store. " +
                        "If you want to report bugs or got improvements feel free to write an e-mail to my connected address in the Play Store!")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();
    }
}
