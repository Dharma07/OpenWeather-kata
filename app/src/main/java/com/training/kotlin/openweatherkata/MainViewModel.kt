package com.training.kotlin.openweatherkata

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.training.kotlin.openweatherkata.commons.NetworkHelper
import com.training.kotlin.openweatherkata.commons.Resource
import com.training.kotlin.openweatherkata.data.local.WeatherItem
import com.training.kotlin.openweatherkata.data.model.WeatherResponse
import com.training.kotlin.openweatherkata.data.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mainRepository: WeatherRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {


    val fetchData: MutableLiveData<Resource<WeatherResponse>> = MutableLiveData()
    var listResponse: WeatherResponse? = null

    val fetchLocalData: MutableLiveData<List<WeatherItem>> = MutableLiveData()
    val _fetchLocalData: LiveData<List<WeatherItem>> = fetchLocalData

    fun getWeatherData(lat: Double, long: Double, appId: String, city: String, country: String) =
        viewModelScope.launch {
            safeWeatherDataCall(lat, long, appId, city, country)
        }

    private suspend fun safeWeatherDataCall(
        lat: Double,
        long: Double,
        appId: String,
        city: String,
        country: String
    ) {
        fetchData.postValue(Resource.Loading())
        try {
            if (networkHelper.isNetworkConnected()) {
                val response = mainRepository.getWeatherData(lat, long, appId)

                fetchData.postValue(handleWeatherDataResponse(response))
            } else {
                fetchData.postValue(Resource.Error("No internet connection"))
            }

        } catch (t: Throwable) {
            Log.e("TAG", "RESPONSE VALUE IS ERROR ==>" + t.message)
            when (t) {
                is IOException -> fetchData.postValue(Resource.Error("Network Failure"))
                else -> fetchData.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private fun handleWeatherDataResponse(response: Response<WeatherResponse>): Resource<WeatherResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                if (listResponse == null) {
                    listResponse = resultResponse
                }
                setValuesToDb(resultResponse)
                return Resource.Success(listResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun insertWeatherData(weatherItem: WeatherItem) {

        Log.e("TAG1", "Weather date is $weatherItem")
        viewModelScope.launch {
            mainRepository.insertWeatherData(weatherItem)
        }
    }


    fun getAllWeatherData() = viewModelScope.launch {
        mainRepository.getWeatherListLocal().collect { values ->
            fetchLocalData.value = values
        }
    }


    private fun setValuesToDb(weatherResponse: WeatherResponse) {

        val temp = weatherResponse.main?.temp
        val iconUrl =
            "https://openweathermap.org/img/w/${weatherResponse.weather.get(0).icon}.png"



        val sdf = SimpleDateFormat("h:mm aa")
        val sunriseTime = weatherResponse.sys!!.sunrise!!.toLong()
        val sunsetTime = weatherResponse.sys!!.sunset!!.toLong()

        val sunset: String = sdf.format(Date(sunsetTime * 1000))
        val sunrise: String = sdf.format(Date(sunriseTime * 1000))

        var weatherItems = WeatherItem(
            sunrise,
            sunset,
            temp!!,
            weatherResponse.weather.get(0).description!!.capitalize(),
            weatherResponse.name!!,
            iconUrl
        )

        insertWeatherData(weatherItems)
    }
}