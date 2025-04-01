package com.example.carlogger.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Entity representing a car in the database
 */
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