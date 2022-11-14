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
    private val searchQuery = MutableLiveData("")
    val countries = Transformations.switchMap(searchQuery){ database.countryDao.getCountriesList(it) }

    val weather = MutableLiveData<DomainModels.Weather>()

    fun search(searchQuery:String){
        this.searchQuery.value = searchQuery
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


    suspend fun getWeatherFromApi(lat:Double,lon:Double):DomainModels.Weather? = withContext(Dispatchers.IO){
            var weather :DomainModels.Weather? = null
            try {
                Log.i("TAG","inside repos get weather")

                val weatherResult = Network.weatherApiService.getWeather(lat,lon, API_KEY_WEATHERBIT).await()

                weather = (weatherResult.asDomainModel())
            }
            catch (e:Exception){
                Log.i("TAG","exception in calling weather api ${e.message}")
            }
        return@withContext weather
        }
}