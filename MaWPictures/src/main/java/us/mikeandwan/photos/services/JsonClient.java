package us.mikeandwan.photos.services;


import android.util.Log;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import us.mikeandwan.photos.MawApplication;


class JsonClient<T> {
    private static final ObjectMapper _mapper = new ObjectMapper();

    private final PhotoApiClient _apiClient;
    private boolean _isSecondAttempt;
    private final Class<T> _typeParameterClass;


    public JsonClient(Class<T> typeParameterClass, PhotoApiClient apiClient) {
        _typeParameterClass = typeParameterClass;
        _apiClient = apiClient;
    }


    public T getSingleItem(String url) throws MawAuthenticationException {
        JsonParser parser = null;

        try {
            parser = _mapper.getFactory().createParser(new URL(url));
            parser.nextToken();
            T t = _mapper.readValue(parser, _typeParameterClass);

            _isSecondAttempt = false;

            return t;
        } catch (JsonParseException ex) {
            if (_isSecondAttempt) {
                _isSecondAttempt = false;  // reset
                return null;
            } else {
                Log.e(MawApplication.LOG_TAG, "invalid json result, this often is due to timeout, we will try again after forcing login");

                _apiClient.ensureAuthenticated(true);
                _isSecondAttempt = true;
                return getSingleItem(url);
            }
        } catch (Exception ex) {
            Log.e(MawApplication.LOG_TAG, "other error: " + ex.getMessage());
        } finally {
            if (parser != null && !parser.isClosed()) {
                try {
                    parser.close();
                } catch (IOException ex) {
                    Log.w(MawApplication.LOG_TAG, "unable to close: " + ex.getMessage());
                }
            }
        }

        return null;
    }


    public List<T> getItemList(String url) throws MawAuthenticationException {
        JsonParser parser = null;
        List<T> result = new ArrayList<>();

        try {
            parser = _mapper.getFactory().createParser(new URL(url));

            JsonToken currentToken = parser.nextToken();

            // if the api call returns a single result not wrapped in an array, pack it in the list
            if (currentToken != JsonToken.START_ARRAY) {
                T t = _mapper.readValue(parser, _typeParameterClass);
                result.add(t);
            } else {
                // iterate over the results in the array until we hit the end of the list
                for (currentToken = parser.nextToken(); currentToken != JsonToken.END_ARRAY; currentToken = parser.nextToken()) {
                    T t = _mapper.readValue(parser, _typeParameterClass);
                    result.add(t);
                }
            }

            _isSecondAttempt = false;

            parser.close();
        } catch (JsonParseException ex) {
            if (_isSecondAttempt) {
                _isSecondAttempt = false; //reset
                return result;
            } else {
                Log.e(MawApplication.LOG_TAG, "invalid json result, this often is due to timeout, we will try again after forcing login");

                _apiClient.ensureAuthenticated(true);
                _isSecondAttempt = true;
                return getItemList(url);
            }
        } catch (Exception ex) {
            Log.w(MawApplication.LOG_TAG, "Error when getting result list: " + ex.getMessage());
        } finally {
            if (parser != null && !parser.isClosed()) {
                try {
                    parser.close();
                } catch (IOException ex) {
                    Log.w(MawApplication.LOG_TAG, "unable to close: " + ex.getMessage());
                }
            }
        }

        return result;
    }


    public String toJson(T item) {
        try {
            return _mapper.writeValueAsString(item);
        } catch (Exception ex) {
            Log.w(MawApplication.LOG_TAG, "Error converting object to json: " + ex.getMessage());
        }

        return null;
    }
}
