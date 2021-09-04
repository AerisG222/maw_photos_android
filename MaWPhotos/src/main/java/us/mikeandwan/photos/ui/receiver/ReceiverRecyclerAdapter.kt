package us.mikeandwan.photos.ui.receiver;

import android.app.Activity;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.VideoView;

import com.squareup.picasso.Picasso;

import org.apache.commons.io.FilenameUtils;

import java.io.File;

import us.mikeandwan.photos.R;


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

        vh.setItemSize(_itemSize);

        if(type.startsWith("image")) {
            vh.setImage(file);
        } else if(type.startsWith("video")) {
            vh.setVideo(file);
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
        private final ImageView _cameraIcon;
        private final ImageView _videoIcon;
        private final ImageView _imageView;
        private final VideoView _videoView;
        private int _itemSize;


        ViewHolder(View itemView) {
            super(itemView);

            _layout = (ConstraintLayout) itemView;
            _imageView = itemView.findViewById(R.id.receiverListImageView);
            _videoView = itemView.findViewById(R.id.receiverListVideoView);
            _cameraIcon = itemView.findViewById(R.id.photoIcon);
            _videoIcon = itemView.findViewById(R.id.videoIcon);

            _videoView.setOnPreparedListener(mp -> {
                mp.setLooping(true);
                mp.setVolume(0, 0);
            });
        }


        public void setItemSize(int size) {

            _itemSize = size;

            _layout.setMaxHeight(_itemSize);
            _layout.setMaxWidth(_itemSize);
        }


        public void setVideo(File file) {
            _cameraIcon.setVisibility(View.GONE);
            _imageView.setVisibility(View.GONE);

            _videoView.setVisibility(View.VISIBLE);
            _videoIcon.setVisibility(View.VISIBLE);

            _videoView.setVideoURI(Uri.parse("file://" + file.getPath()));
            _videoView.start();
        }


        public void setImage(File file) {
            _videoIcon.setVisibility(View.GONE);
            _videoView.setVisibility(View.GONE);

            _imageView.setVisibility(View.VISIBLE);
            _cameraIcon.setVisibility(View.VISIBLE);

            Picasso
                    .get()
                    .load(file)
                    .resize(_itemSize, _itemSize)
                    //.resizeDimen(R.dimen.category_grid_thumbnail_size, R.dimen.category_grid_thumbnail_size)
                    .centerCrop()
                    .into(_imageView);
        }
    }
}
