package us.mikeandwan.photos.di

import android.app.Application
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationAPIClient
import com.auth0.android.authentication.storage.BaseCredentialsManager
import com.auth0.android.authentication.storage.SecureCredentialsManager
import com.auth0.android.authentication.storage.SharedPreferencesStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import us.mikeandwan.photos.R
import us.mikeandwan.photos.authorization.AuthInterceptor
import us.mikeandwan.photos.authorization.AuthService
import us.mikeandwan.photos.authorization.LegacyCredentialPurge
import us.mikeandwan.photos.authorization.TokenAuthenticator

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {
    @Provides
    @Singleton
    fun provideAuthService(
        application: Application,
        auth0: Auth0,
        credManager: BaseCredentialsManager,
    ): AuthService = AuthService(application, auth0, credManager)

    @Provides
    @Singleton
    fun provideAuthInterceptor(
        application: Application,
        credManager: BaseCredentialsManager,
    ): AuthInterceptor = AuthInterceptor(application, credManager)

    @Provides
    @Singleton
    fun provideTokenAuthenticator(
        authService: AuthService,
        credManager: BaseCredentialsManager,
    ): TokenAuthenticator = TokenAuthenticator(authService, credManager)

    @Provides
    @Singleton
    fun provideAuth0(application: Application): Auth0 =
        Auth0.getInstance(
            application.getString(R.string.auth0_client_id),
            application.getString(R.string.auth0_domain),
        )

    @Provides
    @Singleton
    fun provideAuth0AuthenticationClient(account: Auth0): AuthenticationAPIClient = AuthenticationAPIClient(account)

    @Provides
    @Singleton
    fun provideAuth0SharedPreferencesStorage(application: Application): SharedPreferencesStorage =
        SharedPreferencesStorage(application)

    // SecureCredentialsManager keeps the credentials as a single blob encrypted with a Keystore
    // backed key rather than as the bare strings the plain CredentialsManager writes.  The exposed
    // copies the app wrote before this change are dealt with by LegacyCredentialPurge.
    @Provides
    @Singleton
    fun provideAuth0CredentialManager(
        application: Application,
        client: AuthenticationAPIClient,
        storage: SharedPreferencesStorage,
    ): BaseCredentialsManager =
        SecureCredentialsManager(
            client,
            application,
            storage,
        )

    @Provides
    @Singleton
    fun provideLegacyCredentialPurge(
        client: AuthenticationAPIClient,
        storage: SharedPreferencesStorage,
    ): LegacyCredentialPurge =
        LegacyCredentialPurge(
            storage,
            client,
        )
}
