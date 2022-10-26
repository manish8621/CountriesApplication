package com.mk.countries.model.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.mk.countries.model.api.dataTransferObjects.CountryItem
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

private const val BASE_URL = "https://restcountries.com/"
private const val WEATHER_BASE_URL = "https://api.openweathermap.org/"
private const val API_KEY = "44b30a7b56db33121782f34b7b1d5275"


interface CountryApiService {
    @GET("v2/all")
    fun getCountriesList():Deferred<List<NetworkModels.CountryItem>>
    @GET("v2/name/{filter}")
    fun searchCountries(@Path("filter")filter: String): Deferred<List<NetworkModels.CountryItem>>
}

interface WeatherApiService {
    @GET("data/2.5/air_pollution?lat={lat}&lon={lon}&appid=$API_KEY")
    fun getAirQuality(lat:Double,lon:Double):Deferred<NetworkModels.AirQualiltyResult>
}

private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

object Network{
    private val retrofit = Retrofit.Builder().baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    val countriesApiService: CountryApiService = retrofit.create(CountryApiService::class.java)

    val weatherApiService:WeatherApiService = Retrofit.Builder().baseUrl(WEATHER_BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build().create(WeatherApiService::class.java)
}