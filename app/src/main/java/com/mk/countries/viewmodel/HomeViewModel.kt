package com.mk.countries.viewmodel

import android.app.Application
import android.location.Address
import android.location.Location
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.mk.countries.model.db.CountryDatabase.Companion.getDatabase
import com.mk.countries.model.db.asDomainModels
import com.mk.countries.model.repository.Repository
import kotlinx.coroutines.*

class HomeViewModel(application: Application): AndroidViewModel(application) {
    val database = getDatabase(application)
    private val repository = Repository(database)

    val countryItemsList = Transformations.map(repository.countries){ it.asDomainModels() }

    //check if weather load requested at first time after opening app
    private var weatherLoadRequested = false

    //to know if user returned from settings page after granting permission
    var isSentToSettings = false
    //to track if searching operation in progress
    var searching = false



    //to track if recycler view list is loaded
    val isListLoading=MutableLiveData(false)
    var location = MutableLiveData<Location>()
    val weather = repository.weather // replace with empty live data

    init {
        viewModelScope.launch {
            refreshCountriesList()
        }
    }

    fun isWeatherLoadRequested():Boolean = weatherLoadRequested
    fun weatherLoadRequested() {
        weatherLoadRequested = true
    }

    //invoked by fragment observe when we get gps location
    fun updateWeather(location: Location){
        CoroutineScope(Dispatchers.IO).launch {
            weather.postValue(repository.getWeatherFromApi(location.latitude,location.longitude))
        }
    }

    fun setLoadingStatus(flag:Boolean){
        isListLoading.value = flag
    }

    fun searchInList(filter:String){
        viewModelScope.launch {
            repository.search(filter)
        }
    }

    //refreshes the repository
    fun refreshCountriesList()
    {
        isListLoading.postValue(true)
            val job = CoroutineScope(Dispatchers.IO).launch{
                repository.refreshRepository()
            }
        job.invokeOnCompletion {
            isListLoading.postValue(false)
        }
    }

}
