package com.example.workout.presentation.ui.workoutlist

import com.example.workout.domain.interactors.workouts.Workout
import com.example.workout.domain.models.workout.WorkoutType
import com.example.workout.presentation.ui.BaseConfiguration

data class WorkoutListState(
    val configuration: BaseConfiguration = BaseConfiguration.INITIAL,
    val workouts: List<Workout> = emptyList(),
    val error: String? = null,
    val filterType: WorkoutType? = null,
    val querySearch: String = ""
)
