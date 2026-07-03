package com.example.workout.domain.repositories

import com.example.workout.domain.repositories.workout.WorkoutRepository

interface RepositoryFactory {
    fun getWorkoutRepository(): WorkoutRepository
}
