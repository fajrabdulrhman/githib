package com.example.weatherapp.repository

import android.util.Log
import com.example.weatherapp.api.RetrofitInstance
import com.example.weatherapp.db.WeatherDatabase
import com.example.weatherapp.models.SearchResponse
import com.example.weatherapp.models.WeatherResponse
import com.example.weatherapp.util.Constants.Companion.API_KEY
import com.example.weatherapp.util.Resource
import okhttp3.internal.wait
import retrofit2.Response

class WeatherRepository (
    val db:WeatherDatabase
){
    //function to get data from api
    suspend fun getWeather(location:String,days:Int): Response<WeatherResponse> {
        val res=RetrofitInstance.api.getWeather(API_KEY, location, days)
        Log.d("fffffffffff","$res")
        return  res

    }
    suspend fun searchWeather(countryName:String):Response<SearchResponse>{
        val ser=RetrofitInstance.api.searchForCountry(API_KEY,countryName)
        Log.d("repoo","$ser")

        return ser
    }
    suspend fun upsert(weather:WeatherResponse)=db.getWeatherDao().upsert(weather)

    fun getSavedWeather()=db.getWeatherDao().getAllWeathers()

    suspend fun deleteWeather(weather: WeatherResponse)=db.getWeatherDao().deleteCountry(weather)
}