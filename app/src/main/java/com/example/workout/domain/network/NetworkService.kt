package com.example.workout.domain.network

import com.example.workout.domain.models.workout.WorkoutDto
import com.example.workout.domain.models.workout.WorkoutDetailsDto
import retrofit2.http.GET
import retrofit2.http.Query

interface NetworkService {
    @GET("/get_workouts")
    suspend fun getWorkouts(): List<WorkoutDto>

    @GET("/get_video")
    suspend fun getVideo(
        @Query("id") id: Int
    ): WorkoutDetailsDto
}
