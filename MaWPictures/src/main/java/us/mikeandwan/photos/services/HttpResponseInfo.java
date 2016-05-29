package us.mikeandwan.photos.services;


public class HttpResponseInfo {
    private String _content;
    private int _statusCode;


    public HttpResponseInfo(int code, String content) {
        _content = content;
        _statusCode = code;
    }


    public int getStatusCode() {
        return _statusCode;
    }


    public String getContent() {
        return _content;
    }
}
