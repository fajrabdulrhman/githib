package com.example.weatherapp.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherapp.models.WeatherResponse

@Dao
interface WeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(weatherResponse: WeatherResponse):Long


    @Query("SELECT * FROM weather_db")
    fun getAllWeathers():  LiveData<List<WeatherResponse>>

    @Delete
    suspend fun deleteCountry(weatherResponse: WeatherResponse)
}