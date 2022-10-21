package com.mk.countries.model.api

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CountryItem(
    val name:String
)