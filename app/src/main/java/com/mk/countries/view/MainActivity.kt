package com.mk.countries.view

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.mk.countries.R
import com.mk.countries.model.util.LocationUtils

class MainActivity : AppCompatActivity() {
    lateinit var locationUtils: LocationUtils
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContentView(R.layout.activity_main)

    }



}