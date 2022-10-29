package com.mk.countries.model.api

import android.util.Log
import com.mk.countries.model.db.DatabaseEntities
import com.mk.countries.model.domain.DomainModels
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

class NetworkModels {

    @JsonClass(generateAdapter = true)
    data class CountryItem (

        @Json(name="name") val name : String,
        @Json(name="callingCodes") val callingCodes : List<String>,
        @Json(name="capital") val capital : String="no-capital",
        @Json(name="region") val region : String,
        @Json(name="population") val population : Int,
        @Json(name="latlng") val latlng : List<Double> = listOf(),
        @Json(name="timezones") val timezones : List<String>,
        @Json(name="currencies") val currencies : List<Currencies> = listOf(),
        @Json(name="languages") val languages : List<Languages> = listOf(),
        @Json(name="flags") val flag : Flag,
        @Json(name="independent") val independent : Boolean
    )
    @JsonClass(generateAdapter = true)
    data class Flag(
        val png:String
    )

    @JsonClass(generateAdapter = true)
    data class Currencies (

        @Json(name="code") val code : String,
        @Json(name="name") val name : String,
        @Json(name="symbol") val symbol : String
    )
    @JsonClass(generateAdapter = true)
    data class Languages (
        @Json(name="name") val name : String
    )


    //air quality
    @JsonClass(generateAdapter = true)
    data class AirQualityResult (
        @Json(name="list") val list : List<AirQualiltyList>
    )
    @JsonClass(generateAdapter = true)
    data class AirQualiltyList (

        @Json(name="main") val airQualityIndexContainer : AirQualityIndexContainer,
    )
    data class  AirQualityIndexContainer(
        @Json(name = "aqi") val aqi : Int
    )
    //weather
    @JsonClass(generateAdapter = true)
    data class WeatherResult(
        @Json(name = "data") val dataList:List<Data>
    )
    @JsonClass(generateAdapter = true)
    data class Data(
        @Json(name = "weather") val weather:Weather,
        @Json(name = "aqi") val aqi:Int,
        @Json(name = "city_name") val cityName:String,
        @Json(name = "temp") val temperature:Double
    )
    @JsonClass(generateAdapter = true)
    data class Weather(
        @Json(name="icon") val icon :String,
        @Json(name="description") var description:String
    )
}
//to remove the brackets in string
//it will throw MalformedJsonException while parsing string to array
fun String.filterExtraChars():String{
    val replaceWith = '_'
    return filter { c-> c!='('&&c!=')'&&c!='['&&c!=']'&&c!=',' }.replace(' ',replaceWith)
}

fun List<NetworkModels.CountryItem>.asDatabaseModels():Array<DatabaseEntities.CountryItem>
{
    Log.e("TAG","asDbModel invoked")
    return map {
        DatabaseEntities.CountryItem(
            name = it.name,
            callingCodes = it.callingCodes.toString(),
            capital = it.capital,
            region = it.region,
            population =it.population,
            lattitudeLongitude = it.latlng.toString(),
            timezones = it.timezones.toString(),
            currencies = it.currencies.map{currency -> currency.name.filterExtraChars()},
            languages = it.languages.map {language ->language.name.filterExtraChars()},
            flag = it.flag.png,
            independent = it.independent
        )
    }.toTypedArray()
}

fun NetworkModels.WeatherResult.asDomainModel():DomainModels.Weather{
    val data = this.dataList[0]
    return DomainModels.Weather(
        cityName = data.cityName,
        temp=data.temperature,
        weatherDesc = data.weather.description,
        weatherIcon = data.weather.icon,
        aqi = data.aqi
    )
}