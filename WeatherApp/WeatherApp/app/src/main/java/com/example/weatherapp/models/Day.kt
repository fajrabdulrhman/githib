package com.example.weatherapp.models

data class Day(
    val avghumidity: Int,
    val avgtemp_c: Double,
    val condition: Condition,
    val maxwind_kph: Double,
    val uv: Int
)