package us.mikeandwan.photos.models;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import us.mikeandwan.photos.MawApplication;


public class KeyStore {
    private Context _context;
    private KeyInfo _keyInfo;


    public KeyStore(Context context) {
        _context = context;
    }


    public KeyInfo getKey() {
        if(_keyInfo != null) {
            return _keyInfo;
        }

        loadKeyInfo();

        if(_keyInfo == null) {
            createKeyInfo();
        }

        return _keyInfo;
    }


    private void loadKeyInfo() {
        File ivFile = getIvFile();
        File keyFile = getKeyFile();

        if(ivFile.exists() && keyFile.exists()) {
            prepareKeyInfo(readFile(keyFile), readFile(ivFile));
        }
    }


    private void createKeyInfo() {
        try {
            String pwd = "33vXg8c2a3n%c=_;eF:imrpK^FOQz+}8";
            KeySpec keySpec = new PBEKeySpec(pwd.toCharArray(), generateSalt(), 1000, 256);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

            byte[] keyBytes = keyFactory.generateSecret(keySpec).getEncoded();
            byte[] ivBytes = generateIv();

            writeToFile(getKeyFile(), keyBytes);
            writeToFile(getIvFile(), ivBytes);

            prepareKeyInfo(keyBytes, ivBytes);
        } catch(Exception ex) {
            Log.e(MawApplication.LOG_TAG, "Error creating secret key!", ex);
        }
    }


    private void prepareKeyInfo(byte[] keyBytes, byte[] ivBytes) {
        SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec iv = new IvParameterSpec(ivBytes);

        _keyInfo = new KeyInfo(secretKey, iv);
    }


    private byte[] generateSalt() {
        return generateRandom(32);
    }


    private byte[] generateIv() {
        return generateRandom(16);
    }


    private byte[] generateRandom(int length) {
        byte[] randomBytes = new byte[length];
        SecureRandom random = new SecureRandom();

        random.nextBytes(randomBytes);

        return randomBytes;
    }


    private File getIvFile() {
        return new File(_context.getFilesDir(), "iv");
    }


    private File getKeyFile() {
        return new File(_context.getFilesDir(), "key");
    }


    private void writeToFile(File file, byte[] bytes) {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(bytes);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't write to " + file.getName(), e);
        }
    }


    private byte[] readFile(File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] data = new byte[(int)file.length()];
            fis.read(data);
            fis.close();

            return data;
        } catch (IOException e) {
            throw new RuntimeException("Couldn't read from " + file.getName(), e);
        }
    }
}
