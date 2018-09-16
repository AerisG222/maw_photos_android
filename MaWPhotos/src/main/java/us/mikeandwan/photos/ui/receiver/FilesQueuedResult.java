package us.mikeandwan.photos.ui.receiver;

import java.io.File;

public class FilesQueuedResult {
    private int _count = 0;
    private File[] _files;


    public FilesQueuedResult(int count, File[] files) {
        _count = count;
        _files = files;
    }


    public int getCount() {
        return _count;
    }


    public File[] getQueuedFiles() {
        return _files;
    }
}
