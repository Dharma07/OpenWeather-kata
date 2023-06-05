package com.training.kotlin.openweatherkata.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_items")
data class WeatherItem(
    var sunrise: String,
    var sunset: String,
    var temp: Double,
    var desc: String,
    var city: String,
    var imageUrl: String,
    @PrimaryKey(
        autoGenerate = true
    )
    val id: Int? = null
)