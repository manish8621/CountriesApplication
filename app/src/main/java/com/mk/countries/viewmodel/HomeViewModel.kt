package com.mk.countries.viewmodel

import android.app.Application
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
    suspend fun refreshCountriesList()
    {

            withContext(Dispatchers.IO)
            {
                repository.refreshList()
            }
    }

//    private fun refreshList() {
//        viewModelScope.launch {
//                try {
//                    val result = Network.countriesApiService.getCountriesList().await()
//                    if(result.isNotEmpty()) countryItemsList.value = result
//                } catch (e: HttpException) {
//                    return@launch
//                }
//        }
//    }
}
