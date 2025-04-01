package com.example.carlogger.ui.car.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.carlogger.R
import com.example.carlogger.data.model.CarImage
import com.example.carlogger.databinding.ItemCarImagePagerBinding
import java.io.File

class CarImagePagerAdapter(
    private val images: List<CarImage>
) : RecyclerView.Adapter<CarImagePagerAdapter.ImageViewHolder>() {

    class ImageViewHolder(val binding: ItemCarImagePagerBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemCarImagePagerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val image = images[position]
        holder.binding.imgCar.load(File(image.imagePath)) {
            crossfade(true)
            placeholder(R.drawable.placeholder_car)
            error(R.drawable.placeholder_car)
        }
    }

    override fun getItemCount(): Int = images.size
}