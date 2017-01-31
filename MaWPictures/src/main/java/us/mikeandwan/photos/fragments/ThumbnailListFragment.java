package us.mikeandwan.photos.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import us.mikeandwan.photos.R;
import us.mikeandwan.photos.di.TaskComponent;
import us.mikeandwan.photos.models.Photo;
import us.mikeandwan.photos.models.ui.ThumbnailRecyclerAdapter;
import us.mikeandwan.photos.services.PhotoStorage;
import us.mikeandwan.photos.tasks.DownloadPhotoTask;


public class ThumbnailListFragment extends BasePhotoFragment {
    private int _thumbnailIndex;
    private ThumbnailRecyclerAdapter _adapter;
    private final List<Photo> _photoList = new ArrayList<>();
    private Unbinder _unbinder;

    @BindView(R.id.imageRecycler) RecyclerView _thumbnailRecyclerView;

    @Inject Activity _activity;
    @Inject PhotoStorage _photoStorage;
    @Inject DownloadPhotoTask _downloadPhotoTask;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_thumbnail_list, container, false);
        _unbinder = ButterKnife.bind(this, view);

        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.getComponent(TaskComponent.class).inject(this);

        LinearLayoutManager llm = new LinearLayoutManager(_activity);
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        _thumbnailRecyclerView.setLayoutManager(llm);

        _adapter = new ThumbnailRecyclerAdapter(_activity, _photoStorage, _downloadPhotoTask, _photoList);
        _thumbnailRecyclerView.setAdapter(_adapter);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        if(_adapter != null) {
            _adapter.dispose();
        }

        _unbinder.unbind();
    }


    public HorizontalScrollView getThumbnailScrollView() {
        return null; /*_horizontalScrollView;*/
    }


    public void onCurrentPhotoUpdated() {
        // if the change came outside of this fragment, then make sure we move to the current
        // thumbnail.  otherwise, don't change the view as that is unexpected to user
        if (_thumbnailIndex != getPhotoActivity().getCurrentIndex()) {
            Photo thumb = _photoList.get(getPhotoActivity().getCurrentIndex());

            // TODO: scroll to current photo
            /*
            _horizontalScrollView.smoothScrollTo(thumb.getLeft(), 0);

            // TODO: consider adding generic version to a function to the photolistactivity
            // we force the animation here to leave the alpha at 0.2, otherwise was resetting to 1.0
            AlphaAnimation alpha = new AlphaAnimation(PhotoListActivity.FADE_END_ALPHA, PhotoListActivity.FADE_END_ALPHA);
            alpha.setDuration(PhotoListActivity.FADE_DURATION);
            alpha.setFillAfter(true);

            _horizontalScrollView.startAnimation(alpha);
            */
        }
    }


    public void addPhotoList(List<Photo> photoList) {
        synchronized (_photoList) {
            for (Photo photo : photoList) {
                addPhoto(photo);
            }
        }
    }


    public void addPhoto(Photo photo)
    {
        synchronized (_photoList) {
            _photoList.add(photo);
            _thumbnailIndex = _photoList.size() - 1;
        }

        // TODO: goto new thumb
        //displayPhotoThumbnail(photo, _thumbnailIndex);
    }
}
