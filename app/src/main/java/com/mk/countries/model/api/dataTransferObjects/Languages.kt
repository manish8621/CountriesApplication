package com.mk.countries.model.api.dataTransferObjects


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Languages (
	@Json(name="name") val name : String
)