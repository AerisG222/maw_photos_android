package us.mikeandwan.photos.models;


public class FileOperationResult {
    private String _fileOperation;
    private String _relativePathSpecified;
    private UploadedFile _uploadedFile;
    private boolean _wasSuccessful;
    private String _error;

    public String getFileOperation() {
        return _fileOperation;
    }

    public void setFileOperation(String fileOperation) {
        _fileOperation = fileOperation;
    }

    public String getRelativePathSpecified() {
        return _relativePathSpecified;
    }

    public void setRelativePathSpecified(String relativePathSpecified) {
        _relativePathSpecified = relativePathSpecified;
    }

    public UploadedFile getUploadedFile() {
        return _uploadedFile;
    }

    public void setUploadedFile(UploadedFile uploadedFile) {
        _uploadedFile = uploadedFile;
    }

    public boolean getWasSuccessful() {
        return _wasSuccessful;
    }

    public void setWasSuccessful(boolean wasSuccessful) {
        _wasSuccessful = wasSuccessful;
    }

    public String getError() {
        return _error;
    }

    public void setError(String error) {
        _error = error;
    }
}
