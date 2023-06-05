package com.training.kotlin.openweatherkata.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.training.kotlin.openweatherkata.data.local.WeatherItem
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherItem(weatherItem: WeatherItem)


    @Query("DELETE FROM weather_items")
    suspend fun deleteAllProducts()


    @Query("SELECT * FROM weather_items")
    fun getWeatherItemsProducts(): Flow<List<WeatherItem>>

}