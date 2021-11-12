package us.mikeandwan.photos.api

import com.fasterxml.jackson.annotation.JsonProperty

data class FileOperationResult(
    @JsonProperty("operation") val operation: String,
    @JsonProperty("relativePathSpecified") val relativePathSpecified: String,
    @JsonProperty("uploadedFile") val uploadedFile: UploadedFile,
    @JsonProperty("wasSuccessful") val wasSuccessful: Boolean,
    @JsonProperty("error") val error: String? = null,
)