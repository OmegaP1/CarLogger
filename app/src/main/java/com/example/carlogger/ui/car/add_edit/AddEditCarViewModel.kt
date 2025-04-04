package com.example.carlogger.ui.car.add_edit

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carlogger.data.model.Car
import com.example.carlogger.data.repository.CarRepository
import com.example.carlogger.data.repository.ImageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date

class AddEditCarViewModel(
    private val carRepository: CarRepository,
    private val imageRepository: ImageRepository,
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
    val horsepower = MutableStateFlow(0)
    val notes = MutableStateFlow("")

    private val _saveComplete = MutableStateFlow(false)
    val saveComplete = _saveComplete.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        if (isEditMode) {
            loadCar()
        }
    }

    private fun loadCar() {
        _isLoading.value = true
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
                horsepower.value = loadedCar.horsepower
                notes.value = loadedCar.notes
                _isLoading.value = false
            }
        }
    }

    fun saveCar(imageUri: Uri? = null) {
        _isLoading.value = true
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
                    horsepower = horsepower.value,
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
                    horsepower = horsepower.value,
                    notes = notes.value
                )
            }

            carToSave?.let {
                val savedCarId = if (isEditMode) {
                    carRepository.updateCar(it)
                    carId
                } else {
                    carRepository.insertCar(it)
                }

                // Save image if provided
                imageUri?.let { uri ->
                    imageRepository.saveImage(
                        uri = uri,
                        carId = savedCarId,
                        isPrimary = true // Make this the primary image
                    )
                }

                _isLoading.value = false
                _saveComplete.value = true
            }
        }
    }

    fun resetSaveComplete() {
        _saveComplete.value = false
    }
}