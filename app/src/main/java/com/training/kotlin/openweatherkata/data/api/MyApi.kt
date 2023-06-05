package com.training.kotlin.openweatherkata.data.api

import com.training.kotlin.openweatherkata.data.model.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MyApi {

    @GET("weather")
    suspend fun getWeather(
        @Query("lat")
        lat: Double = 0.0,
        @Query("lon")
        long: Double = 0.0,
        @Query("appid")
        appId: String,
        @Query("units")
        unit: String="metric"
    ): Response<WeatherResponse>


}