package us.mikeandwan.photos.models

class FileOperationResult {
    lateinit var operation: String
    lateinit var relativePathSpecified: String
    lateinit var uploadedFile: UploadedFile
    var wasSuccessful = false
    var error: String? = null
}