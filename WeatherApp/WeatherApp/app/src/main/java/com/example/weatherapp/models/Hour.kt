package com.example.weatherapp.models

data class Hour(
    val wind_kph: Double,
    val humidity: Double,
    val condition: Condition,
    val time:String,
    val temp_c:Double
)
