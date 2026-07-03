package com.example.workout.domain.interactors

import com.example.workout.domain.interactors.workoutDetails.WorkoutDetailsInteractor
import com.example.workout.domain.interactors.workoutDetails.WorkoutDetailsInteractorImpl
import com.example.workout.domain.interactors.workouts.WorkoutListInteractor
import com.example.workout.domain.interactors.workouts.WorkoutListInteractorImpl
import com.example.workout.domain.repositories.RepositoryFactory

class InteractorsFactoryImpl(
    private val repositoryFactory: RepositoryFactory
) : InteractorsFactory {

    override fun getWorkoutListInteractor(): WorkoutListInteractor {
        return WorkoutListInteractorImpl(repositoryFactory.getWorkoutRepository())
    }

    override fun getWorkoutDetailsInteractor(): WorkoutDetailsInteractor {
        return WorkoutDetailsInteractorImpl(repositoryFactory.getWorkoutRepository())
    }
}
