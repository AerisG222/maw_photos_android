package us.mikeandwan.photos.services;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.commonsware.cwac.provider.StreamProvider;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import us.mikeandwan.photos.MawApplication;


public class PhotoStorage {
    private static final String AUTHORITY = "us.mikeandwan.streamprovider";
    private static final Uri PROVIDER = Uri.parse("content://"+AUTHORITY);
    private static final SimpleDateFormat _dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH);
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


    public File[] getQueuedFilesForUpload() {
        File uploadDir = getUploadDir();

        return uploadDir.listFiles();
    }


    public boolean enqueueFileToUpload(int id, InputStream inputStream, String mimeType) {
        MimeTypeMap map = MimeTypeMap.getSingleton();
        String extension = map.getExtensionFromMimeType(mimeType);
        File file = getNewUploadFilePath(id, mimeType.substring(0, mimeType.indexOf('/')), extension);

        try(OutputStream outputStream = new FileOutputStream(file)) {
            byte[] buf = new byte[4096];

            while (true) {
                int bytesRead = inputStream.read(buf);

                if (bytesRead == -1) {
                    break;
                }

                outputStream.write(buf, 0, bytesRead);
            }

            outputStream.flush();
        } catch (IOException e) {
            Log.w(MawApplication.LOG_TAG, "Error saving image file: " + e.getMessage());

            return false;
        }

        return true;
    }


    public void deleteFileToUpload(File file) {
        file.delete();
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


    private File getUploadDir() {
        File uploadDir = new File(getRootPath(), "__upload");

        if(!uploadDir.exists()) {
            uploadDir.mkdir();
        }

        return uploadDir;
    }


    private File getNewUploadFilePath(int id, String filePrefix, String extension) {
        File uploadDir = getUploadDir();
        Date now = new Date();
        String filename = String.format(Locale.ENGLISH, "%s_%s_%d", filePrefix, _dateFormat.format(now), id);

        return new File(uploadDir, filename + "." + extension);
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


    private File getTempRootPath() {
        File dir = new File(getRootPath() + "/" + "temp");

        if(!dir.exists()) {
            dir.mkdir();
        }

        return dir;
    }
}
