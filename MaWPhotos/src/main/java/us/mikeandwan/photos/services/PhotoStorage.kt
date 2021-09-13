package us.mikeandwan.photos.services

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.webkit.MimeTypeMap
import com.commonsware.cwac.provider.StreamProvider
import okhttp3.ResponseBody
import org.apache.commons.io.FileUtils
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class PhotoStorage @Inject constructor(private val _context: Context) {
    fun put(remotePath: String, body: ResponseBody) {
        val dir = File(rootPath, remotePath.substring(0, remotePath.lastIndexOf('/')))
        val file = getCachePath(remotePath)

        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                Timber.e("Error creating photo directory hierarchy: %s", dir.name)
                return
            }
        } else {
            if (file.exists()) {
                return
            }
        }

        // use a unique id here so if we end up downloading the same file 2 times, we don't try to
        // write to the same temp file.  As such, with the final rename, a valid complete file should
        // be put in place
        val tempFile = File(tempRootPath, UUID.randomUUID().toString() + ".tmp")

        try {
            FileOutputStream(tempFile).use { outputStream ->
                outputStream.write(body.bytes())
                outputStream.flush()
                outputStream.close()
                tempFile.renameTo(file)
            }
        } catch (e: IOException) {
            Timber.w("Error saving image file: %s", e.message)
        } finally {
            if (tempFile.exists()) {
                try {
                    tempFile.delete()
                } catch (ex: Exception) {
                    // swallow
                }
            }
        }
    }

    fun doesExist(remotePath: String): Boolean {
        val file = getCachePath(remotePath)

        return file.exists()
    }

    val placeholderThumbnail: String
        get() = "file:///android_asset/placeholder.png"

    fun getCachePath(remotePath: String): File {
        return File(rootPath, remotePath)
    }

    val queuedFilesForUpload: Array<File>
        get() {
            return uploadDir.listFiles() ?: emptyArray<File>()
        }

    fun enqueueFileToUpload(id: Int, inputStream: InputStream, mimeType: String): Boolean {
        val map = MimeTypeMap.getSingleton()
        val extension = map.getExtensionFromMimeType(mimeType)
        val file = getNewUploadFilePath(id, mimeType.substring(0, mimeType.indexOf('/')), extension)

        try {
            FileOutputStream(file).use { outputStream ->
                val buf = ByteArray(4096)

                while (true) {
                    val bytesRead = inputStream.read(buf)
                    if (bytesRead == -1) {
                        break
                    }
                    outputStream.write(buf, 0, bytesRead)
                }

                outputStream.flush()
            }
        } catch (e: IOException) {
            Timber.w("Error saving image file: %s", e.message)
            return false
        }
        return true
    }

    fun deleteFileToUpload(file: File) {
        file.delete()
    }

    fun getSharingContentUri(remotePath: String): Uri {
        val file = File(Environment.DIRECTORY_PICTURES, remotePath)

        return PROVIDER
            .buildUpon()
            .appendPath(StreamProvider.getUriPrefix(AUTHORITY))
            .appendEncodedPath(file.path)
            .build()
    }

    private val rootPath: File?
        get() = _context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

    private val uploadDir: File
        get() {
            val uploadDir = File(rootPath, "__upload")

            if (!uploadDir.exists()) {
                uploadDir.mkdir()
            }

            return uploadDir
        }

    private fun getNewUploadFilePath(id: Int, filePrefix: String, extension: String?): File {
        val uploadDir = uploadDir
        val now = Date()
        val filename =
            String.format(Locale.ENGLISH, "%s_%s_%d", filePrefix, _dateFormat.format(now), id)

        return File(uploadDir, "$filename.$extension")
    }

    fun wipeTempFiles() {
        try {
            FileUtils.deleteDirectory(tempRootPath)
        } catch (ex: IOException) {
            Timber.e("Unable to delete temp files: %s", ex.message)
        }
    }

    fun wipeCache() {
        try {
            FileUtils.deleteDirectory(rootPath)
        } catch (ex: IOException) {
            Timber.e("Unable to wipe cache: %s", ex.message)
        }
    }

    private val tempRootPath: File
        get() {
            val dir = File(rootPath.toString() + "/" + "temp")

            if (!dir.exists()) {
                dir.mkdir()
            }

            return dir
        }

    companion object {
        private const val AUTHORITY = "us.mikeandwan.streamprovider"
        private val PROVIDER = Uri.parse("content://$AUTHORITY")
        private val _dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH)
    }
}