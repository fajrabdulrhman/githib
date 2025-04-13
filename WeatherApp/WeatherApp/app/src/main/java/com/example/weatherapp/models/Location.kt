package com.example.weatherapp.models

import java.io.Serializable

data class Location(
    val country: String,
    val lat: Double,
    val lon: Double,
    val localtime: String,
    val localtime_epoch: Int,
    val name: String,
    val region: String,
    val tz_id: String
)