package us.mikeandwan.photos.models;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;


public class KeyInfo {
    private SecretKey _key;
    private IvParameterSpec _ivSpec;


    public KeyInfo(SecretKey key, IvParameterSpec ivSpec) {
        _key = key;
        _ivSpec = ivSpec;
    }


    public SecretKey getSecretKey() {
        return _key;
    }


    public IvParameterSpec getIvParameterSpec() {
        return _ivSpec;
    }
}
