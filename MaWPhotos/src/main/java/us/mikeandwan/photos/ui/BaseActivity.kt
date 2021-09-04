package us.mikeandwan.photos.ui

import android.R
import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import io.reactivex.ObservableSource
import androidx.core.view.ViewCompat
import timber.log.Timber
import net.openid.appauth.AuthorizationException
import com.google.android.material.snackbar.Snackbar
import io.reactivex.Observable
import java.net.ConnectException
import java.util.concurrent.TimeUnit

@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() {
    private val _disposables = CompositeDisposable()
    private val _errorSubject = PublishSubject.create<String>()
    open fun onApiException(throwable: Throwable?) {
        handleApiException(throwable)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _disposables.add(_errorSubject
            .publish { publishedItems: Observable<String> ->
                publishedItems
                    .take(1)
                    .concatWith(
                        publishedItems
                            .skip(1)
                            .debounce(2, TimeUnit.SECONDS)
                    )
            }
            .subscribe { msg: String -> showError(msg) })
    }

    override fun onDestroy() {
        _disposables.clear()
        super.onDestroy()
    }

    protected fun updateToolbar(toolbar: Toolbar?, title: String?) {
        if (toolbar != null) {
            setSupportActionBar(toolbar)
            ViewCompat.setElevation(toolbar, 4f)
            if (title != null) {
                toolbar.title = title
            }
        }
    }

    protected fun handleApiException(throwable: Throwable?) {
        if (throwable == null) {
            return
        }
        Timber.e("Error accessing api: %s", throwable.message)
        if (throwable is ConnectException) {
            _errorSubject.onNext("Unable to connect to service at this time.")
        } else if (throwable is AuthorizationException) {
            _errorSubject.onNext("Authorization failed.")
        }
    }

    private fun showError(msg: String) {
        val view = findViewById<View>(R.id.content)
        Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).show()
    }
}