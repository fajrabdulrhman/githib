package com.example.weatherapp.fakes

import com.example.weatherapp.models.SearchResponse
import com.example.weatherapp.models.SearchResponseItem


object FakeSearchResponse {

    val searchResponse: SearchResponse = SearchResponse().apply {
        add(
            SearchResponseItem(
                country = "Egypt",
                id = 1,
                lat = 30.0444,
                lon = 31.2357,
                name = "Cairo",
                region = "Cairo Governorate",
                url = "cairo/egypt"
            )
        )
    }

}