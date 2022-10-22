package com.mk.countries.model.api.dataTransferObjects


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class CountryItem (

	@Json(name="name") val name : String,
	@Json(name="topLevelDomain") val topLevelDomain : List<String>,
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