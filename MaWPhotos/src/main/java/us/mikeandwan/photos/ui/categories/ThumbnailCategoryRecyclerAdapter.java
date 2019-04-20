package us.mikeandwan.photos.ui.categories;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import us.mikeandwan.photos.R;
import us.mikeandwan.photos.models.Category;
import us.mikeandwan.photos.services.DataServices;


public class ThumbnailCategoryRecyclerAdapter extends CategoryRecyclerAdapter<ThumbnailCategoryRecyclerAdapter.ViewHolder> {
    public ThumbnailCategoryRecyclerAdapter(ICategoryListActivity activity,
                                            DataServices dataServices) {
        super(activity, dataServices);
    }


    @NonNull
    @Override
    public ThumbnailCategoryRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ImageView imageView = new ImageView(parent.getContext());
        imageView.setPadding(4, 4, 4, 4);
        imageView.setAdjustViewBounds(true);

        return new ViewHolder(imageView);
    }


    @Override
    protected String downloadCategoryTeaser(Category category) {
        return _dataServices.downloadMdCategoryTeaser(category);
    }


    protected void displayCategory(Category category, String path, ThumbnailCategoryRecyclerAdapter.ViewHolder viewHolder) {
        Picasso
                .get()
                .load(path)
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
