package com.example.carlogger.ui.car.detail

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carlogger.data.model.Car
import com.example.carlogger.data.model.CarImage
import com.example.carlogger.data.model.MaintenanceRecord
import com.example.carlogger.data.repository.CarRepository
import com.example.carlogger.data.repository.ImageRepository
import com.example.carlogger.data.repository.MaintenanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CarDetailViewModel(
    private val carRepository: CarRepository,
    private val maintenanceRepository: MaintenanceRepository,
    private val imageRepository: ImageRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val carId: Long = savedStateHandle.get<Long>("carId") ?: 0L

    private val _car = MutableStateFlow<Car?>(null)
    val car: StateFlow<Car?> = _car.asStateFlow()

    private val _maintenanceRecords = MutableStateFlow<List<MaintenanceRecord>>(emptyList())
    val maintenanceRecords: StateFlow<List<MaintenanceRecord>> = _maintenanceRecords.asStateFlow()

    private val _carImages = MutableStateFlow<List<CarImage>>(emptyList())
    val carImages: StateFlow<List<CarImage>> = _carImages.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadCarData()
    }

    private fun loadCarData() {
        _isLoading.value = true
        loadCar()
        loadMaintenanceRecords()
        loadCarImages()
    }

    private fun loadCar() {
        viewModelScope.launch {
            carRepository.getCarById(carId).collect {
                _car.value = it
                _isLoading.value = false
            }
        }
    }

    private fun loadMaintenanceRecords() {
        viewModelScope.launch {
            maintenanceRepository.getRecordsForCar(carId).collect {
                _maintenanceRecords.value = it
            }
        }
    }

    private fun loadCarImages() {
        viewModelScope.launch {
            imageRepository.getImagesForCar(carId).collect {
                _carImages.value = it
            }
        }
    }

    fun addImage(uri: Uri, description: String = "", isPrimary: Boolean = false) {
        _isLoading.value = true
        viewModelScope.launch {
            imageRepository.saveImage(uri, carId, description, isPrimary)
        }
    }

    fun deleteImage(carImage: CarImage) {
        _isLoading.value = true
        viewModelScope.launch {
            imageRepository.deleteImage(carImage)
        }
    }

    fun setImageAsPrimary(carImage: CarImage) {
        _isLoading.value = true
        viewModelScope.launch {
            imageRepository.setAsPrimary(carImage)
        }
    }

    fun addMaintenanceRecord(record: MaintenanceRecord) {
        _isLoading.value = true
        viewModelScope.launch {
            maintenanceRepository.insertRecord(record)
            _isLoading.value = false
        }
    }

    fun deleteMaintenanceRecord(record: MaintenanceRecord) {
        _isLoading.value = true
        viewModelScope.launch {
            maintenanceRepository.deleteRecord(record)
            _isLoading.value = false
        }
    }

    fun deleteCar(car: Car) {
        _isLoading.value = true
        viewModelScope.launch {
            carRepository.deleteCar(car)
        }
    }

    fun getImageUri(imagePath: String): Uri {
        return imageRepository.getImageUri(imagePath)
    }
}