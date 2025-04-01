package com.example.carlogger.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Entity representing a car image in the database
 */
@Entity(tableName = "car_images")
data class CarImage(
    @PrimaryKey(autoGenerate = true)
    val imageId: Long = 0,
    val carId: Long,
    val imagePath: String,
    val description: String = "",
    val dateTaken: Date = Date(),
    val isPrimary: Boolean = false
)