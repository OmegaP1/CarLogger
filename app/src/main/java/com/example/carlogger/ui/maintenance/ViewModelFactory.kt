package com.example.carlogger.ui.maintenance

import android.content.Context
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import android.os.Bundle
import com.example.carlogger.CarLoggerApplication
import com.example.carlogger.data.repository.MaintenanceRepository


class ViewModelFactory(
    private val context: Context,
    owner: SavedStateRegistryOwner,
    defaultArgs: Bundle? = null
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {

    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (modelClass.isAssignableFrom(AddEditMaintenanceRecordViewModel::class.java)) {
            val application = context.applicationContext as CarLoggerApplication
            val database = application.database
            val maintenanceRepository = MaintenanceRepository(database.maintenanceDao())
            return AddEditMaintenanceRecordViewModel(maintenanceRepository, handle) as T // Corrected line
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}