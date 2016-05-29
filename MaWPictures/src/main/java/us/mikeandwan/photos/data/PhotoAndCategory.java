package us.mikeandwan.photos.data;


public class PhotoAndCategory {
    private Photo _photo;
    private Category _category;


    public PhotoAndCategory() {
        // do nothing
    }


    public Photo getPhoto() {
        return _photo;
    }


    public void setPhoto(Photo photo) {
        _photo = photo;
    }


    public Category getCategory() {
        return _category;
    }


    public void setCategory(Category category) {
        _category = category;
    }
}
