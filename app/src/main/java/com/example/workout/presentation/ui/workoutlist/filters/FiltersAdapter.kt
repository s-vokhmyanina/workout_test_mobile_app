package com.example.workout.presentation.ui.workoutlist.filters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.workout.R
import com.example.workout.databinding.FilterItemBinding

class FiltersAdapter(private val onItemClicked: (FilterType) -> Unit) :
    ListAdapter<FilterItem, FiltersViewHolder>(callback) {

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): FiltersViewHolder {

        val binding =
            FilterItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FiltersViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: FiltersViewHolder, position: Int
    ) {
        val item = getItem(position)
        val color = if (item.isSelected) R.color.accent_light else R.color.background
        holder.binding.filterTitle.text = item.type.text
        holder.binding.filterContainer.setBackgroundResource(color)
        holder.binding.filterContainer.setOnClickListener {
            onItemClicked(item.type)
        }
    }
}

class FiltersViewHolder(val binding: FilterItemBinding) :
    RecyclerView.ViewHolder(binding.root)

val callback = object : DiffUtil.ItemCallback<FilterItem>() {
    override fun areItemsTheSame(oldItem: FilterItem, newItem: FilterItem): Boolean {
        return oldItem.type == newItem.type
    }

    override fun areContentsTheSame(oldItem: FilterItem, newItem: FilterItem): Boolean {
        return oldItem == newItem
    }
}
