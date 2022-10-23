package com.mk.countries.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mk.countries.model.domain.DomainModels

class DetailsViewModelFactory(val id: Long,val application: Application):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(DetailsViewModel::class.java))
            return DetailsViewModel(id, application) as T
        throw IllegalArgumentException("Illegal args on details factory")
    }
}