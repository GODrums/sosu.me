package com.example.sudokusolver;

import android.support.v7.app.AppCompatDelegate;

public class IconRef {

    public static int getMenuIcon() {
        if(AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_YES)
            return R.drawable.ic_menu_white;
        return R.drawable.ic_menu_black;
    }
}
