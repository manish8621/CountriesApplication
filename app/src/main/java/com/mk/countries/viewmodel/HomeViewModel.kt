package com.mk.countries.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mk.countries.model.api.Network
import com.mk.countries.model.api.dataTransferObjects.CountryItem
import com.mk.countries.model.db.DatabaseEntities
import com.mk.countries.model.db.getDatabase
import com.mk.countries.model.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class HomeViewModel(application: Application): AndroidViewModel(application) {
    val database = getDatabase(application)
    val repository = Repository(database)

    val countryItemsList = repository.countries

    init {
        viewModelScope.launch {
            refreshCountriesList()
        }
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

            withContext(Dispatchers.IO)
            {
                repository.refreshRepository()
                Log.i("TAG","inside search")
            }
    }

}
