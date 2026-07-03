package com.example.workout.domain.network

import com.example.workout.domain.common.BaseError
import com.example.workout.domain.models.workout.WorkoutDto
import com.example.workout.domain.models.workout.WorkoutDetailsDto
import com.example.workout.domain.common.Response

interface WorkoutApi {
    suspend fun getWorkouts(): Response<List<WorkoutDto>, BaseError.Api>

    suspend fun getWorkoutVideo(id: Int): Response<WorkoutDetailsDto, BaseError.Api>
}
