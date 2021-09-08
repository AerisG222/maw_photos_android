package us.mikeandwan.photos.models;


public class CommentPhoto {
    private int _photoId;
    private String _comment;

    public int getPhotoId() { return _photoId; }
    public void setPhotoId(int photoId) { _photoId = photoId; }

    public String getComment() { return _comment; }
    public void setComment(String comment) { _comment = comment; }
}
