package com.example.carlogger.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.carlogger.data.model.Car
import com.example.carlogger.data.model.CarImage
import com.example.carlogger.data.model.Converters
import com.example.carlogger.data.model.MaintenanceRecord

// Define migration from version 1 to version 2
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Add the horsepower column to the cars table with a default value of 0
        database.execSQL("ALTER TABLE cars ADD COLUMN horsepower INTEGER NOT NULL DEFAULT 0")
    }
}

@Database(
    entities = [Car::class, MaintenanceRecord::class, CarImage::class],
    version = 2, // Updated from 1 to 2
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
                    .addMigrations(MIGRATION_1_2) // Add the migration
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}