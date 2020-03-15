package com.daevsoft.muvi;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.daevsoft.muvi.reminders.ReminderReceiver;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
        private String KEY_DAILY_NOTIF;
        private String KEY_RELEASE_NOTIF;

        private SwitchPreferenceCompat switchPreferenceDaily;
        private SwitchPreferenceCompat switchPreferenceRelease;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            init();
            setSummaries();
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onDetach() {
            super.onDetach();
            Context context = getContext();
            if (context != null) {

                if (switchPreferenceDaily.isChecked()) {
                    ReminderReceiver.setRemainder(context, ReminderReceiver.TYPE_DAILY_REMINDER);
                } else {
                    ReminderReceiver.cancelReminder(context, ReminderReceiver.TYPE_DAILY_REMINDER);
                }

                if (switchPreferenceRelease.isChecked()) {
                    ReminderReceiver.setRemainder(context, ReminderReceiver.TYPE_RELEASE_REMINDER);
                } else {
                    ReminderReceiver.cancelReminder(context, ReminderReceiver.TYPE_RELEASE_REMINDER);
                }

            }
        }

        private void setSummaries() {
            SharedPreferences sharedPreferences = getPreferenceManager().getSharedPreferences();
            switchPreferenceDaily.setChecked(sharedPreferences.getBoolean(KEY_DAILY_NOTIF, false));
            switchPreferenceRelease.setChecked(sharedPreferences.getBoolean(KEY_RELEASE_NOTIF, false));
        }

        private void init() {
            KEY_DAILY_NOTIF = getString(R.string.key_reminder_daily);
            KEY_RELEASE_NOTIF = getString(R.string.key_reminder_release);

            switchPreferenceDaily = findPreference(KEY_DAILY_NOTIF);
            switchPreferenceRelease = findPreference(KEY_RELEASE_NOTIF);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(KEY_DAILY_NOTIF))
                switchPreferenceDaily.setChecked(sharedPreferences.getBoolean(KEY_DAILY_NOTIF, false));
            if (key.equals(KEY_RELEASE_NOTIF))
                switchPreferenceRelease.setChecked(sharedPreferences.getBoolean(KEY_RELEASE_NOTIF, false));
        }
    }
}