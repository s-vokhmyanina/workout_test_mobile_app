package com.example.workout.domain.models.workout

import com.google.gson.annotations.SerializedName

data class WorkoutDetailsDto (
    @SerializedName("id")
    val id: Int,
    @SerializedName("duration")
    val duration: String,
    @SerializedName("link")
    val link: String,
)
