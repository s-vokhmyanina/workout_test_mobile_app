package com.example.workout.services

import android.content.Context
import com.example.workout.domain.network.services.ReachabilityService
import com.example.workout.domain.network.services.ReachabilityServiceImpl

class ServicesFactoryImpl(
    private val context: Context,
) : ServicesFactory {
    private val _reachabilityService: ReachabilityService by lazy {
        ReachabilityServiceImpl(context)
    }

    override fun getReachabilityService(): ReachabilityService = _reachabilityService
}
