package com.example.workout.presentation.ui.workoutdetails

import com.example.workout.domain.interactors.workouts.Workout
import com.example.workout.presentation.ui.BaseConfiguration

data class WorkoutDetailsState(
    val configuration: BaseConfiguration = BaseConfiguration.INITIAL,
    val workout: Workout? = null,
    val error: String? = null,
    val link: String = "",
    val duration: String = "",
    val isFullscreen: Boolean = false,
    val selectedSpeedIndex: Int = 2,
    val selectedQualityIndex: Int = 0,
)
