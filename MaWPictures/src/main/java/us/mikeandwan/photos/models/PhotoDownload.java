package us.mikeandwan.photos.models;


public class PhotoDownload {
    private Photo _photo;
    private int _index;
    private int _downloadAttempts;


    public PhotoDownload(Photo photo) {
        this(photo, -1);
    }


    public PhotoDownload(Photo photo, int index) {
        _photo = photo;
        _index = index;
    }


    public Photo getMawPhoto() {
        return _photo;
    }


    public void setMawPhoto(Photo photo) {
        _photo = photo;
    }


    public int getIndex() {
        return _index;
    }


    public void setIndex(int index) {
        _index = index;
    }


    public void incrementDownloadCount() {
        _downloadAttempts++;
    }


    public int getDownloadAttempts() {
        return _downloadAttempts;
    }
}
