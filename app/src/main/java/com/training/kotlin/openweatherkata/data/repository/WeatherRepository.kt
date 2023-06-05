package com.training.kotlin.openweatherkata.data.repository

import androidx.lifecycle.LiveData
import com.training.kotlin.openweatherkata.data.local.WeatherItem
import com.training.kotlin.openweatherkata.data.model.WeatherResponse
import com.training.kotlin.openweatherkata.data.repository.datasource.WeatherLocalDataSource
import com.training.kotlin.openweatherkata.data.repository.datasource.WeatherRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import javax.inject.Inject

class WeatherRepository @Inject constructor(private val weatherLocalDataSource: WeatherLocalDataSource,
                                             private val weatherRemoteDataSource: WeatherRemoteDataSource
){

    suspend fun getWeatherData(lat: Double ,long :Double,appId: String) =
        weatherRemoteDataSource.getWeatherDetails(lat,long,appId)


     fun getWeatherListLocal(): Flow<List<WeatherItem>> {
        lateinit var productList: Flow<List<WeatherItem>>
        try {
            productList = weatherLocalDataSource.getWeatherFromDB()
        } catch (e: Exception){
            e.printStackTrace()
        }

        return productList
    }

    suspend fun insertWeatherData(weatherData: WeatherItem) = weatherLocalDataSource.saveWeatherToDB(weatherData)
}