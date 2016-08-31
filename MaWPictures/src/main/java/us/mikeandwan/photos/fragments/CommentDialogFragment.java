package us.mikeandwan.photos.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.ColorRes;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.R;
import us.mikeandwan.photos.activities.LoginActivity_;
import us.mikeandwan.photos.data.Comment;
import us.mikeandwan.photos.data.CommentPhoto;
import us.mikeandwan.photos.services.MawAuthenticationException;
import us.mikeandwan.photos.tasks.AddCommentBackgroundTask;
import us.mikeandwan.photos.tasks.BackgroundTaskExecutor;
import us.mikeandwan.photos.tasks.GetCommentsBackgroundTask;


@SuppressWarnings("ALL")
@EFragment(R.layout.dialog_comment)
public class CommentDialogFragment extends BasePhotoDialogFragment {
    @ViewById(R.id.commentTableLayout)
    protected TableLayout _commentTableLayout;

    @ViewById(R.id.addCommentButton)
    protected Button _addCommentButton;

    @ViewById(R.id.commentEditText)
    protected EditText _commentEditText;

    @ViewById(R.id.commentScrollView)
    protected ScrollView _commentScrollView;

    @ColorRes(R.color.primary)
    protected int _colorPrimary;

    @ColorRes(R.color.primary_dark)
    protected int _colorPrimaryDark;


    @Click(R.id.addCommentButton)
    void onAddCommentClick() {
        String comment = _commentEditText.getText().toString();

        if (!TextUtils.isEmpty(comment)) {
            CommentPhoto cp = new CommentPhoto();
            cp.setPhotoId(getCurrentPhoto().getId());
            cp.setComment(comment);

            AddCommentBackgroundTask task = new AddCommentBackgroundTask(getContext(), cp) {
                @Override
                protected void postExecuteTask(List<Comment> comments) {
                    _commentEditText.setText("");
                    displayComments(comments);
                }

                @Override
                protected void handleException(ExecutionException ex) {
                    Log.e(MawApplication.LOG_TAG, "exception adding the comment: " + ex.getMessage());

                    if (ex.getCause() instanceof MawAuthenticationException) {
                        startActivity(new Intent(getContext(), LoginActivity_.class));
                    }
                }
            };

            BackgroundTaskExecutor.getInstance().enqueueTask(task);
            updateProgress();
        }
    }


    @AfterViews
    protected void afterViews() {
        getDialog().setTitle("Comments");

        _commentEditText.requestFocus();
    }


    @Override
    public void onResume() {
        getComments();

        super.onResume();
    }


    private void getComments() {
        GetCommentsBackgroundTask task = new GetCommentsBackgroundTask(getContext(), getCurrentPhoto().getId()) {
            @Override
            protected void postExecuteTask(List<Comment> comments) {
                displayComments(comments);
            }

            @Override
            protected void handleException(ExecutionException ex) {
                Log.e(MawApplication.LOG_TAG, "exception getting the comments: " + ex.getMessage());

                if (ex.getCause() instanceof MawAuthenticationException) {
                    startActivity(new Intent(getContext(), LoginActivity_.class));
                }
            }
        };

        BackgroundTaskExecutor.getInstance().enqueueTask(task);
        updateProgress();
    }


    private void displayComments(List<Comment> comments) {
        _commentTableLayout.removeAllViews();
        _commentScrollView.scrollTo(0, 0);

        DateFormat df = new SimpleDateFormat("MMM d, yyyy", Locale.US);

        // go backwards through the comments, as they appear to be sorted in chronological order, and
        // probably better to show newest first here...
        for (int i = comments.size() - 1; i >= 0; i--) {
            Comment comment = comments.get(i);

            Context ctx = getContext();

            TableRow titleRow = new TableRow(ctx);
            TableRow commentRow = new TableRow(ctx);

            if (i % 2 == 1) {
                titleRow.setBackgroundColor(_colorPrimaryDark);
                commentRow.setBackgroundColor(_colorPrimaryDark);
            } else {
                titleRow.setBackgroundColor(_colorPrimary);
                commentRow.setBackgroundColor(_colorPrimary);
            }

            _commentTableLayout.addView(titleRow);
            _commentTableLayout.addView(commentRow);

            TextView usernameView = new TextView(ctx);
            usernameView.setPadding(4, 4, 4, 4);
            usernameView.setTypeface(Typeface.DEFAULT_BOLD);
            usernameView.setText(comment.getUsername());

            TextView dateView = new TextView(ctx);
            dateView.setPadding(4, 4, 4, 4);
            dateView.setTypeface(Typeface.DEFAULT_BOLD);
            dateView.setText(df.format(comment.getEntryDate()));
            dateView.setGravity(Gravity.END);

            titleRow.addView(usernameView);
            titleRow.addView(dateView);

            TextView commentView = new TextView(ctx);
            commentView.setPadding(4, 4, 4, 4);
            commentView.setText(comment.getCommentText());
            commentRow.addView(commentView);
            ((TableRow.LayoutParams) commentView.getLayoutParams()).span = 2;
        }

        updateProgress();
    }
}
