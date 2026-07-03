package com.example.workout.domain.network

interface APIFactory {
    fun getWorkoutApi(): WorkoutApi
}
