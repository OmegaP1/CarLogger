package com.example.carlogger.ui.car.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.carlogger.R
import com.example.carlogger.data.model.CarImage
import com.example.carlogger.databinding.ItemCarImageBinding
import java.io.File

class CarImagesAdapter(
    private val listener: CarImageListener
) : ListAdapter<CarImage, CarImagesAdapter.CarImageViewHolder>(CarImageDiffCallback()) {

    interface CarImageListener {
        fun onImageClicked(carImage: CarImage, position: Int)
        fun onImageOptionsClicked(carImage: CarImage)
        fun onSetPrimaryClicked(carImage: CarImage)
        fun onDeleteImageClicked(carImage: CarImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarImageViewHolder {
        val binding = ItemCarImageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CarImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CarImageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CarImageViewHolder(
        private val binding: ItemCarImageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.imgCarImage.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onImageClicked(getItem(position), position)
                }
            }

            binding.overlay.setOnLongClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    toggleOptionsVisibility(true)
                    listener.onImageOptionsClicked(getItem(position))
                }
                true
            }

            binding.btnSetPrimary.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    toggleOptionsVisibility(false)
                    listener.onSetPrimaryClicked(getItem(position))
                }
            }

            binding.btnDeleteImage.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    toggleOptionsVisibility(false)
                    listener.onDeleteImageClicked(getItem(position))
                }
            }
        }

        fun bind(carImage: CarImage) {
            // Load image from file path
            binding.imgCarImage.load(File(carImage.imagePath)) {
                crossfade(true)
                placeholder(R.drawable.placeholder_car)
                error(R.drawable.placeholder_car)
            }

            // Show primary badge if this is the primary image
            binding.imgPrimaryBadge.visibility = if (carImage.isPrimary) View.VISIBLE else View.GONE

            // Reset options visibility
            toggleOptionsVisibility(false)
        }

        private fun toggleOptionsVisibility(show: Boolean) {
            binding.overlay.alpha = if (show) 0.7f else 0f
            binding.layoutOptions.visibility = if (show) View.VISIBLE else View.GONE
        }
    }

    class CarImageDiffCallback : DiffUtil.ItemCallback<CarImage>() {
        override fun areItemsTheSame(oldItem: CarImage, newItem: CarImage): Boolean {
            return oldItem.imageId == newItem.imageId
        }

        override fun areContentsTheSame(oldItem: CarImage, newItem: CarImage): Boolean {
            return oldItem == newItem
        }
    }
}