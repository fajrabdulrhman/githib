package com.example.weatherapp.di

import android.content.Context
import androidx.room.Room
import com.example.weatherapp.db.WeatherApi
import com.example.weatherapp.db.WeatherDatabase
import com.example.weatherapp.fakes.FakeRepo
import com.example.weatherapp.repository.WeatherRepo
import com.example.weatherapp.repository.WeatherRepository
import com.example.weatherapp.ui.WeatherViewModel
import com.example.weatherapp.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    

    @Singleton
    @Provides
    fun provideWeatherDataBase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(context,WeatherDatabase::class.java,"weather_db").build()


    @Singleton
    @Provides
    fun provideWeatherDao(
        database: WeatherDatabase
    ) =database.getWeatherDao()



    @Provides
    @Singleton
    fun provideWeatherRepository(
        api: WeatherApi,
        dao: WeatherDatabase,
    ): WeatherRepo {
        return WeatherRepository(dao, api)
    }


    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

    }

    @Provides
    @Singleton
    fun provideWeatherApi(retrofit: Retrofit): WeatherApi
    {

           return retrofit.create(WeatherApi::class.java)
    }
}