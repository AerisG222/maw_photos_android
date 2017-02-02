package us.mikeandwan.photos.services;

import android.content.Context;
import android.content.Intent;

import us.mikeandwan.photos.activities.LoginActivity;


public class AuthenticationExceptionHandler {
    private Context _context;


    public AuthenticationExceptionHandler(Context context) {
        _context = context;
    }


    public boolean handleException(Throwable throwable) {
        if (throwable.getCause() instanceof MawAuthenticationException) {
            _context.startActivity(new Intent(_context, LoginActivity.class));

            return true;
        }

        return false;
    }
}
