package com.yourname.carlogger.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.yourname.carlogger.data.model.Car
import com.yourname.carlogger.data.model.CarImage
import com.yourname.carlogger.data.model.Converters
import com.yourname.carlogger.data.model.MaintenanceRecord

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

// DAOs (Data Access Objects)
package com.yourname.carlogger.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.yourname.carlogger.data.model.Car
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