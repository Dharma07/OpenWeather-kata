package com.training.kotlin.openweatherkata.data.repository.datasource

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.anonymous.shopping.utils.BaseUnitTest
import com.training.kotlin.openweatherkata.data.local.WeatherDao
import com.training.kotlin.openweatherkata.data.local.WeatherItem
import io.mockk.*
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Test

class WeatherLocalDataSourceShould: BaseUnitTest() {

    @Test
    fun getProductsFromLocalDataSource() = runTest {
        val productDao = mockk<WeatherDao>()
        val productLocalDataSource = WeatherLocalDataSource(productDao)

        coEvery { productDao.getWeatherItemsProducts() } returns flow {  }
        productLocalDataSource.getWeatherFromDB()

        coVerify { productDao.getWeatherItemsProducts() }
    }

    @Test
    fun saveProductsToLocalDataSource() = runTest {
        val productDao = mockk<WeatherDao>()
        val productLocalDataSource = WeatherLocalDataSource(productDao)
        val item = WeatherItem("6:00 AM", "6:00 PM", 21.0, "sunny", "city","url",id = 1)

        coEvery { productDao.insertWeatherItem(item) } just runs
        productLocalDataSource.saveWeatherToDB(item)

        coVerify { productDao.insertWeatherItem(item) }
    }

    @Test
    fun deleteProductsFromLocalDataSource() = runTest {
        val productDao = mockk<WeatherDao>()
        val productLocalDataSource = WeatherLocalDataSource(productDao)

        coEvery { productDao.deleteAllProducts() } just runs
        productLocalDataSource.clearAll()

        coVerify { productDao.deleteAllProducts() }
    }


}