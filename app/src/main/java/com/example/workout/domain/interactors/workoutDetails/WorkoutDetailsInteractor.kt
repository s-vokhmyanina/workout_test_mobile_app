package com.example.workout.domain.interactors.workoutDetails

import com.example.workout.domain.common.BaseError
import com.example.workout.domain.common.Response

interface WorkoutDetailsInteractor {
    suspend fun getWorkoutDetails(id: Int): Response<WorkoutDetails, BaseError>
}
