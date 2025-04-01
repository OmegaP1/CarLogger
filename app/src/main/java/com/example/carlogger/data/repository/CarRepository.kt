package com.example.carlogger.data.repository

import com.example.carlogger.data.local.CarDao
import com.example.carlogger.data.model.Car
import kotlinx.coroutines.flow.Flow

/**
 * Repository for handling car-related data operations
 */
class CarRepository(private val carDao: CarDao) {

    // Get all cars as a Flow, automatically updated when data changes
    val allCars: Flow<List<Car>> = carDao.getAllCars()

    // Get a specific car by ID
    fun getCarById(carId: Long): Flow<Car> {
        return carDao.getCarById(carId)
    }

    // Insert a new car and return its ID
    suspend fun insertCar(car: Car): Long {
        return carDao.insertCar(car)
    }

    // Update an existing car
    suspend fun updateCar(car: Car) {
        carDao.updateCar(car)
    }

    // Delete a car
    suspend fun deleteCar(car: Car) {
        carDao.deleteCar(car)
    }
}