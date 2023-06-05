package com.training.kotlin.openweatherkata

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.training.kotlin.openweatherkata.commons.Constant
import com.training.kotlin.openweatherkata.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    val mainViewModel: MainViewModel by viewModels()

    //For getting the location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    private lateinit var city: String
    private lateinit var country: String

    companion object {
        private const val LOCATION_REQUEST = 1001
    }

    private var permissionsRequired = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        getCurrentLocationLogic()
    }

    private fun getCurrentLocationLogic() {

        val permission = checkPermissions()

        when {
            permission -> {
                locationSettingBuilder()
            }
            else -> {
                requestPermissions()
            }
        }
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false

    }

    private fun requestPermissions() {
        requestLocationPermissions.launch(permissionsRequired)
    }

    private val requestLocationPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->

            if (!permissions.containsValue(false)) {
                // all permissions are accepted
                locationSettingBuilder()

            } else {
                showToastMsg("All permissions are not accepted")
            }

        }

    private fun locationSettingBuilder() {
        createLocationRequest()
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnCompleteListener { task ->
            try {
                val response = task.getResult(ApiException::class.java)

                getLocationValues()
            } catch (exception: ApiException) {
                when (exception.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->                                     // Location settings are not satisfied. But could be fixed by showing the
                        // user a dialog.
                        try {
                            // Cast to a resolvable exception.
                            val resolvable = exception as ResolvableApiException
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            resolvable.startResolutionForResult(
                                this@MainActivity,
                                LOCATION_REQUEST
                            )
                        } catch (e: IntentSender.SendIntentException) {
                            // Ignore the error.
                        } catch (e: ClassCastException) {
                            // Ignore, should be an impossible error.
                        }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {

                    }
                }
            }
        }
    }

    private fun createLocationRequest() {
        locationRequest = com.google.android.gms.location.LocationRequest.create().apply {
            interval = 10
            smallestDisplacement = 0F
            fastestInterval = 0
            priority = com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    private fun getLocationValues() {
        locationCallBackMethod()
        startLocationUpdates()
    }

    private fun locationCallBackMethod() {

        showProgressLoad()
        //location update call back
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (locationResult != null) {
                    for (location in locationResult.locations) {

                        latitude = location.latitude
                        longitude = location.longitude

                        try {
                            getAddress(latitude, longitude)
                        } catch (e: Exception) {
                            city = " "
                            country = " "
                            stopLocationUpdates()
                            dismissProgressLoad()
                            if (latitude != 0.0 && longitude != 0.0)
                                callWeatherApi()
                        }

                    }
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Unable to get the location",
                        Toast.LENGTH_SHORT
                    ).show()
                    dismissProgressLoad()
                }
            }
        }

    }


    private fun getAddress(latitude: Double, longitude: Double) {
        showProgressLoad()
        val mainLooper = Looper.getMainLooper()

        GlobalScope.launch {

            val geocoder = Geocoder(this@MainActivity, Locale.getDefault())

            try {
                val list: List<Address> =
                    geocoder.getFromLocation(latitude, longitude, 1)!!

                if (list != null && list.size > 0) {
                    city = list[0].locality
                    country = list[0].countryName

                }
                Handler(mainLooper).post {
                    dismissProgressLoad()
                    stopLocationUpdates()

                    if (latitude != 0.0 && longitude != 0.0 )
                        callWeatherApi()
                }

            } catch (e: Exception) {
                dismissProgressLoad()
                stopLocationUpdates()
                city = " "
                country = " "
                if (latitude != 0.0 && longitude != 0.0)
                    callWeatherApi()
            }
        }
    }

    private fun dismissProgressLoad() {
        binding.progress.visibility = View.GONE
    }

    private fun showProgressLoad() {
        binding.progress.visibility = View.VISIBLE
    }

    private fun callWeatherApi() {
        mainViewModel.getWeatherData(latitude, longitude, Constant.appId,city,country)
        dismissProgressLoad()
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun showToastMsg(message: String?) {
        Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != RESULT_CANCELED) {
            val states = LocationSettingsStates.fromIntent(data!!)
            when (requestCode) {

                LOCATION_REQUEST -> when (resultCode) {
                    RESULT_OK -> {
                        getLocationValues()
                    }
                    RESULT_CANCELED ->
                        Toast.makeText(
                            this@MainActivity, "Canceled",
                            Toast.LENGTH_SHORT
                        ).show()
                    else -> {}
                }
            }
        }
    }
}