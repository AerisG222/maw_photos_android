package us.mikeandwan.photos.models;


public class FileLocation {
    private String _username;
    private String _filename;
    private String _relativePath;

    public String getUsername() {
        return _username;
    }

    public void setUsername(String username) {
        _username = username;
    }

    public String getFilename() {
        return _filename;
    }

    public void setFilename(String filename) {
        _filename = filename;
    }

    public String getRelativePath() {
        return _relativePath;
    }

    public void setRelativePath(String relativePath) {
        _relativePath = relativePath;
    }
}
