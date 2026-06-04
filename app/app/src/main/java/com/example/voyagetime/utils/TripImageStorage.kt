package com.example.voyagetime.utils

import android.content.ContentValues
import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Locale

/**
 * Saves user-selected trip images as real image files in a trip-specific folder.
 *
 * On Android 10+ this writes through MediaStore to:
 * Pictures/VoyageTime/<trip-name>-<trip-id>/
 * so the images are visible from the device Gallery/Photos app as an album/folder.
 */
object TripImageStorage {

    suspend fun copyImageToTripFolder(
        context: Context,
        sourceUri: Uri,
        tripId: String,
        tripName: String,
        prefix: String = "trip_image"
    ): String? = withContext(Dispatchers.IO) {
        val resolver = context.contentResolver
        val mimeType = resolver.getType(sourceUri) ?: "image/jpeg"
        val extension = MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(mimeType)
            ?: sourceUri.lastPathSegment?.substringAfterLast('.', "jpg")?.takeIf { it.length <= 5 }
            ?: "jpg"

        val safeFolderName = buildTripFolderName(tripId, tripName)
        val safePrefix = sanitize(prefix).ifBlank { "trip_image" }
        val fileName = "${safePrefix}_${System.currentTimeMillis()}.$extension"

        runCatching {
            resolver.openInputStream(sourceUri)?.use { input ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val values = ContentValues().apply {
                        put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                        put(MediaStore.Images.Media.MIME_TYPE, mimeType)
                        put(
                            MediaStore.Images.Media.RELATIVE_PATH,
                            "${Environment.DIRECTORY_PICTURES}/VoyageTime/$safeFolderName"
                        )
                        put(MediaStore.Images.Media.IS_PENDING, 1)
                    }

                    val destinationUri = resolver.insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        values
                    ) ?: return@runCatching null

                    resolver.openOutputStream(destinationUri)?.use { output ->
                        input.copyTo(output)
                    }

                    values.clear()
                    values.put(MediaStore.Images.Media.IS_PENDING, 0)
                    resolver.update(destinationUri, values, null, null)
                    destinationUri.toString()
                } else {
                    val baseDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    val directory = File(baseDir, "VoyageTime/$safeFolderName").apply { mkdirs() }
                    val destinationFile = File(directory, fileName)

                    destinationFile.outputStream().use { output ->
                        input.copyTo(output)
                    }

                    MediaScannerConnection.scanFile(
                        context,
                        arrayOf(destinationFile.absolutePath),
                        arrayOf(mimeType),
                        null
                    )
                    Uri.fromFile(destinationFile).toString()
                }
            }
        }.getOrNull()
    }

    fun buildTripFolderName(tripId: String, tripName: String): String {
        val cleanName = sanitize(tripName).ifBlank { "Trip" }
        val cleanId = sanitize(tripId).ifBlank { "0" }
        return "$cleanName-$cleanId"
    }

    private fun sanitize(value: String): String {
        return value
            .trim()
            .replace(Regex("[^A-Za-z0-9 _-]"), "")
            .replace(Regex("\\s+"), "_")
            .lowercase(Locale.ROOT)
            .take(60)
    }
}
