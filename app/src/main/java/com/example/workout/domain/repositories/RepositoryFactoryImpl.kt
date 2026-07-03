package com.example.workout.domain.repositories

import com.example.workout.domain.network.APIFactory
import com.example.workout.domain.repositories.workout.WorkoutRepository
import com.example.workout.domain.repositories.workout.WorkoutRepositoryImpl

class RepositoryFactoryImpl(private val apiFactory: APIFactory) : RepositoryFactory {
    private val _userProfileRepository: WorkoutRepository by lazy {
        WorkoutRepositoryImpl(
            workoutApi = apiFactory.getWorkoutApi()
        )

//        WorkoutRepositoryMock()
    }

    override fun getWorkoutRepository(): WorkoutRepository = _userProfileRepository
}
