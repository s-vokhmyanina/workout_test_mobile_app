package com.example.workout.presentation.ui.workoutlist.filters

import com.example.workout.domain.models.workout.WorkoutType

enum class FilterType(val text: String) {
    ALL("Все"),
    TRAINING("Тренировка"),
    BROADCAST("Эфир"),
    COMPLEX("Комплекс"),
}

fun FilterType.toWorkoutType(): WorkoutType? {
    return when (this) {
        FilterType.ALL -> null
        FilterType.TRAINING -> WorkoutType.TRAINING
        FilterType.BROADCAST -> WorkoutType.BROADCAST
        FilterType.COMPLEX -> WorkoutType.COMPLEX
    }
}

fun WorkoutType?.toFilterType(): FilterType {
    return when (this) {
        WorkoutType.TRAINING -> FilterType.TRAINING
        WorkoutType.BROADCAST -> FilterType.BROADCAST
        WorkoutType.COMPLEX -> FilterType.COMPLEX
        else -> FilterType.ALL
    }
}
