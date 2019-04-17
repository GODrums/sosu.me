package com.example.sudokusolver;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.widget.ImageView;

public class StartingActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting);

        PreferenceManager.setDefaultValues(this, R.xml.app_preferences, false);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        if((getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)==Configuration.UI_MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            //findViewById(R.drawable.startscreen)
            ((ImageView)findViewById(R.id.startImage)).setImageResource(R.drawable.ic_logo_dark);
            findViewById(R.id.startLayout).setBackgroundColor(getResources().getColor(R.color.colorBlack));
        }

        Intent intent = new Intent(getApplicationContext(),
                MainActivity.class);
        startActivity(intent);
        finish();
    }
}
