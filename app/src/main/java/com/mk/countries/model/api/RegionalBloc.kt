package com.mk.countries.model.api

data class RegionalBloc(
    val acronym: String,
    val name: String,
    val otherAcronyms: List<String>,
    val otherNames: List<String>
)