package com.training.kotlin.openweatherkata.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.training.kotlin.openweatherkata.MainActivity
import com.training.kotlin.openweatherkata.MainViewModel
import com.training.kotlin.openweatherkata.R
import com.training.kotlin.openweatherkata.commons.Resource
import com.training.kotlin.openweatherkata.data.local.WeatherItem
import com.training.kotlin.openweatherkata.data.model.WeatherResponse
import com.training.kotlin.openweatherkata.databinding.FragmentHomeBinding
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        viewModel = (activity as MainActivity).mainViewModel

        viewModel.fetchData.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()

                    response.data?.let { weatherResponse ->
                        Log.e("TAG1", "weatherResponse is $weatherResponse")
                        setValues(weatherResponse)
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        showErrorMessage(message)
                    }
                }
            }
        })

        return root
    }

    private fun setValues(weatherResponse: WeatherResponse) {

        val temp = weatherResponse.main?.temp
        binding.txtTemp.text = " $temp \u2103"
        val iconUrl =
            "https://openweathermap.org/img/w/${weatherResponse.weather.get(0).icon}.png"


        val sdf = SimpleDateFormat("h:mm aa")

        val sunriseTime = weatherResponse.sys!!.sunrise!!.toLong()
        val sunsetTime = weatherResponse.sys!!.sunset!!.toLong()

        val sunset: String = sdf.format(Date(sunsetTime * 1000))
        val sunrise: String = sdf.format(Date(sunriseTime * 1000))

        binding.txtCity.text = weatherResponse.name
        binding.txtDesc.text = weatherResponse.weather.get(0).description!!.capitalize()
        binding.txtSunrise.text = "Sunrise : " + sunrise
        binding.txtSunset.text = "Sunset : " + sunset

        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        if (currentHour >= 18 && weatherResponse.weather.get(0).main!! == "Clear") {
            Glide.with(this).load(R.drawable.moon).into(binding.iconTemp)
        } else
            Glide.with(this).load(iconUrl).into(binding.iconTemp)

        var weatherItems = WeatherItem(
            sunrise,
            sunset,
            temp!!,
            weatherResponse.weather.get(0).description!!.capitalize(),
            weatherResponse.name!!,
            iconUrl
        )

//        insertItemToDatabase(weatherItems)
    }

    private fun insertItemToDatabase(weatherItems: WeatherItem) {
        viewModel.insertWeatherData(weatherItems)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun hideProgressBar() {
        binding.progress.visibility = View.INVISIBLE
    }

    private fun showProgressBar() {
        binding.progress.visibility = View.VISIBLE
    }

    private fun showErrorMessage(message: String) {
        Toast.makeText(activity, "An error occurred: $message", Toast.LENGTH_SHORT).show()

    }
}