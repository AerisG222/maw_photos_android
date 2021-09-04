package us.mikeandwan.photos.ui.photos

import android.content.Context
import dagger.hilt.android.AndroidEntryPoint
import us.mikeandwan.photos.ui.photos.BasePhotoDialogFragment
import io.reactivex.disposables.CompositeDisposable
import butterknife.Unbinder
import butterknife.BindView
import us.mikeandwan.photos.R
import butterknife.BindColor
import javax.inject.Inject
import us.mikeandwan.photos.services.DataServices
import butterknife.OnClick
import android.text.TextUtils
import us.mikeandwan.photos.models.CommentPhoto
import io.reactivex.Flowable
import us.mikeandwan.photos.models.ApiCollection
import io.reactivex.schedulers.Schedulers
import io.reactivex.android.schedulers.AndroidSchedulers
import android.os.Bundle
import butterknife.ButterKnife
import android.graphics.Typeface
import android.view.*
import android.widget.*
import us.mikeandwan.photos.models.Comment
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class CommentDialogFragment : BasePhotoDialogFragment() {
    private val _disposables = CompositeDisposable()
    private var _unbinder: Unbinder? = null

    @JvmField
    @BindView(R.id.commentTableLayout)
    var _commentTableLayout: TableLayout? = null

    @JvmField
    @BindView(R.id.addCommentButton)
    var _addCommentButton: Button? = null

    @JvmField
    @BindView(R.id.commentEditText)
    var _commentEditText: EditText? = null

    @JvmField
    @BindView(R.id.commentScrollView)
    var _commentScrollView: ScrollView? = null

    @JvmField
    @BindColor(R.color.primary)
    var _colorPrimary = 0

    @JvmField
    @BindColor(R.color.primary_dark)
    var _colorPrimaryDark = 0

    @Inject lateinit var _dataServices: DataServices

    @OnClick(R.id.addCommentButton)
    fun onAddCommentClick() {
        val comment = _commentEditText!!.text.toString()
        if (!TextUtils.isEmpty(comment)) {
            val cp = CommentPhoto()
            cp.photoId = currentPhoto.id
            cp.comment = comment
            _disposables.add(Flowable.fromCallable {
                addWork()
                _dataServices!!.addComment(cp)
            }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { x: ApiCollection<Comment> ->
                        removeWork()
                        _commentEditText!!.setText("")
                        displayComments(x.items)
                    }
                ) { ex: Throwable? ->
                    removeWork()
                    photoActivity.onApiException(ex)
                }
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle
    ): View? {
        //View view = inflater.inflate(R.layout.dialog_comment, container, false);
        val view = activity.layoutInflater.inflate(R.layout.dialog_comment, container, false)
        _unbinder = ButterKnife.bind(this, view)
        dialog.setTitle("Comments")
        _commentEditText!!.requestFocus()
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onResume() {
        // http://stackoverflow.com/a/24213921
        val params = dialog.window!!.attributes
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog.window!!.attributes = params
        comments
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        _disposables.clear() // do not send event after activity has been destroyed
        _unbinder!!.unbind()
    }

    private val comments: Unit
        private get() {
            _disposables.add(Flowable.fromCallable {
                addWork()
                _dataServices!!.getComments(currentPhoto.id)
            }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { x: ApiCollection<Comment> ->
                        removeWork()
                        displayComments(x.items)
                    }
                ) { ex: Throwable? ->
                    removeWork()
                    photoActivity.onApiException(ex)
                }
            )
        }

    private fun displayComments(comments: List<Comment>) {
        _commentTableLayout!!.removeAllViews()
        _commentScrollView!!.scrollTo(0, 0)
        val df: DateFormat = SimpleDateFormat("MMM d, yyyy", Locale.US)

        // go backwards through the comments, as they appear to be sorted in chronological order, and
        // probably better to show newest first here...
        for (i in comments.indices.reversed()) {
            val comment = comments[i]
            val ctx: Context = activity
            val titleRow = TableRow(ctx)
            val commentRow = TableRow(ctx)
            if (i % 2 == 1) {
                titleRow.setBackgroundColor(_colorPrimaryDark)
                commentRow.setBackgroundColor(_colorPrimaryDark)
            } else {
                titleRow.setBackgroundColor(_colorPrimary)
                commentRow.setBackgroundColor(_colorPrimary)
            }
            _commentTableLayout!!.addView(titleRow)
            _commentTableLayout!!.addView(commentRow)
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