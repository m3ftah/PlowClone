package alarmproject.apps.plow.alarmproject.activities;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import alarmproject.apps.plow.alarmproject.R;

/**
 * Created by YouNesS on 07/11/2015.
 */
public class SettingsActivity extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.activity_preferences);
    }
}
