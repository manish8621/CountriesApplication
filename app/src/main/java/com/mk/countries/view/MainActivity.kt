package com.mk.countries.view

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.mk.countries.R
import com.mk.countries.model.util.LocationUtils

class MainActivity : AppCompatActivity() {
    lateinit var locationUtils: LocationUtils
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContentView(R.layout.activity_main)

//        if(!checkInternet())
//            Toast.makeText(this, "No internet", Toast.LENGTH_SHORT).show()
    }

    fun checkInternet(): Boolean {
        val conManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = conManager.activeNetworkInfo
        return activeNetwork!=null && activeNetwork.isConnected
    }
}