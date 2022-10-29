package com.mk.countries.model.util

import android.content.Context
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity

class NetUtils (private val context: Context) {
    fun isConnectedToInternet(): Boolean {
        var flag = false
        val connectivity =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivity.activeNetwork?.let {
            flag = true
        }
        return flag
    }
}