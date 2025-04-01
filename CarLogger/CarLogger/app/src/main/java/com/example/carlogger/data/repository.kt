package com.yourname.carlogger.data.repository

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.yourname.carlogger.data.local.CarDao
import com.yourname.carlogger.data.local.CarImageDao
import com.yourname.carlogger.data.local.MaintenanceDao
import com.yourname.carlogger.data.model.Car
import com.yourname.carlogger.data.model.CarImage
import com.yourname.carlogger.data.model.MaintenanceRecord
import kotlinx.coroutines.flow.Flow
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CarRepository(private val carDao: CarDao) {

    val allCars: Flow<List<Car>> = carDao.getAllCars()

    fun getCarById(carId: Long): Flow<Car> {
        return carDao.getCarById(carId)
    }

    suspend fun insertCar(car: Car): Long {
        return carDao.insertCar(car)
    }

    suspend fun updateCar(car: Car) {
        carDao.updateCar(car)
    }

    suspend fun deleteCar(car: Car) {
        carDao.deleteCar(car)
    }
}

class MaintenanceRepository(private val maintenanceDao: MaintenanceDao) {

    fun getRecordsForCar(carId: Long): Flow<List<MaintenanceRecord>> {
        return maintenanceDao.getRecordsForCar(carId)
    }

    suspend fun insertRecord(record: MaintenanceRecord): Long {
        return maintenanceDao.insertRecord(record)
    }

    suspend fun updateRecord(record: MaintenanceRecord) {
        maintenanceDao.updateRecord(record)
    }

    suspend fun deleteRecord(record: MaintenanceRecord) {
        maintenanceDao.deleteRecord(record)
    }
}

class ImageRepository(
    private val context: Context,
    private val imageDao: CarImageDao
) {

    fun getImagesForCar(carId: Long): Flow<List<CarImage>> {
        return imageDao.getImagesForCar(carId)
    }

    fun getPrimaryImageForCar(carId: Long): Flow<CarImage?> {
        return imageDao.getPrimaryImageForCar(carId)
    }

    suspend fun saveImage(uri: Uri, carId: Long, description: String = "", isPrimary: Boolean = false): CarImage? {
        // Create a file in the app's private storage
        val storageDir = context.getExternalFilesDir("car_images")
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "CAR_${carId}_${timeStamp}.jpg"
        val file = File(storageDir, fileName)

        return try {
            // Copy the content from Uri to the file
            context.contentResolver.openInputStream(uri)?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            // Create CarImage object
            val carImage = CarImage(
                carId = carId,
                imagePath = file.absolutePath,
                description = description,
                isPrimary = isPrimary
            )

            // If this is set as primary, clear other primary flags
            if (isPrimary) {
                imageDao.clearPrimaryFlagForCar(carId)
            }

            // Save to database and return
            val id = imageDao.insertImage(carImage)
            carImage.copy(imageId = id)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun deleteImage(carImage: CarImage) {
        // Delete the file
        try {
            File(carImage.imagePath).delete()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Delete the database entry
        imageDao.deleteImage(carImage)
    }

    suspend fun setAsPrimary(carImage: CarImage) {
        imageDao.clearPrimaryFlagForCar(carImage.carId)
        imageDao.updateImage(carImage.copy(isPrimary = true))
    }

    fun getImageUri(imagePath: String): Uri {
        val file = File(imagePath)
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }
}