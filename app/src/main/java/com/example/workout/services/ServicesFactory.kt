package com.example.workout.services

import com.example.workout.domain.network.services.ReachabilityService

interface ServicesFactory {
    fun getReachabilityService(): ReachabilityService
}
