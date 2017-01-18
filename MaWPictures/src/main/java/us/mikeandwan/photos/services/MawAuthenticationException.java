package us.mikeandwan.photos.services;


public class MawAuthenticationException extends Exception {
    private static final long serialVersionUID = 1;

    public MawAuthenticationException() {
        this("Unable to authenticate to mikeandwan.us");
    }

    private MawAuthenticationException(String message) {
        super(message);
    }
}
