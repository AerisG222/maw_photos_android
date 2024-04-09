package us.mikeandwan.photos.api

import kotlinx.serialization.Serializable

@Serializable
data class FileOperationResult(
    val operation: String,
    val relativePathSpecified: String,
    val uploadedFile: UploadedFile?,
    val wasSuccessful: Boolean,
    val error: String? = null,
)