package com.mk.countries.model.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

private const val BASE_URL = "https://restcountries.com/"
private const val WEATHER_BASE_URL = "https://api.openweathermap.org/"
private const val WEATHERBIT_BASE_URL = "https://api.weatherbit.io/"

const val API_KEY = "44b30a7b56db33121782f34b7b1d5275"
const val API_KEY_WEATHERBIT = "702ebf54ea5e4b90a0380fc15100f535"


interface CountryApiService {
    @GET("v2/all")
    fun getCountriesList():Deferred<List<NetworkModels.CountryItem>>
    @GET("v2/name/{filter}")
    fun searchCountries(@Path("filter")filter: String): Deferred<List<NetworkModels.CountryItem>>
}

interface WeatherApiService {
    @GET("v2.0/current")
    fun getWeather(@Query("lat") lat:Double,@Query("lon") lon:Double,@Query("key") apikey:String):Deferred<NetworkModels.WeatherResult>
}

private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

object Network{
    private val retrofit = Retrofit.Builder().baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    val countriesApiService: CountryApiService = retrofit.create(CountryApiService::class.java)

    val weatherApiService:WeatherApiService = Retrofit.Builder().baseUrl(WEATHERBIT_BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build().create(WeatherApiService::class.java)
}