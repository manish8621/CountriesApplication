package com.mk.countries.model.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.mk.countries.model.api.Network
import com.mk.countries.model.api.NetworkModels
import com.mk.countries.model.api.asDatabaseModels
import com.mk.countries.model.db.CountryDatabase
import com.mk.countries.model.db.DatabaseEntities
import com.mk.countries.model.db.asDomainModels
import com.mk.countries.model.domain.DomainModels
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.util.*

class Repository(val database:CountryDatabase) {
    val countries:LiveData<List<DomainModels.CountryItem>> = Transformations.map(database.countryDao.getCountriesList()){
        it.asDomainModels()
    }
    suspend fun refreshList(filter:String){
        Log.e("TAG","refresh in repo invoked")

        withContext(Dispatchers.IO)
        {
            try{
                val deferred = if(filter.lowercase(Locale.ROOT) =="all")
                    Network.countriesApiService.getCountriesList()
                else
                    Network.countriesApiService.searchCountries(filter)

                val _countries:List<NetworkModels.CountryItem> = deferred.await()
                database.countryDao.deleteAll()
                database.countryDao.insertAll(*(_countries.asDatabaseModels()))
            }
            catch (e:java.net.SocketTimeoutException){
                Log.e("TAG","No internet")
            }
            catch (e:Exception){
                Log.e("TAG",(e.message?:" error filter = ")+filter)
            }
        }

    }
}