package com.example.workout.presentation.ui.workoutlist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.workout.R
import com.example.workout.databinding.FragmentWorkoutListBinding
import com.example.workout.domain.interactors.workouts.Workout
import com.example.workout.presentation.ViewModels
import com.example.workout.presentation.ui.BaseConfiguration
import com.example.workout.presentation.ui.workoutlist.filters.FilterItem
import com.example.workout.presentation.ui.workoutlist.filters.FilterType
import com.example.workout.presentation.ui.workoutlist.filters.FiltersAdapter
import com.example.workout.presentation.ui.workoutlist.filters.toFilterType
import com.example.workout.presentation.ui.workoutlist.filters.toWorkoutType
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WorkoutList : Fragment() {
    private var _binding: FragmentWorkoutListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WorkoutListViewModel by viewModels {
        ViewModels.factory
    }
    private var adapter: WorkoutListAdapter? = null
    private var filtersAdapter: FiltersAdapter? = null
    private var searchJob: Job? = null
    private lateinit var filtersDialog: BottomSheetDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkoutListBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun navigateToWorkoutDetails(workout: Workout) {
        val action = WorkoutListDirections.actionWorkoutListToWorkoutDetails(workout)
        findNavController().navigate(action)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = WorkoutListAdapter(onItemClicked = { navigateToWorkoutDetails(workout = it) })
        filtersAdapter =
            FiltersAdapter(
                onItemClicked = {
                    viewModel.setFilterType(it.toWorkoutType())
                    filtersDialog.dismiss()
                }
            )
        binding.rvWorkoutList.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect() {
                updateView(it)
            }
        }
        setupUI()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupUI() {
        setupSearchView()
        initFiltersBottomSheet()
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refresh()
        }

        binding.loadingErrorBlock.btnRetry.setOnClickListener {
            viewModel.retry()
        }

        binding.filterTitle.setOnClickListener {
            showFiltersBottomSheet()
        }

        binding.chipFilter.setOnClickListener {
            showFiltersBottomSheet()
        }

        binding.chipFilter.setOnCloseIconClickListener {
            viewModel.setFilterType(null)
        }
    }

    private fun updateView(state: WorkoutListState) {

        filtersAdapter?.submitList(FilterType.entries.map {
            FilterItem(
                it,
                viewModel.state.value.filterType.toFilterType() == it
            )
        })

        if (state.filterType != null) {
            binding.chipFilter.isVisible = true
            binding.chipFilter.text = state.filterType.text
            binding.filterTitle.isVisible = false
        } else {
            binding.chipFilter.isVisible = false
            binding.filterTitle.isVisible = true
        }

        when (state.configuration) {
            BaseConfiguration.INITIAL -> {}
            BaseConfiguration.LOADING -> {
                if (state.workouts.isNotEmpty()) {
                    showRefresh()
                } else {
                    showLoading()
                }
            }

            BaseConfiguration.READY -> {
                if (state.workouts.isNotEmpty()) {
                    showWorkoutsList(state.workouts)
                } else {
                    showEmpty()
                }
            }

            BaseConfiguration.ERROR -> {
                val message = state.error ?: getString(R.string.unknown_error)
                if (state.workouts.isNotEmpty()) {
                    showToast(message)
                } else {
                    showError(message)
                }
            }
        }
    }

    private fun showLoading() {
        hideAll()
        disableAll()
        binding.loadingErrorBlock.container.isVisible = true
        binding.loadingErrorBlock.loadingContainer.isVisible = true
    }

    private fun showRefresh() {
        hideAll()
        disableAll()
        binding.swipeRefresh.isRefreshing = true
    }

    private fun showError(message: String) {
        hideAll()
        binding.loadingErrorBlock.container.isVisible = true
        binding.loadingErrorBlock.errorContainer.isVisible = true
        binding.loadingErrorBlock.tvErrorMessage.text = message
    }

    private fun showWorkoutsList(workouts: List<Workout>) {
        hideAll()
        adapter?.submitList(workouts)
        binding.swipeRefresh.isVisible = true
        binding.searchView.isEnabled = true
        binding.filterInfo.isEnabled = true
    }

    private fun showEmpty() {
        hideAll()
        binding.searchView.isEnabled = true
        binding.filterInfo.isEnabled = true
        binding.loadingErrorBlock.container.isVisible = true
        binding.loadingErrorBlock.emptyContainer.isVisible = true
    }

    private fun hideAll() {
        binding.loadingErrorBlock.container.isVisible = false
        binding.loadingErrorBlock.loadingContainer.isVisible = false
        binding.loadingErrorBlock.errorContainer.isVisible = false
        binding.loadingErrorBlock.emptyContainer.isVisible = false
        binding.swipeRefresh.isVisible = false
        binding.swipeRefresh.isRefreshing = false
    }

    private fun disableAll() {
        binding.searchView.isEnabled = false
        binding.filterInfo.isEnabled = false
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun initFiltersBottomSheet() {
        context?.let {
            val dialogView = layoutInflater.inflate(R.layout.filters_bottom_sheet_layout, null)
            filtersDialog = BottomSheetDialog(it, R.style.BottomSheetDialogTheme)
            filtersDialog.setContentView(dialogView)
            val filtersRV = dialogView.findViewById<RecyclerView>(R.id.rv_filter_list)
            filtersRV.adapter = filtersAdapter
        }
    }

    private fun showFiltersBottomSheet() {
        filtersDialog.show()
    }

    private fun setupSearchView() {
        binding.searchView.apply {

            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.let { performSearch(it) }
                    clearFocus()
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    searchJob?.cancel()
                    newText?.let { text ->
                        searchJob = viewLifecycleOwner.lifecycleScope.launch {
                            delay(SEARCH_DEBOUNCE_DELAY)
                            performSearch(text)
                        }
                    }
                    return true
                }
            })

            setOnCloseListener {
                performSearch("")
                false
            }

            setOnQueryTextFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    binding.searchView.setQuery(binding.searchView.query, false)
                }
            }
        }
    }

    private fun performSearch(query: String) {
        val trimmedQuery = query.trim()
        viewModel.setSearchQuery(trimmedQuery)
    }

    companion object {
        fun newInstance() = WorkoutList()

        private const val SEARCH_DEBOUNCE_DELAY = 100L
    }
}
