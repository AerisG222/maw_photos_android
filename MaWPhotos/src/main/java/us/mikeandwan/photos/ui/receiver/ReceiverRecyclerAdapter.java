package us.mikeandwan.photos.ui.receiver;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.VideoView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.apache.commons.io.FilenameUtils;

import java.io.File;

import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.R;
import us.mikeandwan.photos.services.DataServices;


public class ReceiverRecyclerAdapter extends RecyclerView.Adapter {
    private File[] _queuedFiles;
    private final Activity _activity;
    private final MimeTypeMap _mimeMap = MimeTypeMap.getSingleton();
    private int _itemSize = 120;

    public ReceiverRecyclerAdapter(Activity activity) {
        _activity = activity;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View receiverView = LayoutInflater.from(parent.getContext()).inflate(R.layout.receiver_list_item, parent, false);

        return new ViewHolder(receiverView);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        File file = _queuedFiles[position];
        String type = _mimeMap.getMimeTypeFromExtension(FilenameUtils.getExtension(file.getName()));
        ViewHolder vh = (ViewHolder) holder;

        vh._layout.setMaxHeight(_itemSize);
        vh._layout.setMaxWidth(_itemSize);

        if(type.startsWith("image")) {
            vh._videoView.setVisibility(View.GONE);
            vh._imageView.setVisibility(View.VISIBLE);

            Picasso
                .with(_activity)
                .load(file)
                .resize(_itemSize, _itemSize)
                //.resizeDimen(R.dimen.category_grid_thumbnail_size, R.dimen.category_grid_thumbnail_size)
                .centerCrop()
                .into(vh._imageView);
        } else if(type.startsWith("video")) {
            vh._videoView.setVisibility(View.VISIBLE);
            vh._imageView.setVisibility(View.GONE);

            vh._videoView.setVideoPath(file.getAbsolutePath());
        }
    }


    @Override
    public int getItemCount() {
        return _queuedFiles == null ? 0 : _queuedFiles.length;
    }


    public void setItemSize(int size) {
        _itemSize = size;
    }


    public void setQueuedFiles(File[] files) {
        _queuedFiles = files;

        notifyDataSetChanged();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        final ConstraintLayout _layout;
        final ImageView _imageView;
        final VideoView _videoView;


        ViewHolder(View itemView) {
            super(itemView);

            _layout = (ConstraintLayout) itemView;
            _imageView = itemView.findViewById(R.id.receiverListImageView);
            _videoView = itemView.findViewById(R.id.receiverListVideoView);
        }
    }
}
