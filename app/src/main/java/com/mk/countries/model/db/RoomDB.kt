package com.mk.countries.model.db

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import com.mk.countries.model.db.DatabaseEntities.*


@Dao
interface CountryDao{
    @Query("SELECT * FROM countries_table where id = :id")
    fun getCountry(id:Long): DatabaseEntities.CountryItem

    @Query("SELECT * FROM countries_table where name like '%' || :country || '%' ")
    fun getCountriesList(country:String): LiveData<List<DatabaseEntities.CountryItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg countryItem: CountryItem)

    @Query("DELETE from countries_table")
    fun deleteAll()
    @Query("DELETE from sqlite_sequence where name = 'countries_table'")
    fun resetId()
}

@Database(entities = [CountryItem::class], version = 2, exportSchema = false)
@TypeConverters(ListConverter::class)
abstract class CountryDatabase:RoomDatabase()
{
    abstract val countryDao:CountryDao
    companion object{
        private lateinit var INSTANCE:CountryDatabase

        fun getDatabase(context: Context):CountryDatabase{
            synchronized(this){
                if (!(::INSTANCE.isInitialized)) {
                    INSTANCE = Room.databaseBuilder(
                        context,
                        CountryDatabase::class.java,
                        "country_database"
                    ).fallbackToDestructiveMigration().build()
                }
                return INSTANCE
            }
        }
    }
}

