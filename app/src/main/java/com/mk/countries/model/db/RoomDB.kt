package com.mk.countries.model.db

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import com.mk.countries.model.db.DatabaseEntities.*


@Dao
interface CountryDao{
    @Query("SELECT * FROM countries_table")
    fun getCountriesList(): LiveData<List<DatabaseEntities.CountryItem>>

    @Query("SELECT * FROM countries_table where name like '%' || :country || '%' ")
    fun search(country:String): LiveData<List<DatabaseEntities.CountryItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg countryItem: CountryItem)

    @Query("DELETE from countries_table")
    fun deleteAll()
}

@Database(entities = [CountryItem::class], version = 1, exportSchema = false)
@TypeConverters(ListConverter::class)
abstract class CountryDatabase:RoomDatabase()
{
    abstract val countryDao:CountryDao
}

private lateinit var INSTANCE:CountryDatabase

fun getDatabase(context: Context):CountryDatabase{

        if (!(::INSTANCE.isInitialized)){
            INSTANCE = Room.databaseBuilder(
                context,
                CountryDatabase::class.java,
                "country_database"
            ).fallbackToDestructiveMigration().build()
        }
        return INSTANCE

}
