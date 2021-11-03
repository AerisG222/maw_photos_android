package us.mikeandwan.photos.ui.screens.upload

import android.app.Activity
import android.media.MediaPlayer
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.VideoView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import org.apache.commons.io.FilenameUtils
import us.mikeandwan.photos.R
import java.io.File

class UploadRecyclerAdapter(private val _activity: Activity) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var _queuedFiles: Array<File>? = null
    private val _mimeMap = MimeTypeMap.getSingleton()
    private var _itemSize = 120
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val receiverView =
            LayoutInflater.from(parent.context).inflate(R.layout.receiver_list_item, parent, false)
        return ViewHolder(receiverView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val file = _queuedFiles!![position]
        val type = _mimeMap.getMimeTypeFromExtension(FilenameUtils.getExtension(file.name))
        val vh = holder as ViewHolder
        vh.setItemSize(_itemSize)
        if (type!!.startsWith("image")) {
            vh.setImage(file)
        } else if (type.startsWith("video")) {
            vh.setVideo(file)
        }
    }

    override fun getItemCount(): Int {
        return if (_queuedFiles == null) 0 else _queuedFiles!!.size
    }

    fun setItemSize(size: Int) {
        _itemSize = size
    }

    fun setQueuedFiles(files: Array<File>?) {
        _queuedFiles = files
        notifyDataSetChanged()
    }

    internal class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val _layout: ConstraintLayout
        private val _cameraIcon: ImageView
        private val _videoIcon: ImageView
        private val _imageView: ImageView
        private val _videoView: VideoView
        private var _itemSize = 0
        fun setItemSize(size: Int) {
            _itemSize = size
            _layout.maxHeight = _itemSize
            _layout.maxWidth = _itemSize
        }

        fun setVideo(file: File) {
            _cameraIcon.visibility = View.GONE
            _imageView.visibility = View.GONE
            _videoView.visibility = View.VISIBLE
            _videoIcon.visibility = View.VISIBLE
            _videoView.setVideoURI(Uri.parse("file://" + file.path))
            _videoView.start()
        }

        fun setImage(file: File?) {
            _videoIcon.visibility = View.GONE
            _videoView.visibility = View.GONE
            _imageView.visibility = View.VISIBLE
            _cameraIcon.visibility = View.VISIBLE
            Picasso
                .get()
                .load(file!!)
                .resize(
                    _itemSize,
                    _itemSize
                ) //.resizeDimen(R.dimen.category_grid_thumbnail_size, R.dimen.category_grid_thumbnail_size)
                .centerCrop()
                .into(_imageView)
        }

        init {
            _layout = itemView as ConstraintLayout
            _imageView = itemView.findViewById(R.id.receiverListImageView)
            _videoView = itemView.findViewById(R.id.receiverListVideoView)
            _cameraIcon = itemView.findViewById(R.id.photoIcon)
            _videoIcon = itemView.findViewById(R.id.videoIcon)
            _videoView.setOnPreparedListener { mp: MediaPlayer ->
                mp.isLooping = true
                mp.setVolume(0f, 0f)
            }
        }
    }
}