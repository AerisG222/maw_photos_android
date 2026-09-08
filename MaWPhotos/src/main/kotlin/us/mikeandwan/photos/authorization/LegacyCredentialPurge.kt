package us.mikeandwan.photos.authorization

import com.auth0.android.authentication.AuthenticationAPIClient
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.authentication.storage.Storage
import timber.log.Timber

/**
 * Revokes and deletes the credentials left behind by the plaintext `CredentialsManager` this app
 * used before it moved to `SecureCredentialsManager`.
 *
 * The old manager wrote the access, id and refresh tokens as bare strings into the
 * `com.auth0.authentication.storage` preferences file, where anything with root and every cloud
 * backup could read them.  The two managers key their storage differently, so simply swapping the
 * provider leaves those strings on disk forever - nothing ever reads or overwrites them again.
 *
 * Deleting them locally is only half the job: this tenant does not rotate refresh tokens, so a
 * refresh token that already leaked stays good against Auth0 until it is explicitly revoked.  The
 * copy on disk is the last chance to name it, so it is revoked before it is deleted.
 */
class LegacyCredentialPurge(
    private val storage: Storage,
    private val apiClient: AuthenticationAPIClient,
) {
    suspend fun run() {
        val refreshToken = storage.retrieveString(KEY_REFRESH_TOKEN)

        val hasLegacyCredentials = refreshToken != null ||
            storage.retrieveString(KEY_ACCESS_TOKEN) != null ||
            storage.retrieveString(KEY_ID_TOKEN) != null

        if (!hasLegacyCredentials) {
            return
        }

        if (refreshToken != null && !revoke(refreshToken)) {
            // only the network stood in the way, so the tokens wait on disk for another launch
            // rather than being deleted while they are still live credentials at Auth0
            return
        }

        purge()
    }

    /** @return whether there is anything left to gain by keeping the token around */
    private suspend fun revoke(refreshToken: String): Boolean =
        try {
            apiClient.revokeToken(refreshToken).await()

            Timber.i("Revoked the refresh token that had been left in plaintext storage")

            true
        } catch (e: AuthenticationException) {
            if (e.isNetworkError) {
                Timber.w(e, "Could not reach Auth0 to revoke the legacy refresh token; will retry")

                false
            } else {
                // Auth0 turned the token down, so it is already dead or was never one of ours -
                // there is nothing left to revoke and holding it only prolongs the exposure
                Timber.w(e, "Auth0 rejected the legacy refresh token; purging it anyway")

                true
            }
        } catch (t: Throwable) {
            Timber.e(t, "Unexpected failure revoking the legacy refresh token; will retry")

            false
        }

    private fun purge() {
        LEGACY_KEYS.forEach { storage.remove(it) }

        Timber.i("Purged the legacy plaintext credentials")
    }

    companion object {
        private const val KEY_ACCESS_TOKEN = "com.auth0.access_token"
        private const val KEY_REFRESH_TOKEN = "com.auth0.refresh_token"
        private const val KEY_ID_TOKEN = "com.auth0.id_token"

        // every key the old manager wrote that the new one does not also use.  com.auth0.token_type
        // is deliberately absent: both managers write it, and it holds nothing sensitive
        private val LEGACY_KEYS = listOf(
            KEY_ACCESS_TOKEN,
            KEY_REFRESH_TOKEN,
            KEY_ID_TOKEN,
            "com.auth0.expires_at",
            "com.auth0.scope",
            "com.auth0.cache_expires_at",
        )
    }
}
