package us.mikeandwan.photos.ui.photos;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import us.mikeandwan.photos.R;
import us.mikeandwan.photos.di.ActivityComponent;
import us.mikeandwan.photos.models.Comment;
import us.mikeandwan.photos.models.CommentPhoto;
import us.mikeandwan.photos.services.DataServices;


public class CommentDialogFragment extends BasePhotoDialogFragment {
    private final CompositeDisposable _disposables = new CompositeDisposable();
    private Unbinder _unbinder;

    @BindView(R.id.commentTableLayout) TableLayout _commentTableLayout;
    @BindView(R.id.addCommentButton) Button _addCommentButton;
    @BindView(R.id.commentEditText) EditText _commentEditText;
    @BindView(R.id.commentScrollView) ScrollView _commentScrollView;

    @BindColor(R.color.primary) int _colorPrimary;
    @BindColor(R.color.primary_dark) int _colorPrimaryDark;

    @Inject DataServices _dataServices;


    @OnClick(R.id.addCommentButton)
    void onAddCommentClick() {
        String comment = _commentEditText.getText().toString();

        if (!TextUtils.isEmpty(comment)) {
            CommentPhoto cp = new CommentPhoto();
            cp.setPhotoId(getCurrentPhoto().getId());
            cp.setComment(comment);

            _disposables.add(Flowable.fromCallable(() -> {
                        addWork();
                        return _dataServices.addComment(cp);
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            x -> {
                                removeWork();
                                _commentEditText.setText("");
                                displayComments(x.getItems());
                            },
                            ex -> {
                                removeWork();
                                getPhotoActivity().onApiException(ex);
                            }
                    )
            );
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //View view = inflater.inflate(R.layout.dialog_comment, container, false);
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_comment, container, false);
        _unbinder = ButterKnife.bind(this, view);

        getDialog().setTitle("Comments");

        _commentEditText.requestFocus();

        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.getComponent(ActivityComponent.class).inject(this);
    }


    @Override
    public void onResume() {
        // http://stackoverflow.com/a/24213921
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

        getComments();

        super.onResume();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        _disposables.clear(); // do not send event after activity has been destroyed
        _unbinder.unbind();
    }


    private void getComments() {
        _disposables.add(Flowable.fromCallable(() -> {
                    addWork();
                    return _dataServices.getComments(getCurrentPhoto().getId());
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        x -> {
                            removeWork();
                            displayComments(x.getItems());
                        },
                        ex -> {
                            removeWork();
                            getPhotoActivity().onApiException(ex);
                        }
                )
        );
    }


    private void displayComments(List<Comment> comments) {
        _commentTableLayout.removeAllViews();
        _commentScrollView.scrollTo(0, 0);

        DateFormat df = new SimpleDateFormat("MMM d, yyyy", Locale.US);

        // go backwards through the comments, as they appear to be sorted in chronological order, and
        // probably better to show newest first here...
        for (int i = comments.size() - 1; i >= 0; i--) {
            Comment comment = comments.get(i);

            Context ctx = getActivity();

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
    }
}
