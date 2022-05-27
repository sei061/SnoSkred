package com.example.snoskred.fragments

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.example.snoskred.MainActivity
import com.example.snoskred.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        setHasOptionsMenu(true)
        (activity as MainActivity).supportActionBar?.title = "Innstillinger"

    }
}