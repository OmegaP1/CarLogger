package com.example.carlogger.ui.car.add_edit

import android.content.Context
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import android.os.Bundle
import com.example.carlogger.CarLoggerApplication
import com.example.carlogger.data.repository.CarRepository


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

        return when {
            modelClass.isAssignableFrom(AddEditCarViewModel::class.java) -> {
                AddEditCarViewModel(carRepository, handle) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}