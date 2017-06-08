package me.xiaopan.sketchsample.adapter.itemfactory;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import me.xiaopan.assemblyadapter.AssemblyRecyclerItemFactory;
import me.xiaopan.sketch.SketchImageView;
import me.xiaopan.sketch.shaper.ImageShaper;
import me.xiaopan.sketch.shaper.RoundRectImageShaper;
import me.xiaopan.sketch.util.SketchUtils;
import me.xiaopan.sketchsample.ImageOptions;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.adapter.BindAssemblyRecyclerItem;
import me.xiaopan.sketchsample.widget.SampleImageView;

public class MyPhotoItemFactory extends AssemblyRecyclerItemFactory<MyPhotoItemFactory.PhotoAlbumItem> {

    private OnImageClickListener onImageClickListener;
    private int itemSize;

    public MyPhotoItemFactory(OnImageClickListener onImageClickListener) {
        this.onImageClickListener = onImageClickListener;
    }

    @Override
    public boolean isTarget(Object o) {
        return o instanceof String;
    }

    @Override
    public PhotoAlbumItem createAssemblyItem(ViewGroup viewGroup) {
        if (itemSize == 0) {
            itemSize = -1;
            if (viewGroup instanceof RecyclerView) {
                int spanCount = 1;
                RecyclerView recyclerView = (RecyclerView) viewGroup;
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                if (layoutManager instanceof GridLayoutManager) {
                    spanCount = ((GridLayoutManager) recyclerView.getLayoutManager()).getSpanCount();
                } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                    spanCount = ((StaggeredGridLayoutManager) recyclerView.getLayoutManager()).getSpanCount();
                }
                if (spanCount > 1) {
                    int screenWidth = viewGroup.getResources().getDisplayMetrics().widthPixels;
                    itemSize = (screenWidth - (SketchUtils.dp2px(viewGroup.getContext(), 4) * (spanCount + 1))) / spanCount;
                }
            }
        }

        return new PhotoAlbumItem(R.layout.list_item_my_photo, viewGroup);
    }

    public interface OnImageClickListener {
        void onClickImage(int position, String optionsKey);
    }

    public class PhotoAlbumItem extends BindAssemblyRecyclerItem<String> {
        @BindView(R.id.image_myPhotoItem)
        SampleImageView imageView;

        public PhotoAlbumItem(int itemLayoutId, ViewGroup parent) {
            super(itemLayoutId, parent);
        }

        @Override
        protected void onConfigViews(Context context) {
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onImageClickListener != null) {
                        onImageClickListener.onClickImage(getAdapterPosition(), getData());
                    }
                }
            });
            imageView.setOptions(ImageOptions.RECT);

            ImageShaper imageShaper = imageView.getOptions().getImageShaper();
            if (imageShaper != null && imageShaper instanceof RoundRectImageShaper) {
                RoundRectImageShaper roundRectImageShaper = (RoundRectImageShaper) imageShaper;
                imageView.setImageShape(SketchImageView.ImageShape.ROUNDED_RECT);
                imageView.setImageShapeCornerRadius(roundRectImageShaper.getOuterRadii());
            }

            if (itemSize > 0) {
                ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
                layoutParams.width = itemSize;
                layoutParams.height = itemSize;
                imageView.setLayoutParams(layoutParams);
            }

            imageView.setPage(SampleImageView.Page.PHOTO_LIST);
        }

        @Override
        protected void onSetData(int i, String imageUri) {
            imageView.displayImage(imageUri);
        }
    }
}
