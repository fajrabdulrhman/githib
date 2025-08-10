package com.example.weatherapp.fakes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.weatherapp.models.SearchResponse
import com.example.weatherapp.models.WeatherResponse
import com.example.weatherapp.repository.WeatherRepo
import retrofit2.Response
import java.io.IOException

class FakeRepo():WeatherRepo {


     var shouldReturnNetworkError = true



    private val fakeData= mutableListOf<WeatherResponse>()
     private val observableLiveData=MutableLiveData<List<WeatherResponse>>(fakeData)


    override suspend fun getWeather(location: String, days: Int): Response<WeatherResponse> {
        if (shouldReturnNetworkError) {
            throw IOException("No internet")
        } else {
            return Response.success(FakeWeatherResponse.fakeWeatherResponse)
        }

    }

    override suspend fun searchWeather(countryName: String): Response<SearchResponse> {
        if (shouldReturnNetworkError){
                throw IOException("No internet")
            }
        else {
            return Response.success(FakeSearchResponse.searchResponse)
        }
    }

    override suspend fun upsert(weather: WeatherResponse): Long {
        fakeData.add(weather)
        observableLiveData.postValue(fakeData)
        return fakeData.size.toLong()
    }

    override fun getSavedWeather(): LiveData<List<WeatherResponse>> {
        return observableLiveData
    }

    override suspend fun deleteWeather(weather: WeatherResponse) {

       fakeData.remove(weather)
       observableLiveData.postValue(fakeData)
    }
}