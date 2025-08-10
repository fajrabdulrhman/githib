package com.example.weatherapp.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.weatherapp.models.SearchResponse
import com.example.weatherapp.models.WeatherResponse
import com.example.weatherapp.repository.WeatherRepo

import com.example.weatherapp.util.Resource
import com.example.weatherapp.util.WeatherApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    val weatherRepository: WeatherRepo) : ViewModel()

 {
    val gettingWeather: MutableLiveData<Resource<WeatherResponse>> = MutableLiveData()
    var gettingResponse: WeatherResponse? = null

    var searchWeather: MutableLiveData<Resource<SearchResponse>> = MutableLiveData()
    var searchResponse:SearchResponse? = null
    //function to executes our API call from repository
    fun getWeather(latitude:Double?,longitude:Double?) =
        viewModelScope.launch {

        val location = "$latitude,$longitude"

        safeBreakingWeatherCall(location, days = 5)
    }


    fun searchWeather(countryName:String)=viewModelScope.launch {

      //  Log.d("fah", "$searchWeather")
        safeSearchWeatherCall(countryName)

    }

    private fun handelGettingWeatherResponse(response: Response<WeatherResponse>): Resource<WeatherResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->

                gettingResponse = resultResponse


                return Resource.Success(gettingResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handelSearchWeatherResponse(response: Response<SearchResponse>): Resource<SearchResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                if (searchResponse != null) {
                    searchResponse = resultResponse
                }
                return Resource.Success(searchResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }
    fun saveWeather(weatherResponse: WeatherResponse)=viewModelScope.launch {
       // Log.d("saveWeatherr","$weatherResponse")
        weatherRepository.upsert(weatherResponse)
    }
    fun getSavedWeather()=weatherRepository.getSavedWeather()

    fun deleteCounty(weatherResponse: WeatherResponse)=viewModelScope.launch {
        weatherRepository.deleteWeather(weatherResponse)
    }

    private suspend fun safeSearchWeatherCall(searchQuery: String) {
       // searchWeather.postValue(Resource.Loading())
        try {
                val response = weatherRepository.searchWeather(searchQuery)
                searchWeather.postValue(handelSearchWeatherResponse(response))
            }
         catch(t: Throwable) {
            when(t) {
                is IOException -> searchWeather.postValue(Resource.Error("Network Failure"))
                else -> searchWeather.postValue(Resource.Error("Conversion Error"))
            }
        }
    }


    private suspend fun safeBreakingWeatherCall(location: String,days:Int=5) {

        try {

                val response = weatherRepository.getWeather(location, days )
                gettingWeather.postValue(handelGettingWeatherResponse(response))
        }  catch(t: Throwable) {
            when(t) {
                is IOException -> {
                    val localWeatherData = weatherRepository.getSavedWeather().value
                    if (localWeatherData != null && localWeatherData.isNotEmpty()) {
                        gettingWeather.postValue(Resource.Success(localWeatherData.last()))
                    } else {
                        gettingWeather.postValue(Resource.Error("Network Failure"))
                    }
                }
                else -> gettingWeather.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

}