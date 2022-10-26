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
import com.mk.countries.model.db.asDomainModel
import com.mk.countries.model.db.asDomainModels
import com.mk.countries.model.domain.DomainModels
import kotlinx.coroutines.*
import retrofit2.HttpException
import java.util.*

class Repository(private val database:CountryDatabase) {
    private val _countries = MutableLiveData<List<DatabaseEntities.CountryItem>>()
    val countries:LiveData<List<DomainModels.CountryItem>> = Transformations.map(_countries){
        it.asDomainModels()
    }

    val airQuality = MutableLiveData<Int>(0)

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
                //clear the old data
                database.countryDao.deleteAll()
                database.countryDao.resetId()
                //insert new data
                database.countryDao.insertAll(*(networkResult.asDatabaseModels()))
                refreshList()
            }
            catch (e:Exception){
                Log.e("TAG",(e.message?:" error "))
            }
        }

    }
    fun getCountryItemByIdAsync(id:Long):Deferred<DomainModels.CountryItem>
    {
        return CoroutineScope(Dispatchers.IO).async{
            database.countryDao.getCountry(id).asDomainModel()
        }
    }
    suspend fun getAirQuality(lat:Double,lon:Double)
    {
        CoroutineScope(Dispatchers.IO).launch{

            try {
                Log.i("TAG","inside repos get airqa")

                val weatherResult = Network.weatherApiService.getAirQuality(lat,lon).await()
                this@Repository.airQuality.postValue(weatherResult.qualityList[0].airQualiltyIndexContainer.aqi)
                Log.i("TAG",weatherResult.qualityList[0].airQualiltyIndexContainer.aqi.toString())
            }
            catch (e:Exception){
                Log.i("TAG","exception in calling weather api")
                return@launch
            }
        }
    }
}