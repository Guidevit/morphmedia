package com.example.lhm3d.ui.model

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
import com.example.lhm3d.model.Model3D
import com.example.lhm3d.model.ProcessingStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Adapter for displaying 3D models in a RecyclerView
 */
class ModelAdapter(private val onItemClick: (String) -> Unit) : 
    ListAdapter<Model3D, ModelAdapter.ModelViewHolder>(ModelDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModelViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_model, parent, false)
        return ModelViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: ModelViewHolder, position: Int) {
        val model = getItem(position)
        holder.bind(model, onItemClick)
    }
    
    class ModelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.image_model_thumbnail)
        private val titleTextView: TextView = itemView.findViewById(R.id.text_model_title)
        private val statusTextView: TextView = itemView.findViewById(R.id.text_model_status)
        private val dateTextView: TextView = itemView.findViewById(R.id.text_model_date)
        
        fun bind(model: Model3D, onItemClick: (String) -> Unit) {
            titleTextView.text = model.name
            
            // Set status text and color based on processing status
            val statusText = when (model.processingStatus) {
                ProcessingStatus.PENDING -> "Pending..."
                ProcessingStatus.PROCESSING -> "Processing..."
                ProcessingStatus.COMPLETED -> "Ready"
                ProcessingStatus.FAILED -> "Failed"
            }
            statusTextView.text = statusText
            
            // Format the date
            val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            dateTextView.text = dateFormat.format(Date(model.createdAt))
            
            // Load thumbnail image
            if (model.thumbnailUrl.isNotEmpty()) {
                Glide.with(itemView.context)
                    .load(model.thumbnailUrl)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(imageView)
            } else if (model.sourceImageUrl.isNotEmpty()) {
                // If no thumbnail, use the source image
                Glide.with(itemView.context)
                    .load(model.sourceImageUrl)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(imageView)
            } else {
                // No images available
                imageView.setImageResource(R.drawable.ic_launcher_foreground)
            }
            
            // Set click listener
            itemView.setOnClickListener {
                onItemClick(model.id)
            }
        }
    }
    
    class ModelDiffCallback : DiffUtil.ItemCallback<Model3D>() {
        override fun areItemsTheSame(oldItem: Model3D, newItem: Model3D): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: Model3D, newItem: Model3D): Boolean {
            return oldItem == newItem
        }
    }
}