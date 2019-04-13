package us.mikeandwan.photos.models;


public class Rating {
    private Short _userRating;
    private Float _averageRating;

    public Short getUserRating() { return _userRating; }
    public void setUserRating(Short yourRating) { _userRating = yourRating; }

    public Float getAverageRating() { return _averageRating; }
    public void setAverageRating(Float averageRating) { _averageRating = averageRating; }
}
