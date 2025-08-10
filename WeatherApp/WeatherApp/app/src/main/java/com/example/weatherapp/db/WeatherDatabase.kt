package com.example.weatherapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.weatherapp.di.AppModule
import com.example.weatherapp.models.WeatherResponse
import javax.inject.Inject


@Database(
    entities = [WeatherResponse::class],
    version = 1
)
@TypeConverters(Convertors::class)
 abstract class WeatherDatabase:RoomDatabase () {

    abstract fun getWeatherDao(): WeatherDao
}
 /*   companion object {
        @Volatile
        private var instance: WeatherDatabase? = null

        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {

            // changed here
            instance ?: AppModule.provideWeatherDataBase(context).also { instance = it }
        }


        //will be added from app Module
         private fun createDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                WeatherDatabase::class.java,
                "weather_db"
            ).build()


    }*/
