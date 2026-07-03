package com.example.workout.domain.repositories.workout

import com.example.workout.domain.common.BaseError
import com.example.workout.domain.common.Response
import com.example.workout.domain.models.workout.WorkoutDto
import com.example.workout.domain.models.workout.WorkoutDetailsDto
import com.example.workout.domain.network.WorkoutApi

class WorkoutRepositoryImpl(private val workoutApi: WorkoutApi): WorkoutRepository {
    override suspend fun getWorkouts(): Response<List<WorkoutDto>, BaseError> {
        return workoutApi.getWorkouts()
    }

    override suspend fun getWorkoutVideo(id: Int): Response<WorkoutDetailsDto, BaseError> {
        return workoutApi.getWorkoutVideo(id)
    }
}
