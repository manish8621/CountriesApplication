package com.mk.countries.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class HomeViewModelFactory(private val application: Application):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(HomeViewModel::class.java))
        {
            return HomeViewModel(application) as T
        }
        throw IllegalArgumentException("IllegalArgument for viewModel factory")
    }
}