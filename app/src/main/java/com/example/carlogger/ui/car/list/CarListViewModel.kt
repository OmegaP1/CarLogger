package com.yourname.carlogger.ui.car.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourname.carlogger.data.model.Car
import com.yourname.carlogger.data.repository.CarRepository
import com.yourname.carlogger.data.repository.ImageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CarListViewModel(
    private val carRepository: CarRepository,
    private val imageRepository: ImageRepository
) : ViewModel() {

    private val _carsWithImages = MutableStateFlow<List<CarWithImage>>(emptyList())
    val carsWithImages: StateFlow<List<CarWithImage>> = _carsWithImages

    init {
        viewModelScope.launch {
            carRepository.allCars.collect { cars ->
                // Combine each car with its primary image
                val carWithImagesList = cars.map { car ->
                    val primaryImage = imageRepository.getPrimaryImageForCar(car.carId)
                    CarWithImage(car, primaryImage.value)
                }
                _carsWithImages.value = carWithImagesList
            }
        }
    }

    fun deleteCar(car: Car) {
        viewModelScope.launch {
            carRepository.deleteCar(car)
        }
    }
}