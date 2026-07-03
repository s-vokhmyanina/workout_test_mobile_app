package com.example.workout.domain.interactors.workoutDetails

import com.example.workout.domain.common.BaseError
import com.example.workout.domain.common.Response
import com.example.workout.domain.common.mapSuccess
import com.example.workout.domain.interactors.workoutDetails.WorkoutDetails.Companion.toData
import com.example.workout.domain.repositories.workout.WorkoutRepository

class WorkoutDetailsInteractorImpl(
    private val workoutRepository: WorkoutRepository
) : WorkoutDetailsInteractor {
    override suspend fun getWorkoutDetails(id: Int): Response<WorkoutDetails, BaseError> {
        return workoutRepository.getWorkoutVideo(id).mapSuccess {
            it.toData()
        }
    }
}
