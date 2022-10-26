package com.mk.countries.model.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
        //Log.e("TAG",value?:"null")
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
        it.asDomainModel()
    }
}
fun DatabaseEntities.CountryItem.asDomainModel():DomainModels.CountryItem{
    return DomainModels.CountryItem(
        id=this.id,
        name = this.name,
        callingCodes = this.callingCodes.replace(' ',',').filter { c-> c !='[' && c!=']' },
        capital = this.capital,
        region = this.region,
        population =this.population,
        latitudeLongitude = this.lattitudeLongitude.filter { c-> c !='[' && c!=']' },
        timezones = this.timezones.filter { c-> c !='[' && c!=']' },
        currencies = this.currencies.toString().filter { c-> c !='[' && c!=']' },
        languages = this.languages.toString().filter { c-> c !='[' && c!=']' },
        flag = this.flag ,
        independent = if(this.independent) "Yes" else "No"
    )
}