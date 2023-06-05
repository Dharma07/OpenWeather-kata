package com.training.kotlin.loginmvvm.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.training.kotlin.openweatherkata.data.local.WeatherDao
import com.training.kotlin.openweatherkata.data.local.WeatherItem

@Database(
    entities = [WeatherItem::class],
    version = 1
)
abstract class WeatherItemDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
}