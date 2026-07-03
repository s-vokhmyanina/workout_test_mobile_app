package com.example.workout.domain.network

import com.example.workout.domain.common.BaseError
import com.example.workout.domain.common.Response
import com.example.workout.domain.models.workout.WorkoutDto
import com.example.workout.domain.models.workout.WorkoutDetailsDto
import com.example.workout.domain.network.services.ReachabilityService

class WorkoutApiImpl(
    private val service: NetworkService,
    reachabilityService: ReachabilityService
) : BaseApi(reachabilityService), WorkoutApi {
    override suspend fun getWorkouts(): Response<List<WorkoutDto>, BaseError.Api> {
        return request { service.getWorkouts() }
    }

    override suspend fun getWorkoutVideo(id: Int): Response<WorkoutDetailsDto, BaseError.Api> {
        return request { service.getVideo(id) }
    }
}
