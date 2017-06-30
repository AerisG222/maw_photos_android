package us.mikeandwan.photos.di;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import us.mikeandwan.photos.models.KeyStore;
import us.mikeandwan.photos.services.EncryptionService;


@Module
public class EncryptionModule {
    @Provides
    @Singleton
    KeyStore provideKeyStore(Application application) {
        return new KeyStore(application);
    }

    @Provides
    @Singleton
    EncryptionService provideEncryptionService(KeyStore keystore) {
        return new EncryptionService(keystore);
    }
}
