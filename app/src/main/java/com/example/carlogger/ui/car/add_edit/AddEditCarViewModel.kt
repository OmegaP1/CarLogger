package com.example.carlogger.ui.car.add_edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carlogger.data.model.Car
import com.example.carlogger.data.repository.CarRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date

class AddEditCarViewModel(
    private val carRepository: CarRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val carId: Long = savedStateHandle.get<Long>("carId") ?: 0L
    private val isEditMode = carId != 0L

    private val _car = MutableStateFlow<Car?>(null)
    val car = _car.asStateFlow()

    // Form fields
    val brand = MutableStateFlow("")
    val model = MutableStateFlow("")
    val year = MutableStateFlow(0)
    val licensePlate = MutableStateFlow("")
    val purchaseDate = MutableStateFlow<Date?>(null)
    val color = MutableStateFlow("")
    val notes = MutableStateFlow("")

    private val _saveComplete = MutableStateFlow(false)
    val saveComplete = _saveComplete.asStateFlow()

    init {
        if (isEditMode) {
            loadCar()
        }
    }

    private fun loadCar() {
        viewModelScope.launch {
            carRepository.getCarById(carId).collect { loadedCar ->
                _car.value = loadedCar

                // Populate form fields
                brand.value = loadedCar.brand
                model.value = loadedCar.model
                year.value = loadedCar.year
                licensePlate.value = loadedCar.licensePlate
                purchaseDate.value = loadedCar.purchaseDate
                color.value = loadedCar.color
                notes.value = loadedCar.notes
            }
        }
    }

    fun saveCar() {
        viewModelScope.launch {
            val carToSave = if (isEditMode) {
                // Update existing car
                car.value?.copy(
                    brand = brand.value,
                    model = model.value,
                    year = year.value,
                    licensePlate = licensePlate.value,
                    purchaseDate = purchaseDate.value,
                    color = color.value,
                    notes = notes.value,
                    lastUpdated = Date()
                )
            } else {
                // Create new car
                Car(
                    brand = brand.value,
                    model = model.value,
                    year = year.value,
                    licensePlate = licensePlate.value,
                    purchaseDate = purchaseDate.value,
                    color = color.value,
                    notes = notes.value
                )
            }

            carToSave?.let {
                if (isEditMode) {
                    carRepository.updateCar(it)
                } else {
                    carRepository.insertCar(it)
                }
                _saveComplete.value = true
            }
        }
    }

    fun resetSaveComplete() {
        _saveComplete.value = false
    }
}