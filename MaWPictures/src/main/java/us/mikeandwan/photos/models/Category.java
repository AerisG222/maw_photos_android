package us.mikeandwan.photos.models;


public class Category {
    private int _id;
    private int _year;
    private String _name;
    private boolean _hasGpsData;
    private PhotoInfo _teaserInfo;


    public int getId() {
        return _id;
    }


    public void setId(int id) {
        _id = id;
    }


    public int getYear() {
        return _year;
    }


    public void setYear(int year) {
        _year = year;
    }


    public String getName() {
        return _name;
    }


    public void setName(String name) {
        _name = name;
    }


    public boolean getHasGpsData() {
        return _hasGpsData;
    }


    public void setHasGpsData(boolean hasGpsData) {
        _hasGpsData = hasGpsData;
    }


    public PhotoInfo getTeaserPhotoInfo() {
        return _teaserInfo;
    }


    public void setTeaserPhotoInfo(PhotoInfo teaserInfo) {
        _teaserInfo = teaserInfo;
    }


    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }

        if (this == object) {
            return true;
        }

        if (object instanceof Category) {
            Category other = (Category) object;

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
