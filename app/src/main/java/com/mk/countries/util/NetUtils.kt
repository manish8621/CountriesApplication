package com.mk.countries.util

import android.content.Context
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity

    fun isConnectedToInternet(context: Context): Boolean {
        val connectivity =context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivity.activeNetwork != null
    }