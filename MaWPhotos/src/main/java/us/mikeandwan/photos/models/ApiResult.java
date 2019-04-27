package us.mikeandwan.photos.models;

import java.io.IOException;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Response;


public class ApiResult<T> {
    private T _result;
    private String _error;
    private boolean _success;

    public T getResult() { return _result; }
    public String getError() { return _error; }
    public boolean isSuccess() { return _success; }


    public ApiResult(Response<T> response) {
        if(response == null) {
            _success = false;
            _error = "Response was null.  Unable to extract result from API call.";
        }
        else {
            if(response.isSuccessful()) {
                _success = true;
                _result = response.body();
            } else {
                _success = false;

                ResponseBody body = response.errorBody();
                String message = response.message();

                if(body != null) {
                    try {
                        message = body.string();
                    }
                    catch(IOException ioe) {
                        message = response.message();
                    }
                }

                _error = String.format(Locale.ENGLISH, "api error response: %d | %s", response.code(), message);
            }
        }
    }
}
