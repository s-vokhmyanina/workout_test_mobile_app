package com.example.workout.domain.repositories.workout

import com.example.workout.domain.common.BaseError
import com.example.workout.domain.common.Response
import com.example.workout.domain.models.workout.WorkoutDto
import com.example.workout.domain.models.workout.WorkoutDetailsDto

interface WorkoutRepository {
    suspend fun getWorkouts(): Response<List<WorkoutDto>, BaseError>

    suspend fun getWorkoutVideo(id: Int): Response<WorkoutDetailsDto, BaseError>
}
