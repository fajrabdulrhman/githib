package com.example.weatherapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.example.weatherapp.fakes.FakeWeatherResponse
import com.example.weatherapp.db.WeatherDao
import com.example.weatherapp.db.WeatherDatabase
import com.example.getOrAwaitValueTest
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.*
import javax.inject.Inject
import javax.inject.Named


@ExperimentalCoroutinesApi
@SmallTest
@HiltAndroidTest
class WeatherDaoTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    @Named("test_db")
    lateinit var database: WeatherDatabase
    private lateinit var dao: WeatherDao
    private val fakeWeatherResponse = FakeWeatherResponse.fakeWeatherResponse

    @Before
    fun setup() {
        hiltRule.inject()

        dao = database.getWeatherDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertWeatherItem_insertsSuccessfully() = runTest {
        dao.upsert(fakeWeatherResponse)

        val result = dao.getAllWeathers().getOrAwaitValueTest()
        assertTrue(result.contains(fakeWeatherResponse))
    }

    @Test
    fun deleteWeatherItem_deletesSuccessfully() = runTest {
        dao.upsert(fakeWeatherResponse)
        dao.deleteCountry(fakeWeatherResponse)

        val result = dao.getAllWeathers().getOrAwaitValueTest()
        assertFalse(result.contains(fakeWeatherResponse))
    }




}
