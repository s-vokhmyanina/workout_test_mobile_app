package com.example.workout.domain.interactors.workouts

import com.example.workout.domain.common.BaseError
import com.example.workout.domain.common.Response
import com.example.workout.domain.common.mapSuccess
import com.example.workout.domain.interactors.workouts.Workout.Companion.toData
import com.example.workout.domain.models.workout.WorkoutType
import com.example.workout.domain.repositories.workout.WorkoutRepository

class WorkoutListInteractorImpl(
    private val workoutRepository: WorkoutRepository
) : WorkoutListInteractor {
    override suspend fun getWorkouts(): Response<List<Workout>, BaseError> {
        return workoutRepository.getWorkouts().mapSuccess { list ->
            list.map {
                it.toData()
            }
        }
    }

    override fun filterAndSearchWorkout(
        workouts: List<Workout>,
        filter: WorkoutType?,
        searchQuery: String
    ): List<Workout> {
        val filteredWorkouts = if (filter != null) {
            workouts.filter { it.type == filter }
        } else workouts

        val resultWorkouts = if (searchQuery.isNotEmpty()) {
            filteredWorkouts.filter {
                it.title.lowercase().contains(searchQuery.lowercase().trim())
            }
        } else filteredWorkouts

        return resultWorkouts
    }
}
