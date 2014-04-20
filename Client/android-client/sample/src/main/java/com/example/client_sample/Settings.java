package com.example.client_sample;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
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

        EditTextPreference debugUrlPref = (EditTextPreference) findPreference(getString(R.string.debugUrlKey));
        debugUrlPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (newValue != null) {
                    preference.setSummary(newValue.toString());
                }
                return true;
            }
        });
        debugUrlPref.setSummary(debugUrlPref.getText());
    }

}
