package com.mk.countries.model.api.dataTransferObjects

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Currencies (

	@Json(name="code") val code : String,
	@Json(name="name") val name : String,
	@Json(name="symbol") val symbol : String
)