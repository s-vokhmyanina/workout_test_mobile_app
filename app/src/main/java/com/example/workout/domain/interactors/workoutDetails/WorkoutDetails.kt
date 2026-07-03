package com.example.workout.domain.interactors.workoutDetails

import com.example.workout.domain.models.workout.WorkoutDetailsDto

data class WorkoutDetails(
    val id: Int,
    val duration: String,
    val link: String
) {
    companion object {
        fun WorkoutDetailsDto.toData(): WorkoutDetails {
            return WorkoutDetails(
                id = id,
                duration = duration,
                link = link
            )
        }
    }
}
