package us.mikeandwan.photos.models;


import java.io.Serializable;


public class Photo implements Serializable {
    private static final long serialVersionUID = 1;

    private int _id;
    private int _categoryId;
    private MultimediaAsset _imageXs;
    private MultimediaAsset _imageSm;
    private MultimediaAsset _imageMd;
    private MultimediaAsset _imageLg;


    public int getId() { return _id; }
    public void setId(int id) { _id = id; }

    public int getCategoryId() { return _categoryId; }
    public void setCategoryId(int categoryId) { _categoryId = categoryId; }

    public MultimediaAsset getImageXs() { return _imageXs; }
    public void setImageXs(MultimediaAsset imageXs) { _imageXs = imageXs; }

    public MultimediaAsset getImageSm() { return _imageSm; }
    public void setImageSm(MultimediaAsset imageSm) { _imageSm = imageSm; }

    public MultimediaAsset getImageMd() { return _imageMd; }
    public void setImageMd(MultimediaAsset imageMd) { _imageMd = imageMd; }

    public MultimediaAsset getImageLg() { return _imageLg; }
    public void setImageLg(MultimediaAsset imageLg) { _imageLg = imageLg; }


    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }

        if (this == object) {
            return true;
        }

        if (object instanceof Photo) {
            Photo other = (Photo) object;

            return this._id == other._id;
        } else {
            return false;
        }
    }


    @Override
    public int hashCode() {
        return _id;
    }
}
