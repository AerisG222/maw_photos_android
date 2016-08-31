package us.mikeandwan.photos.data;


import java.io.Serializable;

@SuppressWarnings("ALL")
public class Photo implements Serializable {
    private int _id;
    private int _categoryId;
    private Double _latitude;
    private Double _longitude;
    private PhotoInfo _xsInfo;
    private PhotoInfo _smInfo;
    private PhotoInfo _mdInfo;
    private PhotoInfo _lgInfo;
    private PhotoInfo _prtInfo;


    public int getId() {
        return _id;
    }


    public void setId(int id) {
        _id = id;
    }


    public int getCategoryId() {
        return _categoryId;
    }


    public void setCategoryId(int categoryId) {
        _categoryId = categoryId;
    }


    public Double getLatitude() {
        return _latitude;
    }


    public void setLatitude(Double latitude) {
        _latitude = latitude;
    }


    public Double getLongitude() {
        return _longitude;
    }


    public void setLongitude(Double longitude) {
        _longitude = longitude;
    }


    public PhotoInfo getXsInfo() {
        return _xsInfo;
    }


    public void setXsInfo(PhotoInfo xsInfo) {
        _xsInfo = xsInfo;
    }


    public PhotoInfo getSmInfo() {
        return _smInfo;
    }


    public void setSmInfo(PhotoInfo smInfo) {
        _smInfo = smInfo;
    }


    public PhotoInfo getMdInfo() {
        return _mdInfo;
    }


    public void setMdInfo(PhotoInfo mdInfo) {
        _mdInfo = mdInfo;
    }


    public PhotoInfo getLgInfo() {
        return _lgInfo;
    }


    public void setLgInfo(PhotoInfo lgInfo) {
        _lgInfo = lgInfo;
    }


    public PhotoInfo getPrtInfo() {
        return _prtInfo;
    }


    public void setPrtInfo(PhotoInfo prtInfo) {
        _prtInfo = prtInfo;
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
