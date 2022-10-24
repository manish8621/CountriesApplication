package com.mk.countries.model.domain


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
}