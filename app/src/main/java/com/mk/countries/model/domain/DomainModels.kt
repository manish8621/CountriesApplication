package com.mk.countries.model.domain

import com.mk.countries.model.api.NetworkModels


class DomainModels {
    data class CountryItem (
        val id:Long,
        val name : String,
        val callingCodes : String,
        val capital : String,
        val region : String,
        val population : Int,
        val latitudeLongitude : String,
        val timezones : String,
        val currencies : String,
        val languages : String,
        val flag : String,
        val independent : String
    )
    data class Weather(
        val cityName:String,
        val temp:Double,
        val weatherDesc:String ,
        val weatherIcon:String,
        val aqi:Int
    )
}