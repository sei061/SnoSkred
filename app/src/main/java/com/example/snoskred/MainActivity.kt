package com.example.snoskred

import android.app.UiModeManager.MODE_NIGHT_YES
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.preference.Preference
import androidx.preference.PreferenceManager
import com.example.snoskred.fragments.SettingsFragment
import com.google.android.gms.maps.GoogleMap
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.coroutines.coroutineContext
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lateinit var mMap: GoogleMap
        val myToolbar: Toolbar = findViewById(R.id.myToolbar)
        myToolbar.title = ""
        setSupportActionBar(myToolbar)
        myToolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material)
        myToolbar.setNavigationOnClickListener {
            onBackPressed()
        }



        val exitBtn = findViewById<FloatingActionButton>(R.id.floatingActionButton2)

        exitBtn.setOnClickListener {
            this@MainActivity.finish()
            exitProcess(0)
        }

        val settingsBtn = findViewById<FloatingActionButton>(R.id.floatingActionButton)

        settingsBtn.setOnClickListener {
            supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView,
                SettingsFragment()
            ).commit()
        }



        loadSettings()


    }
    fun loadSettings(){
        val sp = PreferenceManager.getDefaultSharedPreferences(baseContext)

        val mode = sp.getBoolean("mode", true)
        if (!mode){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }

    }
    override fun onBackPressed() {
        val fragmentUp = supportFragmentManager.findFragmentById(R.id.homeFragment)
        if(fragmentUp != null){
            super.onBackPressed()
        }
    }

}