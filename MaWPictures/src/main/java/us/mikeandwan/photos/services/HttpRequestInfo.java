package us.mikeandwan.photos.services;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;


public class HttpRequestInfo {
    private URL _url;

    public URL getUrl() {
        return _url;
    }

    public void setUrl(URL url) {
        _url = url;
    }

    private String _method;

    public String getMethod() {
        return _method;
    }

    public void setMethod(String method) {
        _method = method;
    }

    private Map<String, String> _params = new HashMap<>();

    public Map<String, String> getParams() {
        return _params;
    }

    private String _jsonParam;

    public String getJsonParam() {
        return _jsonParam;
    }

    public void setJsonParam(String jsonParam) {
        _jsonParam = jsonParam;
    }
}
