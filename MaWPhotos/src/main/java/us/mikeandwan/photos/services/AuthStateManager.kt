package us.mikeandwan.photos.services

import retrofit2.http.GET
import us.mikeandwan.photos.models.ApiCollection
import us.mikeandwan.photos.models.ExifData
import retrofit2.http.PATCH
import us.mikeandwan.photos.models.RatePhoto
import retrofit2.http.POST
import us.mikeandwan.photos.models.CommentPhoto
import retrofit2.http.Multipart
import okhttp3.MultipartBody
import us.mikeandwan.photos.models.FileOperationResult
import us.mikeandwan.photos.services.DatabaseAccessor
import us.mikeandwan.photos.services.PhotoApiClient
import us.mikeandwan.photos.services.PhotoStorage
import io.reactivex.subjects.BehaviorSubject
import kotlin.Throws
import timber.log.Timber
import us.mikeandwan.photos.models.PhotoSize
import us.mikeandwan.photos.services.PhotoListType
import android.text.TextUtils
import okhttp3.ResponseBody
import javax.inject.Inject
import android.webkit.MimeTypeMap
import android.os.Environment
import com.commonsware.cwac.provider.StreamProvider
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import us.mikeandwan.photos.services.PhotoApi
import us.mikeandwan.photos.models.ApiResult
import org.apache.commons.io.FilenameUtils
import okhttp3.RequestBody
import android.content.SharedPreferences
import net.openid.appauth.AuthState
import androidx.annotation.AnyThread
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationException
import net.openid.appauth.TokenResponse
import net.openid.appauth.RegistrationResponse
import us.mikeandwan.photos.services.AuthStateManager
import org.json.JSONException
import android.app.Application
import android.app.job.JobScheduler
import android.app.job.JobInfo
import us.mikeandwan.photos.services.MawSQLiteOpenHelper
import android.database.sqlite.SQLiteDatabase
import android.content.ContentValues
import us.mikeandwan.photos.models.MultimediaAsset
import us.mikeandwan.photos.services.BaseJobScheduler
import us.mikeandwan.photos.MawApplication
import android.content.ComponentName
import android.content.Context
import us.mikeandwan.photos.services.UploadJobService
import android.database.sqlite.SQLiteOpenHelper
import us.mikeandwan.photos.services.UpdateCategoriesJobService
import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.locks.ReentrantLock

/*
 * Copyright 2017 The AppAuth for Android Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
// package net.openid.appauthdemo;
/**
 * An example persistence mechanism for an [AuthState] instance.
 * This stores the instance in a shared preferences file, and provides thread-safe access and
 * mutation.
 */
class AuthStateManager private constructor(context: Context) {
    private val mPrefs: SharedPreferences
    private val mPrefsLock: ReentrantLock
    private val mCurrentAuthState: AtomicReference<AuthState>

    @get:AnyThread
    val current: AuthState
        get() {
            if (mCurrentAuthState.get() != null) {
                return mCurrentAuthState.get()
            }
            val state = readState()
            return if (mCurrentAuthState.compareAndSet(null, state)) {
                state
            } else {
                mCurrentAuthState.get()
            }
        }

    @AnyThread
    fun replace(state: AuthState): AuthState {
        writeState(state)
        mCurrentAuthState.set(state)
        return state
    }

    @AnyThread
    fun updateAfterAuthorization(
        response: AuthorizationResponse?,
        ex: AuthorizationException?
    ): AuthState {
        val current = current
        current.update(response, ex)
        return replace(current)
    }

    @AnyThread
    fun updateAfterTokenResponse(
        response: TokenResponse?,
        ex: AuthorizationException?
    ): AuthState {
        val current = current
        current.update(response, ex)
        return replace(current)
    }

    @AnyThread
    fun updateAfterRegistration(
        response: RegistrationResponse?,
        ex: AuthorizationException?
    ): AuthState {
        val current = current
        if (ex != null) {
            return current
        }
        current.update(response)
        return replace(current)
    }

    @AnyThread
    private fun readState(): AuthState {
        mPrefsLock.lock()
        return try {
            val currentState = mPrefs.getString(KEY_STATE, null)
                ?: return AuthState()
            try {
                AuthState.jsonDeserialize(currentState)
            } catch (ex: JSONException) {
                Timber.w("Failed to deserialize stored auth state - discarding")
                AuthState()
            }
        } finally {
            mPrefsLock.unlock()
        }
    }

    @AnyThread
    private fun writeState(state: AuthState?) {
        mPrefsLock.lock()
        try {
            val editor = mPrefs.edit()
            if (state == null) {
                editor.remove(KEY_STATE)
            } else {
                editor.putString(KEY_STATE, state.jsonSerializeString())
            }
            check(editor.commit()) { "Failed to write state to shared prefs" }
        } finally {
            mPrefsLock.unlock()
        }
    }

    companion object {
        private val INSTANCE_REF = AtomicReference(WeakReference<AuthStateManager?>(null))
        private const val TAG = "AuthStateManager"
        private const val STORE_NAME = "AuthState"
        private const val KEY_STATE = "state"
        @AnyThread
        fun getInstance(context: Context): AuthStateManager {
            var manager = INSTANCE_REF.get().get()
            if (manager == null) {
                manager = AuthStateManager(context.applicationContext)
                INSTANCE_REF.set(WeakReference(manager))
            }
            return manager
        }
    }

    init {
        mPrefs = context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE)
        mPrefsLock = ReentrantLock()
        mCurrentAuthState = AtomicReference()
    }
}