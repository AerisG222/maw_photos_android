package us.mikeandwan.photos.domain

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Environment
import android.webkit.MimeTypeMap
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import java.io.FileFilter
import java.io.FileOutputStream
import java.lang.Exception
import javax.inject.Inject

class FileStorageRepository @Inject constructor(
    private val _context: Context
) {
    companion object {
        val mimeTypeMap = MimeTypeMap.getSingleton()!!
    }

    fun getPendingUploads(): Flow<List<File>> = flow {
        val dir = getUploadDirectory()

        if(dir == null) {
            emit(emptyList<File>())
        } else {
            emit(dir.listFiles(FileFilter { it.isFile }).asList())
        }
    }

    suspend fun savePhotoToShare(drawable: Drawable, originalFilename: String): File {
        return withContext(Dispatchers.IO) {
            val fileToShare = getShareFile(originalFilename)

            if(fileToShare.exists()) {
                // if there is an old share with the same name, it may not yet have been cleaned up yet
                fileToShare.delete()
            }

            val outputStream = FileOutputStream(fileToShare)
            val bitmap = (drawable as BitmapDrawable).bitmap

            if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 92, outputStream)) {
                throw Exception("failed to save drawable!")
            }

            fileToShare
        }
    }

    suspend fun saveFileToUpload(mediaUri: Uri): File? {
        val mimeType = _context.contentResolver.getType(mediaUri)

        return when(isValidType(mimeType)) {
            true -> writeUploadFile(mediaUri, mimeType!!)
            false -> null
        }
    }

    suspend fun clearShareCache() {
        withContext(Dispatchers.IO) {
            getShareDirectory()
                ?.walkBottomUp()
                ?.forEach { it.delete() }
        }
    }

    suspend fun clearLegacyFiles() {
        withContext(Dispatchers.IO) {
            _context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                ?.walkBottomUp()
                ?.forEach { it.delete() }
        }
    }

    private suspend fun writeUploadFile(mediaUri: Uri, mimeType: String): File? {
        return withContext(Dispatchers.IO) {
            val uploadFile = getUploadFile(mediaUri, mimeType)

            if(uploadFile.exists()) {
                null
            } else {
                val inputStream = _context.contentResolver.openInputStream(mediaUri)
                val outputStream = uploadFile.outputStream()

                if (inputStream != null) {
                    inputStream.copyTo(outputStream)

                    uploadFile
                } else {
                    null
                }
            }
        }
    }

    private fun getUploadFile(mediaUri: Uri, mimeType: String): File {
        val extension = mimeTypeMap.getExtensionFromMimeType(mimeType)
        val typeName = mimeType.substring(0, mimeType.indexOf('/'))
        val filename = "${typeName}_${mediaUri.lastPathSegment}.$extension"

        return File(getUploadDirectory(), filename)
    }

    private fun getUploadDirectory(): File? {
        return _context.getExternalFilesDir("upload")
    }

    private fun getShareFile(originalFilename: String): File {
        return File(getShareDirectory(), originalFilename)
    }

    private fun getShareDirectory(): File? {
        return _context.getExternalFilesDir("photos_to_share")
    }

    private fun isValidType(mimeType: String?): Boolean {
        return mimeType != null && (mimeType.startsWith("image/") || mimeType.startsWith("video/"))
    }
}