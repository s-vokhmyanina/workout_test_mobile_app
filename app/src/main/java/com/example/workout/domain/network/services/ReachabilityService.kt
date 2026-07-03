package com.example.workout.domain.network.services

interface ReachabilityService {
    val currentState: ConnectionState
}

enum class ConnectionState {
    HAS_CONNECTION, NO_CONNECTION
}
