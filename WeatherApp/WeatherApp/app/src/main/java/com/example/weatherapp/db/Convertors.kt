package com.example.weatherapp.db

import androidx.room.TypeConverter
import com.example.weatherapp.models.Current
import com.example.weatherapp.models.Forecast
import com.example.weatherapp.models.Location
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Convertors {

    private val gson = Gson()

    // Convert Current object to JSON string
    @TypeConverter
    fun fromCurrent(current: Current?): String? {
        return current?.let { gson.toJson(it) }
    }

    // Convert JSON string to Current object
    @TypeConverter
    fun toCurrent(currentString: String?): Current? {
        return currentString?.let {
            val type = object : TypeToken<Current>() {}.type
            gson.fromJson(it, type)
        }
    }

    // Convert Forecast object to JSON string
    @TypeConverter
    fun fromForecast(forecast: Forecast?): String? {
        return forecast?.let { gson.toJson(it) }
    }

    // Convert JSON string to Forecast object
    @TypeConverter
    fun toForecast(forecastString: String?): Forecast? {
        return forecastString?.let {
            val type = object : TypeToken<Forecast>() {}.type
            gson.fromJson(it, type)
        }
    }

    // Convert Location object to JSON string
    @TypeConverter
    fun fromLocation(location: Location?): String? {
        return location?.let { gson.toJson(it) }
    }

    // Convert JSON string to Location object
    @TypeConverter
    fun toLocation(locationString: String?): Location? {
        return locationString?.let {
            val type = object : TypeToken<Location>() {}.type
            gson.fromJson(it, type)
        }
    }
}
