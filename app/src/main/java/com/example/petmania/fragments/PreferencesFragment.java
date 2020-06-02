package com.example.petmania.fragments;

import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.widget.Toast;
import androidx.annotation.RequiresApi;

import com.example.petmania.R;

public class PreferencesFragment extends PreferenceFragment {


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.pref_general);

        findPreference(getResources().getString(R.string.app_settings)).setOnPreferenceClickListener(preference -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Toast.makeText(getContext(), "Settings", Toast.LENGTH_SHORT).show();
            }
            return false;
        });
    }
}
