package com.example.carlogger.data.local

import androidx.room.*
import com.example.carlogger.data.model.Car
import kotlinx.coroutines.flow.Flow

@Dao
interface CarDao {
    @Query("SELECT * FROM cars ORDER BY lastUpdated DESC")
    fun getAllCars(): Flow<List<Car>>

    @Query("SELECT * FROM cars WHERE carId = :carId")
    fun getCarById(carId: Long): Flow<Car>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCar(car: Car): Long

    @Update
    suspend fun updateCar(car: Car)

    @Delete
    suspend fun deleteCar(car: Car)
}