package us.mikeandwan.photos.services;


public class MawAuthenticationException extends Exception {
    public MawAuthenticationException() {
        this("Unable to authenticate to mikeandwan.us");
    }

    private MawAuthenticationException(String message) {
        super(message);
    }
}
