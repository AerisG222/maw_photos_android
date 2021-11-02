package us.mikeandwan.photos.domain

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.util.*
import javax.inject.Inject

class FileStorageRepository @Inject constructor(
    private val _context: Context
) {
    suspend fun savePhotoToShare(drawable: Drawable, originalFilename: String): File {
        return withContext(Dispatchers.IO) {
            var fileToShare = getShareFile(originalFilename)
            val outputStream = FileOutputStream(fileToShare)
            val bitmap = (drawable as BitmapDrawable).bitmap

            if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 92, outputStream)) {
                throw Exception("failed to save drawable!")
            }

            fileToShare
        }
    }

    private fun getShareFile(originalFilename: String): File {
        val extension = originalFilename.substringAfterLast('.')
        val rootPath = _context.getExternalFilesDir("photos")

        return File(rootPath, "${UUID.randomUUID()}.${extension}")
    }
}