package com.mk.countries.viewmodel

import android.app.Application
import android.location.Address
import android.location.Location
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mk.countries.model.db.getDatabase
import com.mk.countries.model.repository.Repository
import kotlinx.coroutines.*

class HomeViewModel(application: Application): AndroidViewModel(application) {
    val database = getDatabase(application)
    val repository = Repository(database)

    val countryItemsList = repository.countries
    //recycler view
    val isLoading=MutableLiveData(false)
    var location = MutableLiveData<Location>()
    var address = MutableLiveData<Address>()
    val weather = repository.weather
    private var weatherLoaded = false

    init {
        viewModelScope.launch {
            refreshCountriesList()
        }

    }
    fun isWeatherLoaded():Boolean = weatherLoaded
    fun weatherLoaded() {
        weatherLoaded = true
    }
    //invoked by fragment observe when we get gps location
    fun getWeather(location: Location){
        CoroutineScope(Dispatchers.IO).launch{
            Log.i("TAG","inside viewmodel aqa lat${location.latitude}");
            repository.getWeather(location.latitude, location.longitude)
        }
    }

    fun setLoadingStatus(flag:Boolean){
        isLoading.value = flag
    }

    fun searchInList(filter:String){
        viewModelScope.launch {

            if(filter.isEmpty() || filter.lowercase() == "all")
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

    fun getCoOrdinates(){
        address.value
    }

}
