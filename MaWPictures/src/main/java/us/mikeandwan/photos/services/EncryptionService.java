package us.mikeandwan.photos.services;

import java.security.GeneralSecurityException;

import javax.crypto.Cipher;

import us.mikeandwan.photos.models.KeyInfo;
import us.mikeandwan.photos.models.KeyStore;


// https://android-developers.googleblog.com/2016/06/security-crypto-provider-deprecated-in.html
public class EncryptionService {
    private final static String HEX = "0123456789ABCDEF";

    private KeyStore _keyStore;


    public EncryptionService(KeyStore keyStore) {
        _keyStore = keyStore;
    }


    public String encrypt(String cleartext) {
        byte[] result = encryptOrDecrypt(_keyStore.getKey(), true, cleartext.getBytes());

        return toHex(result);
    }


    public String decrypt(String encrypted) {
        byte[] enc = toByte(encrypted);
        byte[] result = encryptOrDecrypt(_keyStore.getKey(), false, enc);

        return new String(result);
    }


    private byte[] encryptOrDecrypt(KeyInfo keyInfo, boolean forEncryption, byte[] data) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7PADDING");
            int mode = forEncryption ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE;

            cipher.init(mode, keyInfo.getSecretKey(), keyInfo.getIvParameterSpec());

            return cipher.doFinal(data);
        } catch(GeneralSecurityException gse) {
            throw new RuntimeException("This should not happen!");
        }
    }


    private static byte[] toByte(String hexString) {
        int len = hexString.length() / 2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++)
            result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2), 16).byteValue();
        return result;
    }


    private static String toHex(byte[] buf) {
        if (buf == null)
            return "";
        StringBuffer result = new StringBuffer(2 * buf.length);
        for (byte aBuf : buf) {
            appendHex(result, aBuf);
        }
        return result.toString();
    }


    private static void appendHex(StringBuffer sb, byte b) {
        sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
    }
}
