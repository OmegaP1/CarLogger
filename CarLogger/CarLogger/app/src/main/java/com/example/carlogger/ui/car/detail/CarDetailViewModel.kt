package com.yourname.carlogger.ui.car.detail

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourname.carlogger.data.model.Car
import com.yourname.carlogger.data.model.CarImage
import com.yourname.carlogger.data.model.MaintenanceRecord
import com.yourname.carlogger.data.repository.CarRepository
import com.yourname.carlogger.data.repository.ImageRepository
import com.yourname.carlogger.data.repository.MaintenanceRepository
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
    val car = _car.asStateFlow()

    private val _maintenanceRecords = MutableStateFlow<List<MaintenanceRecord>>(emptyList())
    val maintenanceRecords = _maintenanceRecords.asStateFlow()

    private val _carImages = MutableStateFlow<List<CarImage>>(emptyList())
    val carImages = _carImages.asStateFlow()

    init {
        loadCar()
        loadMaintenanceRecords()
        loadCarImages()
    }

    private fun loadCar() {
        viewModelScope.launch {
            carRepository.getCarById(carId).collect {
                _car.value = it
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
        viewModelScope.launch {
            imageRepository.saveImage(uri, carId, description, isPrimary)
        }
    }

    fun deleteImage(carImage: CarImage) {
        viewModelScope.launch {
            imageRepository.deleteImage(carImage)
        }
    }

    fun setImageAsPrimary(carImage: CarImage) {
        viewModelScope.launch {
            imageRepository.setAsPrimary(carImage)
        }
    }

    fun addMaintenanceRecord(record: MaintenanceRecord) {
        viewModelScope.launch {
            maintenanceRepository.insertRecord(record)
        }
    }

    fun deleteMaintenanceRecord(record: MaintenanceRecord) {
        viewModelScope.launch {
            maintenanceRepository.deleteRecord(record)
        }
    }

    fun getImageUri(imagePath: String): Uri {
        return imageRepository.getImageUri(imagePath)
    }
}