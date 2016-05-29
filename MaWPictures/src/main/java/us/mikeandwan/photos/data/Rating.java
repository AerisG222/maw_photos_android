package us.mikeandwan.photos.data;


public class Rating {
    private Short _userRating;

    public Short getUserRating() {
        return _userRating;
    }

    public void setUserRating(Short yourRating) {
        _userRating = yourRating;
    }

    private Float _averageRating;

    public Float getAverageRating() {
        return _averageRating;
    }

    public void setAverageRating(Float averageRating) {
        _averageRating = averageRating;
    }
}
