package com.mk.countries.model.domain

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

class DomainModels {
    data class CountryItem (
        val name : String,
        val callingCodes : List<String>,
        val capital : String ,
        val region : String,
        val population : Int,
        val latitudeLongitude : String,
        val timezones : List<String>,
        val currencies : List<String> = listOf(),
        val languages : List<String> = listOf(),
        val flag : String,
        val independent : Boolean
    )
}