package com.example.workout.domain.network

import com.example.workout.services.ServicesFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class APIFactoryImpl(baseUrl: String, servicesFactory: ServicesFactory): APIFactory {

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val networkService: NetworkService by lazy {
        retrofit.create(NetworkService::class.java)
    }

    private val reachabilityService by lazy {
        servicesFactory.getReachabilityService()
    }

    private val _workoutApi by lazy {
        WorkoutApiImpl(
            networkService,
            reachabilityService = reachabilityService
        )
    }

    override fun getWorkoutApi(): WorkoutApi = _workoutApi

    companion object {
        private const val CONNECT_TIMEOUT = 30L
        private const val READ_TIMEOUT = 30L
        private const val WRITE_TIMEOUT = 30L
    }
}
