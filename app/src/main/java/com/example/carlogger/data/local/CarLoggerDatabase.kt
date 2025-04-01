package com.example.carlogger.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.carlogger.data.model.Car
import com.example.carlogger.data.model.CarImage
import com.example.carlogger.data.model.Converters
import com.example.carlogger.data.model.MaintenanceRecord

@Database(
    entities = [Car::class, MaintenanceRecord::class, CarImage::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class CarLoggerDatabase : RoomDatabase() {

    abstract fun carDao(): CarDao
    abstract fun maintenanceDao(): MaintenanceDao
    abstract fun imageDao(): CarImageDao

    companion object {
        @Volatile
        private var INSTANCE: CarLoggerDatabase? = null

        fun getDatabase(context: Context): CarLoggerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CarLoggerDatabase::class.java,
                    "car_logger_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}