package com.mk.countries.model.db

import android.util.Log
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mk.countries.model.api.NetworkModels
import com.mk.countries.model.domain.DomainModels

class DatabaseEntities {
    @Entity(tableName = "countries_table")
    data class CountryItem (
        @PrimaryKey(autoGenerate = true)
        val id:Long=0L,
        val name : String,
        @ColumnInfo(name="calling_codes")
        val callingCodes : String,
        val capital : String="no-capital",
        val region : String,
        val population : Int,
        @ColumnInfo(name="latitude_longitude")
        val lattitudeLongitude : String,
        val timezones : String,
        val currencies : List<String>,
        val languages : List<String>,
        val flag : String,
        val independent : Boolean
    )
}

class ListConverter{
    @TypeConverter
    fun fromString(value:String?):List<String>
    {
        val listType = object : TypeToken<List<String>>() {}.type
        //handle empty lists
//        Log.e("TAG",value?:"null")
        return if (value == null || value.length<=2) listOf<String>() else Gson().fromJson(value,listType)
    }
    @TypeConverter
    fun fromList(value :List<String>):String{
        return value.toString()
    }
}


fun List<DatabaseEntities.CountryItem>.asDomainModels():List<DomainModels.CountryItem>
{
    return map {

        DomainModels.CountryItem(
            id=it.id,
            name = it.name,
            callingCodes = it.callingCodes,
            capital = it.capital,
            region = it.region,
            population =it.population,
            latitudeLongitude = it.lattitudeLongitude,
            timezones = it.timezones,
            currencies = it.currencies.toString(),
            languages = it.languages.toString(),
            flag = it.flag ,
            independent = it.independent
        )
    }
}
fun DatabaseEntities.CountryItem.asDomainModel():DomainModels.CountryItem{
    return DomainModels.CountryItem(
        id=this.id,
        name = this.name,
        callingCodes = this.callingCodes,
        capital = this.capital,
        region = this.region,
        population =this.population,
        latitudeLongitude = this.lattitudeLongitude,
        timezones = this.timezones,
        currencies = this.currencies.toString(),
        languages = this.languages.toString(),
        flag = this.flag ,
        independent = this.independent
    )
}