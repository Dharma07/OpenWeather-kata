package com.training.kotlin.openweatherkata.data.repository.datasource

import androidx.lifecycle.LiveData
import com.training.kotlin.openweatherkata.data.local.WeatherDao
import com.training.kotlin.openweatherkata.data.local.WeatherItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class WeatherLocalDataSource @Inject constructor(private val weatherDao: WeatherDao) {

    fun getWeatherFromDB(): Flow<List<WeatherItem>> {
        return weatherDao.getWeatherItemsProducts()
    }

    suspend fun saveWeatherToDB(products: WeatherItem) {
        CoroutineScope(Dispatchers.IO).launch {
            weatherDao.insertWeatherItem(products)
        }
    }

    suspend fun clearAll() {
        CoroutineScope(Dispatchers.IO).launch {
            weatherDao.deleteAllProducts()
        }
    }




}
