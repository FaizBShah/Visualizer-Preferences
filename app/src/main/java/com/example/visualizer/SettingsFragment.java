package com.example.visualizer;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreferenceCompat;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceChangeListener {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    /**
     * Called during {@link #onCreate(Bundle)} to supply the preferences for this fragment.
     * Subclasses are expected to call {@link #setPreferenceScreen(PreferenceScreen)} either
     * directly or via helper methods such as {@link #addPreferencesFromResource(int)}.
     *
     * @param savedInstanceState If the fragment is being re-created from a previous saved state,
     *                           this is the state.
     * @param rootKey            If non-null, this preference fragment should be rooted at the
     *                           {@link PreferenceScreen} with this key.
     */
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_visualizer);

        // Getting SharedPreferences object
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        // Getting PreferenceScreen object
        PreferenceScreen prefScreen = getPreferenceScreen();

        // Getting the no. of preferences in the PreferenceScreen
        int count = prefScreen.getPreferenceCount();

        // Going through each preference
        for (int i = 0; i < count; i++) {
            Preference preference = prefScreen.getPreference(i);
            // Checking if its not a SwitchPreferenceCompat
            if (!(preference instanceof SwitchPreferenceCompat)) {
                String value = sharedPreferences.getString(preference.getKey(), "");
                setPreferenceSummary(preference, value);
            }
        }

        // Setting onPreferenceChangeListener to the EditTextPreference
        Preference preference = findPreference(getString(R.string.pref_size_key));
        preference.setOnPreferenceChangeListener(this);
    }

    // A helper method to set the summary at a particular preference
    private void setPreferenceSummary(Preference preference, String value) {
        // Checking if the preference is an instance of ListPreference or not
        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            // Finding the index of the current selected value in the entriesValues array
            int prefIndex = listPreference.findIndexOfValue(value);
            if (prefIndex >= 0) {
                // If prefIndex is a valid position, then setting the summary to be the label at that position
                listPreference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        }
        else if (preference instanceof EditTextPreference) {
            // If the preference is an instance of EditTextPreference
            preference.setSummary(value);
        }
    }

    /**
     * Called when a shared preference is changed, added, or removed. This
     * may be called even if a preference is set to its existing value.
     *
     * <p>This callback will be run on your main thread.
     *
     * <p><em>Note: This callback will not be triggered when preferences are cleared via
     * {@link SharedPreferences.Editor#clear()}.</em>
     *
     * @param sharedPreferences The {@link SharedPreferences} that received
     *                          the change.
     * @param key               The key of the preference that was changed, added, or
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
        if (preference != null && !(preference instanceof SwitchPreferenceCompat)) {
            String value = sharedPreferences.getString(key, "");
            setPreferenceSummary(preference, value);
        }
    }

    /**
     * Called when a preference has been changed by the user. This is called before the state
     * of the preference is about to be updated and before the state is persisted.
     *
     * @param preference The changed preference
     * @param newValue   The new value of the preference
     * @return {@code true} to update the state of the preference with the new value
     */
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        // Making the error toast
        Toast error = Toast.makeText(getContext(), "Please select a number between 0.1 and 3", Toast.LENGTH_SHORT);

        String sizeKey = getString(R.string.pref_size_key);
        // Checking if the key of the changed preference is equal to the key of the size preference
        if (preference.getKey().equals(sizeKey)) {
            // Getting the string entered by the user
            String stringSize = (String) newValue;
            try {
                float size = Float.parseFloat(stringSize);
                if (size > 3 || size <=0) {
                    error.show();
                    return false;
                }
            } catch (NumberFormatException nfe) {
                error.show();
                return false;
            }
        }
        return true;
    }
}
