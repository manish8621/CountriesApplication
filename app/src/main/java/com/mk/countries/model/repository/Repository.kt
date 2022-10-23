package com.mk.countries.model.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.mk.countries.model.api.Network
import com.mk.countries.model.api.NetworkModels
import com.mk.countries.model.api.asDatabaseModels
import com.mk.countries.model.db.CountryDatabase
import com.mk.countries.model.db.DatabaseEntities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Repository(val database:CountryDatabase) {
    val countries:LiveData<List<DatabaseEntities.CountryItem>> = (database.countryDao.getCountriesList())
    suspend fun refreshList(){
        Log.e("TAG","refresh in repo invoked")

        withContext(Dispatchers.IO)
        {
            try{
                val _countries:List<NetworkModels.CountryItem> = Network.countriesApiService.getCountriesList().await()
                database.countryDao.insertAll(*(_countries.asDatabaseModels()))
            }
            catch (e:Exception){
                Log.e("TAG",e.message?:"error")
                return@withContext
            }
        }
    }
}