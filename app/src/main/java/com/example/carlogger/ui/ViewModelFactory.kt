package com.example.carlogger.ui

import android.content.Context
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.savedstate.SavedStateRegistryOwner
import android.os.Bundle
import com.example.carlogger.CarLoggerApplication
import com.example.carlogger.data.repository.CarRepository
import com.example.carlogger.data.repository.ImageRepository
import com.example.carlogger.data.repository.MaintenanceRepository
import com.example.carlogger.ui.car.add_edit.AddEditCarViewModel
import com.example.carlogger.ui.car.detail.CarDetailViewModel
import com.example.carlogger.ui.car.list.CarListViewModel

/**
 * Factory for creating ViewModels with necessary dependencies
 */
class ViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val application = context.applicationContext as CarLoggerApplication
        val database = application.database

        // Create repositories
        val carRepository = CarRepository(database.carDao())
        val maintenanceRepository = MaintenanceRepository(database.maintenanceDao())
        val imageRepository = ImageRepository(context, database.imageDao())

        return when {
            modelClass.isAssignableFrom(CarListViewModel::class.java) -> {
                CarListViewModel(carRepository, imageRepository) as T
            }
            // For ViewModels that need SavedStateHandle, use this approach directly in Fragment
            // with by viewModels { SavedStateViewModelFactory(this, bundle) }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}

/**
 * Factory for creating ViewModels that require SavedStateHandle
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
            modelClass.isAssignableFrom(AddEditCarViewModel::class.java) -> {
                AddEditCarViewModel(carRepository, handle) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}