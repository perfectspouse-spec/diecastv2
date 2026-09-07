package com.example.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ImageStorageHelper {

    /**
     * Saves a Uri (from gallery or photo picker) to internal files storage.
     * Returns the absolute path of the saved file.
     */
    fun saveImageToInternalStorage(context: Context, uri: Uri): String? {
        return try {
            val contentResolver = context.contentResolver
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            if (inputStream == null) return null

            val directory = File(context.filesDir, "diecast_photos")
            if (!directory.exists()) {
                directory.mkdirs()
            }

            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss_SSS", Locale.getDefault()).format(Date())
            val randomId = java.util.UUID.randomUUID().toString().take(6)
            val file = File(directory, "DIECAST_${timestamp}_$randomId.jpg")

            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)

            inputStream.close()
            outputStream.close()

            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Saves a Bitmap directly to internal files storage.
     */
    fun saveBitmapToInternalStorage(context: Context, bitmap: Bitmap): String? {
        return try {
            val directory = File(context.filesDir, "diecast_photos")
            if (!directory.exists()) {
                directory.mkdirs()
            }

            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val file = File(directory, "DIECAST_$timestamp.jpg")

            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            outputStream.flush()
            outputStream.close()

            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Creates a temporary file Uri for camera capture.
     */
    fun createTempCameraFile(context: Context): File {
        val directory = File(context.cacheDir, "camera_temp")
        if (!directory.exists()) {
            directory.mkdirs()
        }
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        return File(directory, "TEMP_PHOTO_$timestamp.jpg")
    }

    /**
     * Resolves an image path, URL, file, or drawable resource name into an object Coil can load.
     */
    fun resolveImageModel(context: Context, photoPath: String?): Any {
        if (photoPath.isNullOrBlank()) {
            return com.example.R.drawable.img_diecast_banner_1785160991932
        }
        val cleanPath = photoPath.trim()
        if (File(cleanPath).exists()) {
            return File(cleanPath)
        }
        if (cleanPath.startsWith("http://") || 
            cleanPath.startsWith("https://") || 
            cleanPath.startsWith("content://") || 
            cleanPath.startsWith("file://") || 
            cleanPath.startsWith("android.resource://")) {
            return cleanPath
        }
        val resId = context.resources.getIdentifier(cleanPath, "drawable", context.packageName)
        if (resId != 0) {
            return resId
        }
        return cleanPath
    }
}
