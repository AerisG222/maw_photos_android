package us.mikeandwan.photos.models;

import java.util.Date;


public class UploadedFile {
    private FileLocation _fileLocation;
    private Date _creationTime;
    private long _sizeInBytes;


    public FileLocation getFileLocation() {
        return _fileLocation;
    }

    public void setFileLocation(FileLocation fileLocation) {
        _fileLocation = fileLocation;
    }

    public Date getCreationTime() {
        return _creationTime;
    }

    public void setCreationTime(Date creationTime) {
        _creationTime = creationTime;
    }

    public long getSizeInBytes() {
        return _sizeInBytes;
    }

    public void setSizeInBytes(long sizeInBytes) {
        _sizeInBytes = sizeInBytes;
    }
}
