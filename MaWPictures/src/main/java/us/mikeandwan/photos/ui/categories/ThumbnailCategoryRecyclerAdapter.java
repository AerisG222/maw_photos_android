package us.mikeandwan.photos.ui.categories;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import us.mikeandwan.photos.R;
import us.mikeandwan.photos.models.Category;
import us.mikeandwan.photos.services.AuthenticationExceptionHandler;
import us.mikeandwan.photos.services.PhotoStorage;
import us.mikeandwan.photos.tasks.DownloadCategoryTeaserTask;


public class ThumbnailCategoryRecyclerAdapter extends CategoryRecyclerAdapter<ThumbnailCategoryRecyclerAdapter.ViewHolder> {
    public ThumbnailCategoryRecyclerAdapter(Context context,
                                            PhotoStorage photoStorage,
                                            DownloadCategoryTeaserTask downloadTeaserTask,
                                            AuthenticationExceptionHandler authHandler) {
        super(context, photoStorage, downloadTeaserTask, authHandler);
    }


    @Override
    public ThumbnailCategoryRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ImageView imageView = new ImageView(parent.getContext());
        imageView.setPadding(4, 4, 4, 4);
        imageView.setAdjustViewBounds(true);

        return new ViewHolder(imageView);
    }


    protected void displayCategory(Category category, ThumbnailCategoryRecyclerAdapter.ViewHolder viewHolder) {
        String file = "file://" + _photoStorage.getCachePath(category.getTeaserPhotoInfo().getPath());

        Picasso
                .with(_context)
                .load(file)
                .resizeDimen(R.dimen.category_grid_thumbnail_size, R.dimen.category_grid_thumbnail_size)
                .centerCrop()
                .into(viewHolder._thumbnailImageView);
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView _thumbnailImageView;


        ViewHolder(View itemView) {
            super(itemView);

            _thumbnailImageView = (ImageView) itemView;
        }
    }
}
