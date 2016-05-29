package us.mikeandwan.photos.services;


public class MawAuthenticationException extends Exception {
    public MawAuthenticationException() {
        this("Unable to authenticate against mikeandwan.us");
    }

    public MawAuthenticationException(String message) {
        super(message);
    }
}
