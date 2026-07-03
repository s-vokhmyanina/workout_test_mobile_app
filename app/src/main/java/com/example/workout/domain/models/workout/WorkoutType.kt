package com.example.workout.domain.models.workout

import com.example.workout.domain.deserializers.WorkoutTypeDeserializer
import com.google.gson.annotations.JsonAdapter

@JsonAdapter(WorkoutTypeDeserializer::class)
enum class WorkoutType(val value: Int, val text: String) {
    TRAINING(1, "Тренировка"),

    BROADCAST(2, "Эфир"),

    COMPLEX(3, "Комплекс"),
}
