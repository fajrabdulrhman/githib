package com.example.weatherapp.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity(tableName = "weather_db",
    indices = [Index(value = ["locationlat","locationlon"], unique = true)]
    )

data class WeatherResponse(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val current: Current,
    val forecast: Forecast,
    @Embedded(prefix = "location")
    val location: Location
): Serializable