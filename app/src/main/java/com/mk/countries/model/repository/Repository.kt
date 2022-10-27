package com.mk.countries.model.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.mk.countries.model.api.*
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

    val weather = MutableLiveData<DomainModels.Weather>()

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

    suspend fun getWeather(lat:Double,lon:Double)
    {
        CoroutineScope(Dispatchers.IO).launch{

            try {
                Log.i("TAG","inside repos get weather")

                val weatherResult = Network.weatherApiService.getWeather(lat,lon, API_KEY_WEATHERBIT).await()

                this@Repository.weather.postValue(weatherResult.asDomainModel())

                Log.i("TAG","aqi:"+weatherResult.dataList[0].aqi.toString())
            }
            catch (e:Exception){
//                this@Repository.airQuality.postValue(2)
                Log.i("TAG","exception in calling weather api ${e.message}")
                return@launch
            }
        }
    }
    suspend fun getWeather1(lat:Double,lon:Double):DomainModels.Weather? = withContext(Dispatchers.IO){
        var weather :DomainModels.Weather? = null
            try {
                Log.i("TAG","inside repos get weather")

                val weatherResult = Network.weatherApiService.getWeather(lat,lon, API_KEY_WEATHERBIT).await()

                weather = (weatherResult.asDomainModel())
            }
            catch (e:Exception){
//                this@Repository.airQuality.postValue(2)
                Log.i("TAG","exception in calling weather api ${e.message}")
            }
        return@withContext weather
        }
}