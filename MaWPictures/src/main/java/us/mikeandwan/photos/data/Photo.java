package us.mikeandwan.photos.data;


import java.io.Serializable;

public class Photo implements Serializable {
    private int _id;
    private PhotoInfo _thumbInfo;
    private PhotoInfo _fullInfo;
    private PhotoInfo _fullerInfo;
    private PhotoInfo _origInfo;


    public int getId() {
        return _id;
    }


    public void setId(int id) {
        _id = id;
    }


    public PhotoInfo getThumbnailInfo() {

        return _thumbInfo;
    }


    public void setThumbnailInfo(PhotoInfo thumbInfo) {
        _thumbInfo = thumbInfo;
    }


    public PhotoInfo getFullsizeInfo() {
        return _fullInfo;
    }


    public void setFullsizeInfo(PhotoInfo fullInfo) {
        _fullInfo = fullInfo;
    }

    public PhotoInfo getFullerInfo() {
        return _fullerInfo;
    }


    public void setFullerInfo(PhotoInfo fullerInfo) {
        _fullerInfo = fullerInfo;
    }

    public PhotoInfo getOriginalInfo() {
        return _origInfo;
    }


    public void setOriginalInfo(PhotoInfo origInfo) {
        _origInfo = origInfo;
    }


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
