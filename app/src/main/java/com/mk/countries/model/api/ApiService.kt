package com.mk.countries.model.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.mk.countries.model.api.dataTransferObjects.CountryItem
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

private const val BASE_URL = "https://restcountries.com/"


interface CountryApiService {
    @GET("v2/all")
    fun getCountriesList():Deferred<List<NetworkModels.CountryItem>>
}

private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

object Network{
    private val retrofit = Retrofit.Builder().baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()
    val countriesApiService: CountryApiService = retrofit.create(CountryApiService::class.java)
}