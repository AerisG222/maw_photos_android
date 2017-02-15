package us.mikeandwan.photos.services;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.commonsware.cwac.provider.StreamProvider;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import us.mikeandwan.photos.MawApplication;


// TODO: this currently requires external storage, perhaps also allow this to run w/o caching - perhaps thumbnails in internal storage?
public class PhotoStorage {
    private static final String MAW_ROOT = "maw_pictures";
    private static final String AUTHORITY = "us.mikeandwan.streamprovider";
    private static final Uri PROVIDER = Uri.parse("content://"+AUTHORITY);

    private Context _context;


    @Inject
    public PhotoStorage(Context context) {
        _context = context;
    }


    void put(String remotePath, ResponseBody body) {
        File dir = new File(getRootPath(), remotePath.substring(0, remotePath.lastIndexOf('/')));
        File file = getCachePath(remotePath);
        OutputStream outputStream = null;

        if(!dir.exists()) {
            if(!dir.mkdirs()) {
                Log.e(MawApplication.LOG_TAG, "Error creating photo directory hierarchy: " + dir.getName());
                return;
            }
        }

        try {
            byte[] buffer = new byte[4096];
            int len;
            InputStream inputStream = body.byteStream();
            outputStream = new FileOutputStream(file);

            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
        } catch (IOException e) {
            Log.w(MawApplication.LOG_TAG, "Error saving image file: " + e.getMessage());
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (Exception ex) {
                    // swallow
                }
            }
        }
    }


    public boolean doesExist(String remotePath) {
        File file = getCachePath(remotePath);

        return file.exists();
    }


    public Bitmap get(String remotePath) {
        File file = getCachePath(remotePath);

        if (!file.exists()) {
            return null;
        }

        return BitmapFactory.decodeFile(file.getAbsolutePath());
    }


    public Bitmap getPlaceholderThumbnail() {
        return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
    }


    public File getCachePath(String remotePath) {
        return new File(getRootPath(), remotePath);
    }


    public Uri getSharingContentUri(String remotePath) {
        File file = new File(Environment.DIRECTORY_PICTURES, remotePath);

        return PROVIDER
                .buildUpon()
                .appendPath(StreamProvider.getUriPrefix(AUTHORITY))
                .appendEncodedPath(file.getPath())
                .build();
    }


    private File getRootPath() {
        return _context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    }


    public void wipeLegacyCache() {
        wipePriorCache();

        File dir = new File(getLegacyRootPath());

        try {
            FileUtils.deleteDirectory(dir);
        } catch (IOException ex) {
            Log.e(MawApplication.LOG_TAG, "Unable to delete legacy directory: " + ex.getMessage());
        }
    }


    private void wipePriorCache() {
        // with latest site, the dir names are now different (xs, sm, lg, etc) rather than
        // (fullsize, fuller, orig, etc).  this kills the older cache as they will no longer
        // be referenced
        try {
            wipePrior(getRootPath());
        } catch (IOException ex) {
            Log.e(MawApplication.LOG_TAG, "Unable to delete newer cache directory: " + ex.getMessage());
        }
    }


    private void wipePrior(File dir) throws IOException {
        if(dir == null) {
            return;
        }

        if(isPrior(dir)) {
            FileUtils.deleteDirectory(dir);
            return;
        }

        for(File subdir : dir.listFiles(new DirectoryFilter())) {
            wipePrior(subdir);
        }
    }


    private String getLegacyRootPath() {
        return String.valueOf(Environment.getExternalStorageDirectory()) + "/" + MAW_ROOT;
    }


    private boolean isPrior(File dir) {
        return dir.isDirectory() && ("thumbnails".equalsIgnoreCase(dir.getName()) ||
                                     "fuller".equalsIgnoreCase(dir.getName()) ||
                                     "fullsize".equalsIgnoreCase(dir.getName()) ||
                                     "orig".equalsIgnoreCase(dir.getName()));
    }


    private class DirectoryFilter implements FileFilter {
        public boolean accept(File file) {
            return file.isDirectory();
        }
    }
}
