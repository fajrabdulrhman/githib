package com.example.weatherapp.db

import com.example.weatherapp.models.SearchResponse
import com.example.weatherapp.models.WeatherResponse
import com.example.weatherapp.util.Constants.Companion.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {


    @GET("forecast.json")
    suspend fun getWeather(
    @Query("key")
    apiKey:String=API_KEY,
    @Query("q")
    location:String,
    @Query("days")
    days:Int=5
    ):Response<WeatherResponse>

    @GET("search.json")
    suspend fun searchForCountry(
        @Query("key")
        apiKey:String=API_KEY,
        @Query("q")
        countryName:String,
    ):Response<SearchResponse>

}