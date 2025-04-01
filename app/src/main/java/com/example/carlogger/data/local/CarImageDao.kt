package com.example.carlogger.data.local

import androidx.room.*
import com.example.carlogger.data.model.CarImage
import kotlinx.coroutines.flow.Flow

@Dao
interface CarImageDao {
    @Query("SELECT * FROM car_images WHERE carId = :carId ORDER BY dateTaken DESC")
    fun getImagesForCar(carId: Long): Flow<List<CarImage>>

    @Query("SELECT * FROM car_images WHERE carId = :carId AND isPrimary = 1 LIMIT 1")
    fun getPrimaryImageForCar(carId: Long): Flow<CarImage?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(carImage: CarImage): Long

    @Update
    suspend fun updateImage(carImage: CarImage)

    @Delete
    suspend fun deleteImage(carImage: CarImage)

    @Query("UPDATE car_images SET isPrimary = 0 WHERE carId = :carId")
    suspend fun clearPrimaryFlagForCar(carId: Long)
}