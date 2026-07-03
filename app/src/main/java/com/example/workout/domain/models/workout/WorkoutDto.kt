package com.example.workout.domain.models.workout

import com.google.gson.annotations.SerializedName

data class WorkoutDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String?,
    @SerializedName("type")
    val type: WorkoutType?,
    @SerializedName("duration")
    val duration: String,
)
