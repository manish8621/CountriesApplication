package com.mk.countries.model.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.mk.countries.model.api.Network
import com.mk.countries.model.api.NetworkModels
import com.mk.countries.model.api.asDatabaseModels
import com.mk.countries.model.db.CountryDatabase
import com.mk.countries.model.db.DatabaseEntities
import com.mk.countries.model.db.asDomainModels
import com.mk.countries.model.domain.DomainModels
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.util.*

class Repository(private val database:CountryDatabase) {
    private val _countries = MutableLiveData<List<DatabaseEntities.CountryItem>>()
    val countries:LiveData<List<DomainModels.CountryItem>> = Transformations.map(_countries){
        it.asDomainModels()
    }

    init {
            refreshList()
    }

    fun refreshList(){
        CoroutineScope(Dispatchers.IO).launch {
            _countries.postValue(database.countryDao.getCountriesList())
        }
    }
    fun filterList(filter:String){
        CoroutineScope(Dispatchers.IO).launch {
            _countries.postValue(database.countryDao.search(filter))
        }
    }
    suspend fun refreshRepository(){
        withContext(Dispatchers.IO)
        {
            try{
                val networkResult:List<NetworkModels.CountryItem> = Network.countriesApiService.getCountriesList().await()
                database.countryDao.insertAll(*(networkResult.asDatabaseModels()))
                refreshList()
            }
            catch (e:Exception){
                Log.e("TAG",(e.message?:" error "))
            }
        }
    }
}