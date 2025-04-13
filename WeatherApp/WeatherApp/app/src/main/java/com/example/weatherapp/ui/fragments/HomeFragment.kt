package com.example.weatherapp.ui.fragments

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.example.weatherapp.R
import com.example.weatherapp.adapters.DayAdapter
import com.example.weatherapp.adapters.WeekAdapter
//import com.example.weatherapp.adapters.DayAdapter
//import com.example.weatherapp.adapters.DayAdapter
import com.example.weatherapp.api.RetrofitInstance
import com.example.weatherapp.api.WeatherApi
import com.example.weatherapp.db.WeatherDatabase
import com.example.weatherapp.models.Current
import com.example.weatherapp.models.Forecast
import com.example.weatherapp.models.Forecastday
import com.example.weatherapp.repository.WeatherRepository
import com.example.weatherapp.ui.ViewModelProviderFactory
import com.example.weatherapp.ui.WeatherViewModel
import com.example.weatherapp.util.Constants.Companion.API_KEY
import com.example.weatherapp.util.Resource
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var savedButton: ImageButton
    private lateinit var locationManager: LocationManager
    private lateinit var dayAdapter: DayAdapter
    private lateinit var weekAdapter: WeekAdapter
    private lateinit var displayWeek:RecyclerView
    lateinit var diplayDay: RecyclerView
    private lateinit var viewModel: WeatherViewModel
    private lateinit var hum:TextView
    private lateinit var Image:ImageView
    private lateinit var date:TextView
    private lateinit var wind:TextView
    //    private lateinit var degree:TextView
    private lateinit var degree:TextView
    private lateinit var cityName:TextView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view,savedInstanceState)

        degree=view.findViewById(R.id.degreeTv)
        val  weatherRepository= WeatherRepository(WeatherDatabase(requireContext()))
        val application = requireActivity().application
        val viewModelProviderFactory= ViewModelProviderFactory(application, weatherRepository)
        viewModel= ViewModelProvider(this,viewModelProviderFactory).get(WeatherViewModel::class.java)
        diplayDay = view.findViewById(R.id.todaysRecyclerView)
        Image=view.findViewById(R.id.todayImage)
        wind=view.findViewById(R.id.TvWind)
        date=view.findViewById(R.id.monthTv)
        displayWeek=view.findViewById(R.id.displayDays)
        hum=view.findViewById(R.id.humTv)
        cityName=view.findViewById(R.id.cityTv)


        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout.setOnRefreshListener {
            getLocation()
        }
        observe()
        // Initialize LocationManager
        locationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        savedButton = view.findViewById(R.id.saveButton)
        savedButton.setBackgroundColor(Color.TRANSPARENT)
        savedButton.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_savedCitiesFragment)
        }

        // Register permission request launcher
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) ||
                        permissions.getOrDefault(
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            false
                        ) -> {
                    // Permissions granted
                    Log.d("sef", "${getLocation()}")
                    getLocation()
                }

                else -> {
                    // No location access granted
                    Toast.makeText(requireContext(), "Location access denied.", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }

        // Check if permissions are already granted
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            // Show rationale if needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) ||
                ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            ) {

                AlertDialog.Builder(requireContext())
                    .setTitle("Location Permission Required")
                    .setMessage("This app needs location access to provide accurate weather information based on your current location. Please grant the required permissions.")
                    .setPositiveButton("OK") { _, _ ->
                        // Launch permission request
                        locationPermissionRequest.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                    .setNegativeButton("Cancel") { dialog, _ ->
                        Toast.makeText(
                            requireContext(),
                            "Please grant location access.",
                            Toast.LENGTH_LONG
                        ).show()
                        dialog.dismiss()
                    }
                    .create()
                    .show()

            } else {
                // No rationale needed, just request the permissions
                locationPermissionRequest.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        } else {
            // Permissions already granted, get location
            getLocation()
        }


        viewModel.gettingWeather.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    response.data?.let { weatherResponse ->
                        val currentWeather = weatherResponse.current
                        val location = weatherResponse.location
                        val forecastDays = weatherResponse.forecast.forecastday
                        Log.d("FAjr", "Weather data fetched: $weatherResponse")
                        setUI(currentWeather, location , forecastDays)

                    }

                }

                is Resource.Error -> {
                    Toast.makeText(
                        requireContext(),
                        "fofo: ${response.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
//
                is Resource.Loading -> {
                    Toast.makeText(requireContext(),"loading",Toast.LENGTH_LONG).show()

                }
            }
        })

    }


    private fun setUI(current: Current, location: com.example.weatherapp.models.Location, forcastDays: List<Forecastday>
    ) {
        Log.d("setUI","$current")
        setupRecyclerView()
        degree.text=current.temp_c.toString()

        Glide.with(this).load("https:${current.condition.icon}").into((Image))
        wind.text=current.wind_kph.toString()
        hum.text=current.humidity.toString()
        dayAdapter.differ.submitList(forcastDays[0].hour)
        weekAdapter.differ.submitList(forcastDays)
        cityName.text=location.name
//        val dayNmonth=location.localtime
//        val inputFormatter= DateTimeFormatter(dayNmonth)
//          val dateTime=LocalDateTime.parse(dayNmonth,inputFormatter)
        Log.d("setUI","$location")

    }

    private fun getLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            // Check if location services are enabled
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                    LocationManager.NETWORK_PROVIDER
                )
            ) {
                val location: Location? =
                    locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                Log.d("NOUR"," location: $location")
                viewModel.getWeather(location?.latitude, location?.longitude)
                if (location != null) {
                    // Fallback to network provider if GPS location is not available
                    val networkLocation: Location? =
                        locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    Log.d("NOUR"," network loc: $networkLocation")
                    if (networkLocation != null) {
                        val latitude = networkLocation.latitude
                        val longitude = networkLocation.longitude
                        Log.d("NOUR"," lon and lat: $latitude,$longitude")
                        Toast.makeText(
                            requireContext(),
                            "Location:\nLatitude: $latitude\nLongitude: $longitude",
                            Toast.LENGTH_LONG
                        ).show()
//
                        val locationn = " $latitude,$longitude"
                        Log.i("fffffffffffff", "getLocation: $locationn")

                            viewModel.getWeather(latitude, longitude)


                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Unable to find location using available providers.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } else {
                // Location services are disabled, prompt user to enable them
                AlertDialog.Builder(requireContext())
                    .setTitle("Location Services Disabled")
                    .setMessage("Please enable location services in your device settings.")
                    .setPositiveButton("Settings") { _, _ ->
                        startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                    }
                    .setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                    .show()
            }
        } else {
            Toast.makeText(requireContext(), "Location permission not granted.", Toast.LENGTH_SHORT)
                .show()
        }
    }



    private fun setupRecyclerView() {
        dayAdapter = DayAdapter()
        diplayDay.apply {
            adapter = dayAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        }
        weekAdapter= WeekAdapter()
        displayWeek.apply {
            adapter=weekAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }


    }

    private fun observe(){
        Log.d("HomeFragment", "entered observe function ")
        viewModel.gettingWeather.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    response.data?.let { weatherResponse ->
                        val currentWeather = weatherResponse.current
                        val location = weatherResponse.location
                        val forecastDays = weatherResponse.forecast.forecastday
                        Log.d("HomeFragment", "Weather data fetched: $weatherResponse")
                        viewModel.saveWeather(weatherResponse)
                        setUI(currentWeather, location , forecastDays)

                    }

                }

                is Resource.Error -> {
                    Log.d("HomeFragment", "error: ${response.message}")
                    Toast.makeText(
                        requireContext(),
                        "Error: ${response.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
//
                is Resource.Loading -> {
                    Log.d("HomeFragment", "loading: ${response.message}")
                    Toast.makeText(requireContext(),"loading",Toast.LENGTH_LONG).show()

                }
            }
        })
    }

}



