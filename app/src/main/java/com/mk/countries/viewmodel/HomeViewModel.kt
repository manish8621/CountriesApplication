package com.mk.countries.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mk.countries.model.api.CountryContainer
import com.mk.countries.model.api.CountryItem
import com.mk.countries.model.api.Network
import kotlinx.coroutines.*
import retrofit2.HttpException

class HomeViewModel(application: Application): AndroidViewModel(application) {
    var countryItemsList = MutableLiveData<List<CountryItem>>()
    val viewModelJob = SupervisorJob()
    val viewModelScope = CoroutineScope(viewModelJob + Dispatchers.IO)

    init {
        viewModelScope.launch {

//            refreshList()
        }
    }

    private fun refreshList() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val result = Network.countriesApi.getCountriesList().body()
                    countryItemsList.value = result?.countryItemList
                } catch (e: HttpException) {
                    return@withContext
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
