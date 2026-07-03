package com.example.workout.presentation.ui.workoutlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.workout.databinding.WorkoutItemBinding
import com.example.workout.domain.interactors.workouts.Workout
import com.example.workout.utils.addMinutes

class WorkoutListAdapter(private val onItemClicked: (Workout) -> Unit) :
    ListAdapter<Workout, WorkoutListViewHolder>(callback) {

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): WorkoutListViewHolder {

        val binding =
            WorkoutItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WorkoutListViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: WorkoutListViewHolder, position: Int
    ) {
        val item = getItem(position)
        holder.binding.workoutTitle.text = item.title
        holder.binding.workoutType.text = item.type?.text
        holder.binding.workoutDuration.text = item.duration.addMinutes()
        holder.binding.workoutDescriptioin.text = item.description
        holder.binding.workoutContainer.setOnClickListener {
            onItemClicked(item)
        }
    }
}

class WorkoutListViewHolder(val binding: WorkoutItemBinding) :
    RecyclerView.ViewHolder(binding.root)

val callback = object : DiffUtil.ItemCallback<Workout>() {
    override fun areItemsTheSame(oldItem: Workout, newItem: Workout): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Workout, newItem: Workout): Boolean {
        return oldItem == newItem
    }
}
