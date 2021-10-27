package us.mikeandwan.photos.uiold.photos

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import us.mikeandwan.photos.R
import us.mikeandwan.photos.databinding.DialogCommentBinding
import us.mikeandwan.photos.models.ApiCollection
import us.mikeandwan.photos.models.Comment
import us.mikeandwan.photos.models.CommentPhoto
import us.mikeandwan.photos.services.DataServices
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class CommentDialogFragment : BasePhotoDialogFragment() {
    private val _disposables = CompositeDisposable()

    private var _binding: DialogCommentBinding? = null
    private val binding get() = _binding!!

    var _colorPrimary = 0
    var _colorPrimaryDark = 0

    @Inject lateinit var _dataServices: DataServices

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogCommentBinding.inflate(inflater, container, false)

        _colorPrimary = ContextCompat.getColor(context, R.color.black_800)
        _colorPrimaryDark = ContextCompat.getColor(context, R.color.black_900)
        requireDialog().setTitle("Comments")
        binding.commentEditText.requestFocus()

        binding.addCommentButton.setOnClickListener {
            onAddCommentClick()
        }

        return binding.root
    }

    override fun onResume() {
        // http://stackoverflow.com/a/24213921
        val params = requireDialog().window!!.attributes
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = ViewGroup.LayoutParams.MATCH_PARENT
        requireDialog().window!!.attributes = params
        comments
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        _disposables.clear() // do not send event after activity has been destroyed
    }

    fun onAddCommentClick() {
        val comment = binding.commentEditText.text.toString()

        if (!TextUtils.isEmpty(comment)) {
            val cp = CommentPhoto()
            cp.photoId = currentPhoto.id
            cp.comment = comment

            _disposables.add(Flowable.fromCallable {
                addWork()
                _dataServices.addComment(cp)
            }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { x: ApiCollection<Comment>? ->
                        removeWork()
                        binding.commentEditText.setText("")
                        displayComments(x!!.items)
                    }
                ) { ex: Throwable? ->
                    removeWork()
                    photoActivity.onApiException(ex)
                }
            )
        }
    }

    private val comments: Unit
        get() {
            _disposables.add(Flowable.fromCallable {
                addWork()
                _dataServices.getComments(currentPhoto.id)
            }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { x: ApiCollection<Comment>? ->
                        removeWork()
                        displayComments(x!!.items)
                    }
                ) { ex: Throwable? ->
                    removeWork()
                    photoActivity.onApiException(ex)
                }
            )
        }

    private fun displayComments(comments: List<Comment>) {
        binding.commentTableLayout.removeAllViews()
        binding.commentScrollView.scrollTo(0, 0)
        val df: DateFormat = SimpleDateFormat("MMM d, yyyy", Locale.US)

        // go backwards through the comments, as they appear to be sorted in chronological order, and
        // probably better to show newest first here...
        for (i in comments.indices.reversed()) {
            val comment = comments[i]
            val ctx: Context = requireActivity()
            val titleRow = TableRow(ctx)
            val commentRow = TableRow(ctx)
            if (i % 2 == 1) {
                titleRow.setBackgroundColor(_colorPrimaryDark)
                commentRow.setBackgroundColor(_colorPrimaryDark)
            } else {
                titleRow.setBackgroundColor(_colorPrimary)
                commentRow.setBackgroundColor(_colorPrimary)
            }
            binding.commentTableLayout.addView(titleRow)
            binding.commentTableLayout.addView(commentRow)
            val usernameView = TextView(ctx)
            usernameView.setPadding(4, 4, 4, 4)
            usernameView.typeface = Typeface.DEFAULT_BOLD
            usernameView.text = comment.username
            val dateView = TextView(ctx)
            dateView.setPadding(4, 4, 4, 4)
            dateView.typeface = Typeface.DEFAULT_BOLD
            dateView.text = df.format(comment.entryDate)
            dateView.gravity = Gravity.END
            titleRow.addView(usernameView)
            titleRow.addView(dateView)
            val commentView = TextView(ctx)
            commentView.setPadding(4, 4, 4, 4)
            commentView.text = comment.commentText
            commentRow.addView(commentView)
            (commentView.layoutParams as TableRow.LayoutParams).span = 2
        }
    }
}