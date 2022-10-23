package com.mk.countries.model.db.databaseModels

import androidx.room.*

@Entity(tableName = "countries_table")
data class DatabaseCountryItem (
	@PrimaryKey
	val name : String,
	val topLevelDomain : List<String>,
	val callingCodes : List<String>,
	val capital : String="no-capital",
	val region : String,
	val population : Int,
	val latlng : List<Double> = listOf(),
	val timezones : List<String>,
	@Embedded
	val currencies : List<Currencies> = listOf(),
	@Embedded
	val languages : List<Languages> = listOf(),
	val flag : String,
	val independent : Boolean
)

