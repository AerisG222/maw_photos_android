package us.mikeandwan.photos.models;


public class Credentials {
    private String _username;
    private String _password;


    public Credentials() {
        // do nothing
    }


    public String getUsername() {
        return _username;
    }


    public void setUsername(String username) {
        _username = username;
    }


    public String getPassword() {
        return _password;
    }


    public void setPassword(String password) {
        _password = password;
    }
}
