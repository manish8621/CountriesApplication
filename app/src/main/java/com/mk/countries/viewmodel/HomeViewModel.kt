package com.mk.countries.viewmodel

import android.app.Application
import android.location.Address
import android.location.Location
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.mk.countries.model.api.Network
import com.mk.countries.model.api.dataTransferObjects.CountryItem
import com.mk.countries.model.db.DatabaseEntities
import com.mk.countries.model.db.getDatabase
import com.mk.countries.model.repository.Repository
import kotlinx.coroutines.*
import retrofit2.HttpException

class HomeViewModel(application: Application): AndroidViewModel(application) {
    val database = getDatabase(application)
    val repository = Repository(database)

    val countryItemsList = repository.countries
    val isLoading=MutableLiveData(false)
    var address = MutableLiveData<Address>()
    val airQualityIndex = repository.airQuality

    init {
        viewModelScope.launch {
            refreshCountriesList()
        }

    }
    fun getAirQuality(address: Address){
        CoroutineScope(Dispatchers.IO).launch{ Log.i("TAG","inside viewmodel aqa lat${address.latitude}");repository.getAirQuality(address.latitude, address.longitude) }
    }
    fun setLoadingStatus(flag:Boolean){
        isLoading.value = flag
    }

    fun searchInList(filter:String){
        if(filter.isEmpty()) return
        viewModelScope.launch {

            if(filter.lowercase() == "all")
                repository.refreshList()
            else
                repository.filterList(filter)
        }
    }

    //by default show all countries without filter
    private suspend fun refreshCountriesList()
    {
        isLoading.postValue(true)
            val job = CoroutineScope(Dispatchers.IO).launch{
                repository.refreshRepository()
                Log.i("TAG","inside search")
            }
        job.invokeOnCompletion {
            isLoading.postValue(false)
        }
    }

}
