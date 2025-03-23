package com.example.lhm3d.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.lhm3d.R
import com.example.lhm3d.databinding.ItemModelBinding
import com.example.lhm3d.model.Model3D
import com.example.lhm3d.model.getFormattedDate

/**
 * Adapter for displaying 3D models in a RecyclerView.
 */
class ModelAdapter(private val onModelClick: (Model3D) -> Unit) :
    ListAdapter<Model3D, ModelAdapter.ModelViewHolder>(ModelDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModelViewHolder {
        val binding = ItemModelBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ModelViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ModelViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ModelViewHolder(private val binding: ItemModelBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onModelClick(getItem(position))
                }
            }
        }

        fun bind(model: Model3D) {
            binding.textViewModelName.text = model.name
            binding.textViewDate.text = model.getFormattedDate()

            // Load thumbnail
            if (model.thumbnailUrl.isNotEmpty()) {
                Glide.with(binding.imageViewModelThumbnail.context)
                    .load(model.thumbnailUrl)
                    .placeholder(R.drawable.ic_menu_gallery)
                    .into(binding.imageViewModelThumbnail)
            } else {
                // Use a placeholder if there's no thumbnail
                binding.imageViewModelThumbnail.setImageResource(R.drawable.ic_menu_gallery)
            }
        }
    }

    private class ModelDiffCallback : DiffUtil.ItemCallback<Model3D>() {
        override fun areItemsTheSame(oldItem: Model3D, newItem: Model3D): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Model3D, newItem: Model3D): Boolean {
            return oldItem == newItem
        }
    }
}
