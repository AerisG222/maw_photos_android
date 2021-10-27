package us.mikeandwan.photos.uiold

import android.R.id.content
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import net.openid.appauth.AuthorizationException
import timber.log.Timber
import java.net.ConnectException
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
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
        val view = findViewById<View>(content)
        Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).show()
    }
}