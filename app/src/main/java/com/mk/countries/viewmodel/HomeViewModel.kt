package com.mk.countries.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mk.countries.model.api.Network
import com.mk.countries.model.api.dataTransferObjects.CountryItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class HomeViewModel(application: Application): AndroidViewModel(application) {
    var countryItemsList = MutableLiveData<List<CountryItem>>()

    init {
            refreshList()
    }

    private fun refreshList() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {

                    val result = Network.countriesApiService.getCountriesList().await()
                    if(result.isNotEmpty())
                        countryItemsList.value = result

                } catch (e: HttpException) {
                    return@withContext
                }
            }
        }
    }


}
