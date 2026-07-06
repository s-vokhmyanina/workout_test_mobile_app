package com.example.workout.presentation.ui.workoutdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workout.BuildConfig
import com.example.workout.domain.common.Response
import com.example.workout.domain.interactors.workoutDetails.WorkoutDetailsInteractor
import com.example.workout.domain.interactors.workouts.Workout
import com.example.workout.presentation.ui.BaseConfiguration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WorkoutDetailsViewModel(
    private val workoutDetailsInteractor: WorkoutDetailsInteractor
) : ViewModel() {

    private val _state = MutableStateFlow(WorkoutDetailsState())
    val state: StateFlow<WorkoutDetailsState> = _state.asStateFlow()

    fun setWorkout(workout: Workout) {
        loadDetails(workout)
    }

    fun updateFullscreenState(isFullscreen: Boolean) {
        _state.value = _state.value.copy(
            isFullscreen = isFullscreen
        )
    }

    fun setSpeedIndex(index: Int) {
        _state.value = _state.value.copy(
            selectedSpeedIndex = index
        )
    }

    fun setQualityIndex(index: Int) {
        _state.value = _state.value.copy(
            selectedQualityIndex = index
        )
    }

    fun retry() {
        _state.value.workout?.let { loadDetails(it) }
    }

    private fun loadDetails(workout: Workout) {
        _state.value = _state.value.copy(
            configuration = BaseConfiguration.LOADING,
            error = null
        )

        viewModelScope.launch(Dispatchers.IO) {
            when (val response = workoutDetailsInteractor.getWorkoutDetails(workout.id)) {
                is Response.Success -> {
                    _state.value = _state.value.copy(
                        configuration = BaseConfiguration.READY,
                        workout = workout,
                        link = BuildConfig.API_URL + response.value.link,
                        duration = response.value.duration
                    )
                }

                is Response.Failure -> {
                    _state.value = _state.value.copy(
                        configuration = BaseConfiguration.ERROR,
                        error = response.error.message
                    )
                }
            }
        }
    }
}
