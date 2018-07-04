package us.mikeandwan.photos.models;


import java.io.Serializable;

public class PhotoInfo implements Serializable {
    private static final long serialVersionUID = 1;

    private int _height;
    private int _width;
    private String _path;


    public int getHeight() {
        return _height;
    }


    public void setHeight(int height) {
        _height = height;
    }


    public int getWidth() {
        return _width;
    }


    public void setWidth(int width) {
        _width = width;
    }


    public String getPath() {
        return _path;
    }


    public void setPath(String path) {
        _path = path;
    }
}
