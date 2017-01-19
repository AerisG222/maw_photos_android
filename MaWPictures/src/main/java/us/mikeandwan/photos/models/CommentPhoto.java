package us.mikeandwan.photos.models;


public class CommentPhoto {
    private int _photoId;

    public int getPhotoId() {
        return _photoId;
    }

    public void setPhotoId(int photoId) {
        _photoId = photoId;
    }

    private String _comment;

    public String getComment() {
        return _comment;
    }

    public void setComment(String comment) {
        _comment = comment;
    }
}
