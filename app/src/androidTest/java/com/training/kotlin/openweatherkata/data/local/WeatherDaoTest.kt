package com.training.kotlin.openweatherkata.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.training.kotlin.loginmvvm.data.local.WeatherItemDatabase
import com.training.kotlin.loginmvvm.data.local.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class WeatherDaoTest {


    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: WeatherItemDatabase

    private lateinit var dao: WeatherDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherItemDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.weatherDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertShoppingItem() = runBlockingTest {
        val item = WeatherItem("6:00 AM", "6:00 PM", 21.0, "sunny", "city","url",id = 1)
        dao.insertWeatherItem(item)

        val allShoppingItem = dao.observeAllWeatherItem().getOrAwaitValue()

        assertThat(allShoppingItem).contains(item)

    }

    @Test
    fun deleteShoppingItem() = runBlockingTest {
        val item = WeatherItem("6:00 AM", "6:00 PM", 21.0, "sunny", "city","url",id = 1)
        dao.insertWeatherItem(item)
        val allShoppingItem = dao.observeAllWeatherItem().getOrAwaitValue()

        assertThat(allShoppingItem).doesNotContain(item)

    }


}