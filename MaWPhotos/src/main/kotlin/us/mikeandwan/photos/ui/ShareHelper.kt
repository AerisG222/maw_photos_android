package us.mikeandwan.photos.ui

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.core.content.FileProvider
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import us.mikeandwan.photos.domain.models.Photo
import us.mikeandwan.photos.utils.getFilenameFromUrl
import java.io.File

suspend fun sharePhoto(
    ctx: Context,
    savePhotoToShare: (drawable: Drawable, filename: String, onComplete: (File) -> Unit) -> Unit,
    photo: Photo
) {
    val drawable = getPhotoToShare(ctx, photo)

    savePhotoToShare(
        drawable,
        getFilenameFromUrl(photo.mdUrl)
    ) { fileToShare ->
        val contentUri = FileProvider.getUriForFile(ctx, "us.mikeandwan.photos.fileprovider", fileToShare)
        val sendIntent = Intent(Intent.ACTION_SEND)

        sendIntent.setDataAndType(contentUri, "image/*")
        sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        sendIntent.putExtra(Intent.EXTRA_STREAM, contentUri)

        val shareIntent = Intent.createChooser(sendIntent, null)

        ctx.startActivity(shareIntent)
    }
}

private suspend fun getPhotoToShare(ctx: Context, photo: Photo): Drawable {
    return withContext(Dispatchers.IO) {
        val loader = ImageLoader(ctx)
        val request = ImageRequest.Builder(ctx)
            .data(photo.mdUrl)
            .allowHardware(false) // Disable hardware bitmaps.
            .build()

        (loader.execute(request) as SuccessResult).drawable
    }
}
