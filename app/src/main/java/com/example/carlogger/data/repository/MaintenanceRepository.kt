package com.example.carlogger.data.repository

import com.example.carlogger.data.local.MaintenanceDao
import com.example.carlogger.data.model.MaintenanceRecord
import kotlinx.coroutines.flow.Flow

/**
 * Repository for handling maintenance record data operations
 */
class MaintenanceRepository(private val maintenanceDao: MaintenanceDao) {

    // Get all maintenance records for a specific car
    fun getRecordsForCar(carId: Long): Flow<List<MaintenanceRecord>> {
        return maintenanceDao.getRecordsForCar(carId)
    }

    // Insert a new maintenance record and return its ID
    suspend fun insertRecord(record: MaintenanceRecord): Long {
        return maintenanceDao.insertRecord(record)
    }

    // Update an existing maintenance record
    suspend fun updateRecord(record: MaintenanceRecord) {
        maintenanceDao.updateRecord(record)
    }

    // Delete a maintenance record
    suspend fun deleteRecord(record: MaintenanceRecord) {
        maintenanceDao.deleteRecord(record)
    }
}