package com.example.carlogger.data.repository

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.carlogger.data.local.CarImageDao
import com.example.carlogger.data.model.CarImage
import kotlinx.coroutines.flow.Flow
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Repository for handling car image operations
 */
class ImageRepository(
    private val context: Context,
    private val imageDao: CarImageDao
) {

    // Get all images for a specific car
    fun getImagesForCar(carId: Long): Flow<List<CarImage>> {
        return imageDao.getImagesForCar(carId)
    }

    // Get the primary image for a car
    fun getPrimaryImageForCar(carId: Long): Flow<CarImage?> {
        return imageDao.getPrimaryImageForCar(carId)
    }

    /**
     * Save an image from a URI to the app's storage and create a database record
     *
     * @param uri The URI of the image to save
     * @param carId The ID of the car to associate with this image
     * @param description Optional description for the image
     * @param isPrimary Whether this image should be set as the primary image for the car
     * @return The saved CarImage object, or null if saving failed
     */
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

    /**
     * Delete an image from storage and database
     */
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

    /**
     * Set an image as the primary image for a car
     */
    suspend fun setAsPrimary(carImage: CarImage) {
        imageDao.clearPrimaryFlagForCar(carImage.carId)
        imageDao.updateImage(carImage.copy(isPrimary = true))
    }

    /**
     * Get a content URI for an image file path
     */
    fun getImageUri(imagePath: String): Uri {
        val file = File(imagePath)
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }
}