package us.mikeandwan.photos.models;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;


public class UploadedFile {
    private FileLocation _location;
    private Date _creationTime;
    private long _sizeInBytes;


    public FileLocation getLocation() {
        return _location;
    }

    public void setLocation(FileLocation fileLocation) {
        _location = fileLocation;
    }

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSS", timezone="EST")
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
