package com.example.weatherapp.repository

import androidx.lifecycle.LiveData
import com.example.weatherapp.models.SearchResponse
import com.example.weatherapp.models.WeatherResponse
import com.example.weatherapp.util.Constants.Companion.API_KEY
import dagger.Provides
import retrofit2.Response
import javax.inject.Inject

interface WeatherRepo{



    suspend fun getWeather(location:String,days:Int): Response<WeatherResponse>
    suspend fun searchWeather(countryName:String): Response<SearchResponse>
    suspend fun upsert(weather: WeatherResponse):Long
    fun getSavedWeather():LiveData<List<WeatherResponse>>

    suspend fun deleteWeather(weather: WeatherResponse)

}