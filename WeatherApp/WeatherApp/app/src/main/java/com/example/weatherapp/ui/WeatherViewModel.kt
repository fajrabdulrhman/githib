package com.example.weatherapp.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.TYPE_ETHERNET
import android.net.ConnectivityManager.TYPE_WIFI
import android.net.NetworkCapabilities.TRANSPORT_CELLULAR
import android.net.NetworkCapabilities.TRANSPORT_ETHERNET
import android.net.NetworkCapabilities.TRANSPORT_WIFI
import android.os.Build
import android.provider.ContactsContract.CommonDataKinds.Email.TYPE_MOBILE
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.models.Current
import com.example.weatherapp.models.Forecast
import com.example.weatherapp.models.Location
import com.example.weatherapp.models.SearchResponse
import com.example.weatherapp.models.WeatherResponse
import com.example.weatherapp.repository.WeatherRepository
import com.example.weatherapp.util.Resource
import com.example.weatherapp.util.WeatherApplication
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class WeatherViewModel(
    app:Application,
    val weatherRepository: WeatherRepository) : AndroidViewModel(app) {
    val gettingWeather: MutableLiveData<Resource<WeatherResponse>> = MutableLiveData()
    var gettingResponse: WeatherResponse? = null

    var searchWeather: MutableLiveData<Resource<SearchResponse>> = MutableLiveData()
    var searchResponse:SearchResponse? = null
    //function to executes our API call from repository
    fun getWeather(latitude:Double?,longitude:Double?) = viewModelScope.launch {
//        gettingWeather.postValue(Resource.Loading())
        val location = "$latitude,$longitude"
//        val response = weatherRepository.getWeather(location, days = 5)
//        gettingWeather.postValue(handelGettingWeatherResponse(response))
        safeBreakingWeatherCall(location, days = 5)
    }


    fun searchWeather(countryName:String)=viewModelScope.launch {
//        searchWeather.postValue(Resource.Loading())
//        val response = weatherRepository.searchWeather(countryName)
//        searchWeather.postValue(handelSearchWeatherResponse(response))
        Log.d("fah", "$searchWeather")
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
        Log.d("saveWeatherr","$weatherResponse")
        weatherRepository.upsert(weatherResponse)
    }
    fun getSavedWeather()=weatherRepository.getSavedWeather()

    fun deleteCounty(weatherResponse: WeatherResponse)=viewModelScope.launch {
        weatherRepository.deleteWeather(weatherResponse)
    }

    private suspend fun safeSearchWeatherCall(searchQuery: String) {
        searchWeather.postValue(Resource.Loading())
        try {
            if(hasInternetConnection()) {
                val response = weatherRepository.searchWeather(searchQuery)
                searchWeather.postValue(handelSearchWeatherResponse(response))
            } else {
                searchWeather.postValue(Resource.Error("No internet connection"))
            }
        } catch(t: Throwable) {
            when(t) {
                is IOException -> searchWeather.postValue(Resource.Error("Network Failure"))
                else -> searchWeather.postValue(Resource.Error("Conversion Error"))
            }
        }
    }
    private suspend fun safeBreakingWeatherCall(location: String,days:Int=5) {
        Log.d("fffffffffffffffffff","$location")
        gettingWeather.postValue(Resource.Loading())
        try {
            if(hasInternetConnection()) {

                val response = weatherRepository.getWeather(location, days )
        gettingWeather.postValue(handelGettingWeatherResponse(response))
            } else {
//                gettingWeather.postValue(Resource.Error("No internet connection"))
                val localWeatherData=weatherRepository.getSavedWeather().value
                if (localWeatherData!= null&&localWeatherData.isNotEmpty()){

                    gettingWeather.postValue(Resource.Success(localWeatherData.last()))
                }
            }
        } catch(t: Throwable) {
            when(t) {
                is IOException -> gettingWeather.postValue(Resource.Error("Network Failure"))
                else -> gettingWeather.postValue(Resource.Error("Conversion Error"))
            }
        }
    }
    private fun hasInternetConnection(): Boolean {
        val connectivityManger = getApplication<WeatherApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        )as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManger.activeNetwork ?: return false
            val capabilities =
                connectivityManger.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }

        } else {
            connectivityManger.activeNetworkInfo?.run {
                return when (type) {
                    TYPE_MOBILE -> true
                    TYPE_WIFI ->true
                    TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false


    }

}