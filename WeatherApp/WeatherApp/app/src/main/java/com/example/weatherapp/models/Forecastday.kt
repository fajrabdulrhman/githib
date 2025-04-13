package com.example.weatherapp.models

data class Forecastday(
    val date: String,
    val day: Day,
    val hour: List<Hour>
)