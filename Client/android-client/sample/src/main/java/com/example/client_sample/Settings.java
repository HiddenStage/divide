package com.example.client_sample;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

/**
 * Created by williamwebb on 1/11/14.
 */
public class Settings extends PreferenceActivity {

    public static final String PREFERENCE_NAME = "preferences";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager manager = getPreferenceManager();
        manager.setSharedPreferencesName(PREFERENCE_NAME);

        addPreferencesFromResource(R.xml.prefs);

    }

}
