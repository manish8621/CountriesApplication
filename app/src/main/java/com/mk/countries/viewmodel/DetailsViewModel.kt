package com.mk.countries.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mk.countries.model.db.getDatabase
import com.mk.countries.model.domain.DomainModels.CountryItem
import com.mk.countries.model.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailsViewModel(id: Long,application: Application): AndroidViewModel(application) {
    var countryDetails = MutableLiveData<CountryItem>()
    private val database = getDatabase(application)
    private val repository = Repository(database)
    init {
        viewModelScope.launch {
            getCountryItem(id)
        }
    }
    private suspend fun getCountryItem(id:Long) {
        val countryItem = repository.getCountryItemById(id).await()
        countryDetails.postValue(countryItem)
    }
}