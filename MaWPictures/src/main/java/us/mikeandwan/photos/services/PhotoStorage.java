package us.mikeandwan.photos.services;

import android.content.Context;
import android.icu.util.Output;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;

import com.commonsware.cwac.provider.StreamProvider;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

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

        if(!dir.exists()) {
            if(!dir.mkdirs()) {
                Log.e(MawApplication.LOG_TAG, "Error creating photo directory hierarchy: " + dir.getName());
                return;
            }
        }
        else
        {
            if(file.exists()) {
                return;
            }
        }

        // use a unique id here so if we end up downloading the same file 2 times, we don't try to
        // write to the same temp file.  As such, with the final rename, a valid complete file should
        // be put in place
        File tempFile = new File(getTempRootPath(), UUID.randomUUID().toString() + ".tmp");

        try(OutputStream outputStream = new FileOutputStream(tempFile)) {
            outputStream.write(body.bytes());
            outputStream.flush();
            outputStream.close();

            tempFile.renameTo(file);
        } catch (IOException e) {
            Log.w(MawApplication.LOG_TAG, "Error saving image file: " + e.getMessage());
        } finally {
            if(tempFile.exists())
            {
                try {
                    tempFile.delete();
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


    public String getPlaceholderThumbnail() {
        return "file:///android_asset/placeholder.png";
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

        try {
            FileUtils.deleteDirectory(getLegacyRootPath());
        } catch (IOException ex) {
            Log.e(MawApplication.LOG_TAG, "Unable to delete legacy directory: " + ex.getMessage());
        }
    }


    public void wipeTempFiles() {
        try {
            FileUtils.deleteDirectory(getTempRootPath());
        } catch(IOException ex) {
            Log.e(MawApplication.LOG_TAG, "Unable to delete temp files: " + ex.getMessage());
        }
    }


    public void wipeCache() {
        try {
            FileUtils.deleteDirectory(getRootPath());
        } catch (IOException ex) {
            Log.e(MawApplication.LOG_TAG, "Unable to wipe cache: " + ex.getMessage());
        }
    }


    private void wipePriorCache() {
        // with latest site, the dir names are now different (xs, sm, lg, etc) rather than
        // (fullsize, fuller, orig, etc).  this kills the older cache as they will no longer
        // be referenced
        try {
            wipe(getRootPath(), new PriorDirectoryFilter());
        } catch (IOException ex) {
            Log.e(MawApplication.LOG_TAG, "Unable to delete newer cache directory: " + ex.getMessage());
        }
    }


    private void wipe(File dir, FileFilter filter) throws IOException {
        if(dir == null || dir.isFile()) {
            return;
        }

        // remove files/directories matching the filter
        for (File file : dir.listFiles(filter)) {
            if (file.isFile()) {
                file.delete();
            } else {
                try {
                    FileUtils.deleteDirectory(dir);
                } catch(IOException ex) {
                    Log.e(MawApplication.LOG_TAG, "Unable to delete directory: " + ex.getMessage());
                }
            }
        }

        // recurse through any remaining directories
        for (File file : dir.listFiles(new DirectoryFilter())) {
            wipe(file, filter);
        }
    }


    private File getLegacyRootPath() {
        return new File(String.valueOf(Environment.getExternalStorageDirectory()) + "/" + MAW_ROOT);
    }


    private File getTempRootPath() {
        File dir = new File(getRootPath() + "/" + "temp");

        if(!dir.exists()) {
            dir.mkdir();
        }

        return dir;
    }


    private class DirectoryFilter implements FileFilter {
        public boolean accept(File file) {
            return file.isDirectory();
        }
    }


    private class PriorDirectoryFilter implements FileFilter {
        public boolean accept(File file) {
            return file.isDirectory() && ("thumbnails".equalsIgnoreCase(file.getName()) ||
                                          "fuller".equalsIgnoreCase(file.getName()) ||
                                          "fullsize".equalsIgnoreCase(file.getName()) ||
                                          "orig".equalsIgnoreCase(file.getName()));
        }
    }
}
