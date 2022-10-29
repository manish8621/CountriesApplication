package com.mk.countries.viewmodel

import android.app.Application
import android.location.Address
import android.location.Location
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mk.countries.model.db.getDatabase
import com.mk.countries.model.repository.Repository
import kotlinx.coroutines.*

class HomeViewModel(application: Application): AndroidViewModel(application) {
    val database = getDatabase(application)
    val repository = Repository(database)

    val countryItemsList = repository.countries


    //internet status
    private val _internetStatus = MutableLiveData(false)
    val internetStatus :LiveData<Boolean>
    get() = _internetStatus

    private var weatherLoaded = false
    //recycler view
    val isListLoading=MutableLiveData(false)

    var location = MutableLiveData<Location>()
    var address = MutableLiveData<Address>()
    val weather = repository.weather // replace with empty live data

    init {
        viewModelScope.launch {
            refreshCountriesList()
        }
    }
    fun isWeatherLoaded():Boolean = weatherLoaded
    fun weatherLoaded() {
        weatherLoaded = true
    }
    fun changeInternetStatus(status:Boolean)
    {
        _internetStatus.value = status
    }
    //invoked by fragment observe when we get gps location
    fun updateWeather(location: Location){
//        CoroutineScope(Dispatchers.IO).launch{
//            Log.i("TAG","inside viewmodel aqa lat${location.latitude}");
//            repository.updateWeather(location.latitude, location.longitude)
//        }
        CoroutineScope(Dispatchers.IO).launch {
            weather.postValue(repository.getWeatherFromApi(location.latitude,location.longitude))
        }
    }

    fun setLoadingStatus(flag:Boolean){
        isListLoading.value = flag
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
        isListLoading.postValue(true)
            val job = CoroutineScope(Dispatchers.IO).launch{
                repository.refreshRepository()
                Log.i("TAG","inside search")
            }
        job.invokeOnCompletion {
            isListLoading.postValue(false)
        }
    }

    fun getCoOrdinates(){
        address.value
    }

}
