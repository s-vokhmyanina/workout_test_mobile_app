package com.example.workout.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.workout.di.Interactors
import com.example.workout.presentation.ui.workoutdetails.WorkoutDetailsViewModel
import com.example.workout.presentation.ui.workoutlist.WorkoutListViewModel

class ViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WorkoutListViewModel::class.java)) {
            return WorkoutListViewModel(
                Interactors.factory.getWorkoutListInteractor()
            ) as T
        }

        if (modelClass.isAssignableFrom(WorkoutDetailsViewModel::class.java)) {
            return WorkoutDetailsViewModel(
                Interactors.factory.getWorkoutDetailsInteractor()
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

object ViewModels {
    lateinit var factory: ViewModelProvider.Factory
}
