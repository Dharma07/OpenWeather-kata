package com.training.kotlin.openweatherkata.data.repository

import com.anonymous.shopping.utils.BaseUnitTest
import com.google.common.truth.Truth.assertThat
import com.training.kotlin.openweatherkata.data.local.WeatherItem
import com.training.kotlin.openweatherkata.data.model.WeatherResponse
import com.training.kotlin.openweatherkata.data.repository.datasource.WeatherLocalDataSource
import com.training.kotlin.openweatherkata.data.repository.datasource.WeatherRemoteDataSource
import io.mockk.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class WeatherRepositoryShould : BaseUnitTest() {

    private val weatherLocalDataSource = mockk<WeatherLocalDataSource>()
    private val weatherRemoteDataSource = mockk<WeatherRemoteDataSource>()
    private lateinit var repository: WeatherRepository

    @Before
    fun setUp() {
        repository = WeatherRepository(weatherLocalDataSource, weatherRemoteDataSource)
    }

    @Test
    fun getProductsFromAPI() = runTest {
        coEvery { weatherRemoteDataSource.getWeatherDetails(appId = "appid") } returns Response.success(
            WeatherResponse()
        )
        val item = WeatherItem("6:00 AM", "6:00 PM", 21.0, "sunny", "city", "url", id = 1)
        coEvery { weatherLocalDataSource.saveWeatherToDB(item) } just runs
        repository.getWeatherData(0.0, 0.0, appId = "appid")
        coVerify { weatherRemoteDataSource.getWeatherDetails(appId = "appid") }
    }

    @Test
    fun saveProductData() = runTest {
        val item = WeatherItem("6:00 AM", "6:00 PM", 21.0, "sunny", "city", "url", id = 1)
        coEvery { weatherLocalDataSource.saveWeatherToDB(item) } just runs
        repository.insertWeatherData(item)
        coVerify { weatherLocalDataSource.saveWeatherToDB(item) }
    }

    @Test
    fun getProductsFromDBWhenDBHasValues() = runTest {
        val product = mockProduct()

        coEvery { weatherLocalDataSource.getWeatherFromDB() } returns flow {
            emit(listOf(product))
        }

        repository.getWeatherListLocal()

        assertThat(repository.getWeatherListLocal().first()).isEqualTo(listOf(product))
    }

    private fun mockProduct(): WeatherItem {
        val mockProduct = WeatherItem(
            "6:00 AM",
            "6:00 PM",
            21.0,
            "sunny",
            "city",
            "url",
            id = 1
        )
        return mockProduct
    }
}