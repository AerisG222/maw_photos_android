package us.mikeandwan.photos.authorization

import com.auth0.android.NetworkErrorException
import com.auth0.android.authentication.AuthenticationAPIClient
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.authentication.storage.Storage
import com.auth0.android.callback.Callback
import com.auth0.android.request.Request
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.io.IOException
import kotlinx.coroutines.test.runTest
import org.junit.Test

class LegacyCredentialPurgeTest {
    private val apiClient = mockk<AuthenticationAPIClient>()

    @Test
    fun `does nothing when the old manager never wrote anything`() = runTest {
        val storage = storageWith()

        LegacyCredentialPurge(storage, apiClient).run()

        verify(exactly = 0) { storage.remove(any()) }
        verify(exactly = 0) { apiClient.revokeToken(any()) }
    }

    @Test
    fun `revokes the refresh token before deleting it`() = runTest {
        val storage = storageWith(KEY_REFRESH_TOKEN to REFRESH_TOKEN, KEY_ACCESS_TOKEN to "at")
        givenRevocationSucceeds()

        LegacyCredentialPurge(storage, apiClient).run()

        coVerify(exactly = 1) { apiClient.revokeToken(REFRESH_TOKEN) }
        verifyPurged(storage)
    }

    @Test
    fun `keeps the tokens when the network is what stopped the revocation`() = runTest {
        val storage = storageWith(KEY_REFRESH_TOKEN to REFRESH_TOKEN)
        val unreachable = NetworkErrorException(IOException("no route to host"))

        givenRevocationFails(AuthenticationException("could not connect", unreachable))

        LegacyCredentialPurge(storage, apiClient).run()

        // deleting them here would leave a live refresh token nobody can ever revoke
        verify(exactly = 0) { storage.remove(any()) }
    }

    @Test
    fun `deletes the tokens when auth0 turns the revocation down`() = runTest {
        val storage = storageWith(KEY_REFRESH_TOKEN to REFRESH_TOKEN)
        givenRevocationFails(AuthenticationException("invalid_grant", "unknown token"))

        LegacyCredentialPurge(storage, apiClient).run()

        verifyPurged(storage)
    }

    @Test
    fun `deletes what is left when there is no refresh token to revoke`() = runTest {
        val storage = storageWith(KEY_ACCESS_TOKEN to "at")

        LegacyCredentialPurge(storage, apiClient).run()

        verify(exactly = 0) { apiClient.revokeToken(any()) }
        verifyPurged(storage)
    }

    private fun storageWith(vararg entries: Pair<String, String>): Storage {
        val values = entries.toMap()

        return mockk<Storage>(relaxed = true).also { storage ->
            every { storage.retrieveString(any()) } answers { values[firstArg()] }
        }
    }

    private fun givenRevocationSucceeds() {
        every { apiClient.revokeToken(REFRESH_TOKEN) } returns RevocationRequest(null)
    }

    private fun givenRevocationFails(error: AuthenticationException) {
        every { apiClient.revokeToken(REFRESH_TOKEN) } returns RevocationRequest(error)
    }

    // Request.await is @JvmSynthetic, which mockk's proxies do not override, so the revocation call
    // is stood up by hand rather than stubbed
    private class RevocationRequest(
        private val error: AuthenticationException?,
    ) : Request<Void?, AuthenticationException> {
        override suspend fun await(): Void? = error?.let { throw it }

        override fun start(callback: Callback<Void?, AuthenticationException>) = throw notUsed()

        override fun execute(): Void? = throw notUsed()

        override fun addParameters(parameters: Map<String, String>) = this

        override fun addParameter(name: String, value: String) = this

        override fun addHeader(name: String, value: String) = this

        private fun notUsed() = UnsupportedOperationException("the purge only ever awaits")
    }

    private fun verifyPurged(storage: Storage) {
        PURGED_KEYS.forEach { key ->
            verify(exactly = 1) { storage.remove(key) }
        }
    }

    companion object {
        private const val REFRESH_TOKEN = "the-leaked-refresh-token"
        private const val KEY_ACCESS_TOKEN = "com.auth0.access_token"
        private const val KEY_REFRESH_TOKEN = "com.auth0.refresh_token"

        private val PURGED_KEYS = listOf(
            KEY_ACCESS_TOKEN,
            KEY_REFRESH_TOKEN,
            "com.auth0.id_token",
            "com.auth0.expires_at",
            "com.auth0.scope",
            "com.auth0.cache_expires_at",
        )
    }
}
