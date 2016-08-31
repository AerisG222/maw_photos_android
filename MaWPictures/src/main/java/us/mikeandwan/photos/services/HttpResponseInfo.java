package us.mikeandwan.photos.services;


class HttpResponseInfo {
    private final String _content;
    private final int _statusCode;


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
