package com.example.weatherapp.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.weatherapp.MainCoroutineRule
import com.example.weatherapp.fakes.FakeRepo
import com.example.weatherapp.fakes.FakeWeatherResponse
import com.example.weatherapp.getOrAwaitValueTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import com.example.weatherapp.util.Resource
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain


@ExperimentalCoroutinesApi
class WeatherViewModelTest{
// for live data to make it sync

    private val testDispatcher = StandardTestDispatcher()

    @get:Rule
    var instantTaskExecutorRule= InstantTaskExecutorRule()


//    @get:Rule
//    var mainCoroutineRule = MainCoroutineRule()


   private lateinit var viewModel : WeatherViewModel


    @Before
    fun setup()
    {
        viewModel =WeatherViewModel(FakeRepo())
        Dispatchers.setMain(testDispatcher)
    }


    @Test
    fun get_weather_with_valid_coordinates_return_success ()  = runTest{
val viewModel=WeatherViewModel(FakeRepo())
        viewModel.getWeather(30.0,31.0)


        val value=viewModel.gettingWeather.getOrAwaitValueTest()

        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(value is Resource.Success)
    }


    @Test
    fun get_weather_with_null_coordinates_returns_error_returns_error ()  = runTest{

        viewModel.getWeather( null,null )
        val value =viewModel.gettingWeather.getOrAwaitValueTest()

        assertThat(value is Resource.Error)


    }

    @Test
    fun search_for_country_with_valid_name_returns_success ()  = runTest{
        viewModel.searchWeather("Egypt")
        val value =viewModel.searchWeather.getOrAwaitValueTest()

        assertThat(value is Resource.Success)
    }


    @Test
    fun search_for_country_with_invalid_name_returns_error ()  = runTest {

        viewModel.searchWeather("")
        val value =viewModel.searchWeather.getOrAwaitValueTest()

        assertThat(value is Resource.Error)
    }

    @Test
    fun saved_weather_can_be_retrieved() = runTest{
        viewModel.saveWeather(FakeWeatherResponse.fakeWeatherResponse)
        val value= viewModel.getSavedWeather().getOrAwaitValueTest()

        assertThat(value).contains(FakeWeatherResponse.fakeWeatherResponse)
    }




    @Test
    fun searchWeather_noInternet_throwsIOException_returnsNetworkFailureError()  = runTest {

       val fakeRepo= FakeRepo()
        fakeRepo.shouldReturnNetworkError=true
        val viewModel = WeatherViewModel(fakeRepo)

        viewModel.searchWeather("Cairo")

        val result = viewModel.searchWeather.getOrAwaitValueTest()
        assertThat(result is Resource.Error)
    }



    @Test
    fun deleteWeather_removesFromSavedList()  = runTest {

        viewModel.saveWeather(FakeWeatherResponse.fakeWeatherResponse)
        viewModel.deleteCounty(FakeWeatherResponse.fakeWeatherResponse)

        val savedList=viewModel.getSavedWeather().getOrAwaitValueTest()
        assertThat(FakeWeatherResponse.fakeWeatherResponse).isNotIn(savedList)
    }



}