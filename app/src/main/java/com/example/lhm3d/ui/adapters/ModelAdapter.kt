package com.example.lhm3d.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.lhm3d.R
import com.example.lhm3d.data.model.Model

/**
 * Adapter for displaying 3D models in a RecyclerView.
 */
class ModelAdapter(
    private val onModelClick: (Model) -> Unit
) : ListAdapter<Model, ModelAdapter.ModelViewHolder>(ModelDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModelViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_model, parent, false)
        return ModelViewHolder(view, onModelClick)
    }

    override fun onBindViewHolder(holder: ModelViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ModelViewHolder(
        itemView: View,
        private val onModelClick: (Model) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        
        private val modelImage: ImageView = itemView.findViewById(R.id.image_model_thumbnail)
        private val modelName: TextView = itemView.findViewById(R.id.text_model_title)
        private val modelStatus: TextView = itemView.findViewById(R.id.text_model_status)
        private val modelDate: TextView = itemView.findViewById(R.id.text_model_date)
        
        fun bind(model: Model) {
            // Set model name
            modelName.text = model.name
            
            // Set model status
            if (model.isProcessing) {
                modelStatus.visibility = View.VISIBLE
                modelStatus.text = "Processing"
            } else {
                modelStatus.visibility = View.GONE
            }
            
            // Set date
            val dateFormat = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
            modelDate.text = dateFormat.format(model.createdAt)
            
            // Load image with Glide
            val imageUrl = if (model.thumbnailUrl.isNotEmpty()) {
                model.thumbnailUrl
            } else {
                model.imageUrl
            }
            
            Glide.with(itemView.context)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_model)
                .error(R.drawable.placeholder_model)
                .centerCrop()
                .into(modelImage)
            
            // Set click listener
            itemView.setOnClickListener {
                onModelClick(model)
            }
        }
    }

    class ModelDiffCallback : DiffUtil.ItemCallback<Model>() {
        override fun areItemsTheSame(oldItem: Model, newItem: Model): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Model, newItem: Model): Boolean {
            return oldItem == newItem
        }
    }
}