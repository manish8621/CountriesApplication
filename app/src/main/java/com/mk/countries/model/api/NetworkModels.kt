package com.mk.countries.model.api

import android.util.Log
import com.mk.countries.model.db.DatabaseEntities
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
        @Json(name="flag") val flag : String,
        @Json(name="independent") val independent : Boolean
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
            currencies = it.currencies.toString(),
            languages = it.languages.map { language ->language.name.filter { c-> c!='('&&c!=')'&&c!='['&&c!=']'&&c!=' ' } },
            flag = it.flag,
            independent = it.independent
        )
    }.toTypedArray()
}