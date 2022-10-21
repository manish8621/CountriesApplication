package com.mk.countries.model.api

import com.squareup.moshi.JsonClass


data class CountryContainer(
    val countryItemList:ArrayList<CountryItem>
)