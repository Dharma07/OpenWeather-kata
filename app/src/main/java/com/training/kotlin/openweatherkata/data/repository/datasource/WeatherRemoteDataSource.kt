package com.training.kotlin.openweatherkata.data.repository.datasource

import com.training.kotlin.openweatherkata.data.api.MyApi
import com.training.kotlin.openweatherkata.data.model.WeatherResponse
import retrofit2.Response
import javax.inject.Inject

class WeatherRemoteDataSource @Inject constructor(
    private val myApi: MyApi
) {
    suspend fun getWeatherDetails(
        lat: Double=0.0,
        long: Double=0.0,
        appId: String
    ): Response<WeatherResponse> = myApi.getWeather(lat,long,appId)
}