package com.example.workout.domain.interactors

import com.example.workout.domain.interactors.workoutDetails.WorkoutDetailsInteractor
import com.example.workout.domain.interactors.workouts.WorkoutListInteractor

interface InteractorsFactory {
    fun getWorkoutListInteractor(): WorkoutListInteractor

    fun getWorkoutDetailsInteractor(): WorkoutDetailsInteractor
}
