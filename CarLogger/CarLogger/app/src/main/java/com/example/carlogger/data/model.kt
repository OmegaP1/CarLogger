package com.example.carlogger.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.util.Date

@Entity(tableName = "cars")
data class Car(
    @PrimaryKey(autoGenerate = true)
    val carId: Long = 0,
    val brand: String,
    val model: String,
    val year: Int,
    val licensePlate: String,
    val purchaseDate: Date? = null,
    val color: String = "",
    val notes: String = "",
    val lastUpdated: Date = Date()
)

@Entity(tableName = "maintenance_records")
data class MaintenanceRecord(
    @PrimaryKey(autoGenerate = true)
    val recordId: Long = 0,
    val carId: Long,
    val date: Date,
    val odometer: Int,
    val serviceType: String,
    val description: String,
    val cost: Double,
    val location: String = "",
    val notes: String = ""
)

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

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}