package com.example.carlogger.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Entity representing a maintenance record in the database
 */
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