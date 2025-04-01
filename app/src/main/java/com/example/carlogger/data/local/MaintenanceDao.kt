package com.example.carlogger.data.local

import androidx.room.*
import com.example.carlogger.data.model.MaintenanceRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface MaintenanceDao {
    @Query("SELECT * FROM maintenance_records WHERE carId = :carId ORDER BY date DESC")
    fun getRecordsForCar(carId: Long): Flow<List<MaintenanceRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: MaintenanceRecord): Long

    @Update
    suspend fun updateRecord(record: MaintenanceRecord)

    @Delete
    suspend fun deleteRecord(record: MaintenanceRecord)
}