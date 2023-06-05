package com.training.kotlin.openweatherkata.data.repository.datasource

import com.anonymous.shopping.utils.BaseUnitTest
import com.training.kotlin.openweatherkata.data.api.MyApi
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class WeatherRemoteDataSourceShould: BaseUnitTest() {

    @Test
    fun getProductsFromAPIService() = runTest {
        val productService = mockk<MyApi>()
        val productRemoteDataSource = WeatherRemoteDataSource(productService)

        coEvery { productService.getWeather(appId = "appid") } returns mockk()
        productRemoteDataSource.getWeatherDetails(appId = "appid")

        coVerify { productService.getWeather(appId = "appid") }
    }
}