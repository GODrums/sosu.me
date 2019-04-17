package com.example.sudokusolver;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.Preference;
import android.preference.PreferenceCategory;

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.app_preferences);
    }

    private void pickPreferenceObject(Preference p) {
        if (p instanceof PreferenceCategory) {
            PreferenceCategory cat = (PreferenceCategory) p;
            for (int i = 0; i < cat.getPreferenceCount(); i++) {
                pickPreferenceObject(cat.getPreference(i));
            }
        } else if (p instanceof android.preference.EditTextPreference) {
            android.preference.EditTextPreference editTextPref = (android.preference.EditTextPreference) p;
            if(Integer.getInteger(editTextPref.getText())<1) throw new java.lang.RuntimeException("Wrong Input");
            p.setSummary(editTextPref.getText());
        }
    }

}