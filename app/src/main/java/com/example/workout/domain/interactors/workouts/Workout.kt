package com.example.workout.domain.interactors.workouts

import android.os.Parcelable
import com.example.workout.domain.models.workout.WorkoutDto
import com.example.workout.domain.models.workout.WorkoutType
import kotlinx.parcelize.Parcelize

@Parcelize
data class Workout(
    val id: Int,
    val title: String,
    val description: String?,
    val type: WorkoutType?,
    val duration: String,
): Parcelable {
    companion object {
        fun WorkoutDto.toData(): Workout {
            return Workout(
                id = id,
                title = title,
                description = description,
                type = type,
                duration = duration
            )
        }
    }
}
