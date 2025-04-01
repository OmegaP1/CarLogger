package com.example.carlogger.ui.maintenance

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carlogger.data.model.MaintenanceRecord
import com.example.carlogger.data.repository.MaintenanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date

class AddEditMaintenanceRecordViewModel(
    private val maintenanceRepository: MaintenanceRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val carId: Long = savedStateHandle.get<Long>("carId") ?: 0L
    private val recordId: Long = savedStateHandle.get<Long>("recordId") ?: 0L
    private val isEditMode = recordId != 0L

    private val _record = MutableStateFlow<MaintenanceRecord?>(null)
    val record = _record.asStateFlow()

    // Form fields
    val serviceType = MutableStateFlow("")
    val serviceDate = MutableStateFlow<Date>(Date())
    val odometer = MutableStateFlow(0)
    val cost = MutableStateFlow(0.0)
    val location = MutableStateFlow("")
    val description = MutableStateFlow("")

    private val _saveComplete = MutableStateFlow(false)
    val saveComplete: StateFlow<Boolean> = _saveComplete

    init {
        if (isEditMode) {
            // In a real implementation, we would load the record from the repository
            // For now, we'll create an empty record since the repository method isn't available
            loadRecord()
        }
    }

    private fun loadRecord() {
        // This would require a repository method to get a single record by ID
        // Since we don't have that in the current implementation, this is a placeholder
        // In a real implementation, you would do something like:
        // viewModelScope.launch {
        //     maintenanceRepository.getRecordById(recordId).collect { loadedRecord ->
        //         _record.value = loadedRecord
        //         // Populate form fields
        //         serviceType.value = loadedRecord.serviceType
        //         serviceDate.value = loadedRecord.date
        //         odometer.value = loadedRecord.odometer
        //         cost.value = loadedRecord.cost
        //         location.value = loadedRecord.location
        //         description.value = loadedRecord.description
        //     }
        // }
    }

    fun saveRecord(carId: Long) {
        viewModelScope.launch {
            val recordToSave = if (isEditMode) {
                // Update existing record
                record.value?.copy(
                    serviceType = serviceType.value,
                    date = serviceDate.value,
                    odometer = odometer.value,
                    cost = cost.value,
                    location = location.value,
                    description = description.value
                )
            } else {
                // Create new record
                MaintenanceRecord(
                    carId = carId,
                    serviceType = serviceType.value,
                    date = serviceDate.value,
                    odometer = odometer.value,
                    cost = cost.value,
                    location = location.value,
                    description = description.value
                )
            }

            recordToSave?.let {
                if (isEditMode) {
                    maintenanceRepository.updateRecord(it)
                } else {
                    maintenanceRepository.insertRecord(it)
                }
                _saveComplete.value = true
            }
        }
    }

    fun resetSaveComplete() {
        _saveComplete.value = false
    }
}