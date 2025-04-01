package com.example.carlogger.ui.car.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.carlogger.R
import com.example.carlogger.databinding.ItemCarBinding
import java.io.File

class CarAdapter(
    private val listener: CarItemListener
) : ListAdapter<CarWithImage, CarAdapter.CarViewHolder>(CarDiffCallback()) {

    interface CarItemListener {
        fun onCarClicked(carWithImage: CarWithImage)
        fun onCarMenuClicked(carWithImage: CarWithImage, itemView: View)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarViewHolder {
        val binding = ItemCarBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CarViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CarViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CarViewHolder(
        private val binding: ItemCarBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onCarClicked(getItem(position))
                }
            }

            binding.btnCarMenu.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onCarMenuClicked(getItem(position), it)
                }
            }
        }

        fun bind(carWithImage: CarWithImage) {
            val car = carWithImage.car
            binding.tvCarName.text = "${car.brand} ${car.model}"
            binding.tvCarYear.text = car.year.toString()
            binding.tvCarLicense.text = car.licensePlate

            // Load image if available
            val primaryImage = carWithImage.primaryImage
            if (primaryImage != null) {
                binding.imgCar.load(File(primaryImage.imagePath)) {
                    crossfade(true)
                    placeholder(R.drawable.placeholder_car)
                    error(R.drawable.placeholder_car)
                }
            } else {
                binding.imgCar.load(R.drawable.placeholder_car) {
                    crossfade(true)
                }
            }
        }
    }

    class CarDiffCallback : DiffUtil.ItemCallback<CarWithImage>() {
        override fun areItemsTheSame(oldItem: CarWithImage, newItem: CarWithImage): Boolean {
            return oldItem.car.carId == newItem.car.carId
        }

        override fun areContentsTheSame(oldItem: CarWithImage, newItem: CarWithImage): Boolean {
            return oldItem == newItem
        }
    }
}