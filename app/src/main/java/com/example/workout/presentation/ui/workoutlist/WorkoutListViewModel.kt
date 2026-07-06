package com.example.workout.presentation.ui.workoutlist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workout.domain.common.Response
import com.example.workout.domain.interactors.workouts.Workout
import com.example.workout.domain.interactors.workouts.WorkoutListInteractor
import com.example.workout.domain.models.workout.WorkoutType
import com.example.workout.presentation.ui.BaseConfiguration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WorkoutListViewModel(
    private val workoutListInteractor: WorkoutListInteractor
) : ViewModel() {

    private val _state = MutableStateFlow(WorkoutListState())
    val state: StateFlow<WorkoutListState> = _state.asStateFlow()

    private val allWorkouts: MutableStateFlow<List<Workout>> = MutableStateFlow(emptyList())

    init {
        loadWorkouts()
    }

    fun refresh() {
        if (state.value.configuration != BaseConfiguration.LOADING) {
            loadWorkouts()
        }
    }

    fun retry() {
        loadWorkouts()
    }

    fun setSearchQuery(query: String) {
        _state.value = _state.value.copy(
            workouts = applyFiltersAndSearch(
                filter = _state.value.filterType,
                query = query,
            ),
            querySearch = query
        )
    }

//    fun clearSearchQuery() {
//        _state.value = _state.value.copy(
//            workouts = applyFiltersAndSearch(
//                filter = _state.value.filterType,
//                query = "",
//            ),
//            querySearch = ""
//        )
//    }

    fun setFilterType(type: WorkoutType?) {
        _state.value = _state.value.copy(
            workouts = applyFiltersAndSearch(
                filter = type,
                query = _state.value.querySearch,
            ),
            filterType = type
        )
    }

//    fun clearFilterType() {
//        _state.value = _state.value.copy(
//            workouts = applyFiltersAndSearch(
//                filter = null,
//                query = _state.value.querySearch,
//            ),
//            filterType = null
//        )
//    }

    private fun applyFiltersAndSearch(
        filter: WorkoutType?,
        query: String,
    ): List<Workout> {
        return workoutListInteractor.filterAndSearchWorkout(
            workouts = allWorkouts.value,
            filter = filter,
            searchQuery = query,
        )
    }


    private fun loadWorkouts() {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                configuration = BaseConfiguration.LOADING,
                error = null
            )

            when (val response = workoutListInteractor.getWorkouts()) {
                is Response.Success -> {
                    allWorkouts.value = response.value
                    _state.value = _state.value.copy(
                        configuration = BaseConfiguration.READY,
                        workouts = response.value
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
