package com.example.weatherapp.repository

import android.annotation.SuppressLint
import com.example.weatherapp.db.WeatherApi
import com.example.weatherapp.db.WeatherDatabase
import com.example.weatherapp.models.SearchResponse
import com.example.weatherapp.models.WeatherResponse
import com.example.weatherapp.util.Constants.Companion.API_KEY
import retrofit2.Response
import javax.inject.Inject

class WeatherRepository @Inject constructor (
    private val db:WeatherDatabase,
    private val weatherApi: WeatherApi,

    ):WeatherRepo{
    @SuppressLint("ServiceCast")


    //function to get data from api
    override suspend fun getWeather(location:String, days:Int): Response<WeatherResponse> {

        return  weatherApi.getWeather(API_KEY,location,days)

    }
    override suspend fun searchWeather(countryName:String):Response<SearchResponse>{
//        val ser=RetrofitInstance.api.searchForCountry(API_KEY,countryName)
//        Log.d("repoo","$ser")
           return  weatherApi.searchForCountry(API_KEY,countryName)


    }

    override suspend fun upsert(weather: WeatherResponse): Long =db.getWeatherDao().upsert(weather)


    override fun getSavedWeather()=db.getWeatherDao().getAllWeathers()


    override suspend fun deleteWeather(weather: WeatherResponse)=db.getWeatherDao().deleteCountry(weather)


}