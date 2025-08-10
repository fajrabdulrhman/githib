package com.example.weatherapp.fakes

import com.example.weatherapp.models.Condition
import com.example.weatherapp.models.Current
import com.example.weatherapp.models.Day
import com.example.weatherapp.models.Forecast
import com.example.weatherapp.models.Forecastday
import com.example.weatherapp.models.Hour
import com.example.weatherapp.models.Location
import com.example.weatherapp.models.WeatherResponse


object FakeWeatherResponse {

    val fakeWeatherResponse = WeatherResponse(
        id = 1,
        current = Current(
            last_updated = "2025-08-02 12:00",
            temp_c = 29.5,
            is_day = 1,
            wind_kph = 15.0,
            humidity = 60,
            condition = Condition(
                code = "1000",
                icon = "//cdn.weatherapi.com/weather/64x64/day/113.png"
            )
        ),
        forecast = Forecast(
            forecastday = mutableListOf(
                Forecastday(
                    date = "2025-08-02",
                    day = Day(
                        avghumidity = 55,
                        avgtemp_c = 28.0,
                        maxwind_kph = 20.0,
                        uv = 7.5,
                        condition = Condition(
                            code = "1003",
                            icon = "//cdn.weatherapi.com/weather/64x64/day/116.png"
                        )
                    ),
                    hour = listOf(
                        Hour(
                            time = "2025-08-02 09:00",
                            temp_c = 26.0,
                            wind_kph = 12.0,
                            humidity = 65.0,
                            condition = Condition(
                                code = "1006",
                                icon = "//cdn.weatherapi.com/weather/64x64/day/119.png"
                            )
                        )
                    )
                )
            )
        ),
        location = Location(
            country = "Egypt",
            lat = 30.0444,
            lon = 31.2357,
            localtime = "2025-08-02 12:00",
            localtime_epoch = 1627905600,
            name = "Cairo",
            region = "Cairo Governorate",
            tz_id = "Africa/Cairo"
        )
    )

}