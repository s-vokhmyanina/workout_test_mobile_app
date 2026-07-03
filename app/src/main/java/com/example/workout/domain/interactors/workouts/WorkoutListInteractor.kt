package com.example.workout.domain.interactors.workouts

import com.example.workout.domain.common.BaseError
import com.example.workout.domain.common.Response
import com.example.workout.domain.models.workout.WorkoutType

/**
 * Можно было бы создать UseCase отдельно под каждый пук
 * но так как я расширять это приложение вероятно не буду и переиспользования нет, то будет все в интеракторе под каждый экран
 */
interface WorkoutListInteractor {

    suspend fun getWorkouts(): Response<List<Workout>, BaseError>

    fun filterAndSearchWorkout(
        workouts: List<Workout>,
        filter: WorkoutType?,
        searchQuery: String
    ): List<Workout>
}
