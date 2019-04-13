package us.mikeandwan.photos.models;


import java.io.Serializable;

public class MultimediaAsset implements Serializable {
    private static final long serialVersionUID = 1;

    private int _height;
    private int _width;
    private String _url;
    private long _size;


    public int getHeight() { return _height; }
    public void setHeight(int height) { _height = height; }

    public int getWidth() { return _width; }
    public void setWidth(int width) { _width = width; }

    public String getUrl() { return _url; }
    public void setUrl(String path) { _url = path; }

    public long getSize() { return _size; }
    public void setSize(long size) { _size = size; }
}
