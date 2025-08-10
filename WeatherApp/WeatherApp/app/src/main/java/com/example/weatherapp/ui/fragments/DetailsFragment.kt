package com.example.weatherapp.ui.fragments

import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
//import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherapp.R
import com.example.weatherapp.adapters.DayAdapter
import com.example.weatherapp.adapters.WeekAdapter
import com.example.weatherapp.models.Current
import com.example.weatherapp.models.Forecastday
import com.example.weatherapp.ui.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsFragment : Fragment(R.layout.fragment_details) {

    private lateinit var savedButton: ImageButton
    private lateinit var locationManager: LocationManager
    private lateinit var dayAdapter: DayAdapter
    private lateinit var weekAdapter: WeekAdapter
    private lateinit var displayWeek: RecyclerView
    lateinit var diplayDay: RecyclerView
    private lateinit var hum: TextView
    private lateinit var Image: ImageView
    private lateinit var date: TextView
    private lateinit var wind: TextView
    //    private lateinit var degree:TextView
    private lateinit var degree: TextView
    private lateinit var cityName: TextView
    private val viewModel:WeatherViewModel by viewModels()

    val args: DetailsFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

       val weather = args.weather

        degree=view.findViewById(R.id.degreeTv)

        val application = requireActivity().application


        diplayDay = view.findViewById(R.id.todaysRecyclerView)
        Image=view.findViewById(R.id.todayImage)
        wind=view.findViewById(R.id.TvWind)
        date=view.findViewById(R.id.monthTv)
        displayWeek=view.findViewById(R.id.displayDays)
        hum=view.findViewById(R.id.humTv)
        cityName=view.findViewById(R.id.cityTv)
        setUI(weather.current, weather.location, weather.forecast.forecastday)
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
}

