package com.example.carlogger.ui.car.detail

import android.content.Context
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import android.os.Bundle
import com.example.carlogger.CarLoggerApplication
import com.example.carlogger.data.repository.CarRepository
import com.example.carlogger.data.repository.ImageRepository
import com.example.carlogger.data.repository.MaintenanceRepository

/**
 * Factory for creating ViewModels that require SavedStateHandle in the Car Detail screen
 */
class SavedStateViewModelFactory(
    private val context: Context,
    owner: SavedStateRegistryOwner,
    defaultArgs: Bundle? = null
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {

    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        val application = context.applicationContext as CarLoggerApplication
        val database = application.database

        // Create repositories
        val carRepository = CarRepository(database.carDao())
        val maintenanceRepository = MaintenanceRepository(database.maintenanceDao())
        val imageRepository = ImageRepository(context, database.imageDao())

        return when {
            modelClass.isAssignableFrom(CarDetailViewModel::class.java) -> {
                CarDetailViewModel(carRepository, maintenanceRepository, imageRepository, handle) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}