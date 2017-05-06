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
import me.xiaopan.sketch.request.DisplayOptions;
import me.xiaopan.sketch.shaper.ImageShaper;
import me.xiaopan.sketch.shaper.RoundRectImageShaper;
import me.xiaopan.sketch.util.SketchUtils;
import me.xiaopan.sketchsample.ImageOptions;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.adapter.BindAssemblyRecyclerItem;
import me.xiaopan.sketchsample.util.Settings;
import me.xiaopan.sketchsample.widget.MyImageView;

public class PhotoAlbumItemFactory extends AssemblyRecyclerItemFactory<PhotoAlbumItemFactory.PhotoAlbumItem> {

    private OnImageClickListener onImageClickListener;
    private int itemSize;

    public PhotoAlbumItemFactory(OnImageClickListener onImageClickListener) {
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

        return new PhotoAlbumItem(R.layout.list_item_photo_album_image, viewGroup);
    }

    public interface OnImageClickListener {
        void onImageClick(int position, String loadingImageOptionsId);
    }

    public class PhotoAlbumItem extends BindAssemblyRecyclerItem<String> {
        @BindView(R.id.image_photoAlbumImageItem)
        MyImageView sketchImageView;

        public PhotoAlbumItem(int itemLayoutId, ViewGroup parent) {
            super(itemLayoutId, parent);
        }

        @Override
        protected void onConfigViews(Context context) {
            sketchImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onImageClickListener != null) {
                        onImageClickListener.onImageClick(getAdapterPosition(), getData());
                    }
                }
            });
            sketchImageView.setOptionsByName(ImageOptions.RECT);

            ImageShaper imageShaper = sketchImageView.getOptions().getImageShaper();
            if (imageShaper != null && imageShaper instanceof RoundRectImageShaper) {
                RoundRectImageShaper roundRectImageShaper = (RoundRectImageShaper) imageShaper;
                sketchImageView.setImageShape(SketchImageView.ImageShape.ROUNDED_RECT);
                sketchImageView.setImageShapeCornerRadius(roundRectImageShaper.getOuterRadii());
            }

            if (itemSize > 0) {
                ViewGroup.LayoutParams layoutParams = sketchImageView.getLayoutParams();
                layoutParams.width = itemSize;
                layoutParams.height = itemSize;
                sketchImageView.setLayoutParams(layoutParams);
            }
        }

        @Override
        protected void onSetData(int i, String imageUri) {
            boolean thumbnailMode = Settings.getBoolean(sketchImageView.getContext(), Settings.PREFERENCE_THUMBNAIL_MODE);
            DisplayOptions options = sketchImageView.getOptions();
            options.setThumbnailMode(thumbnailMode);
            options.setCacheProcessedImageInDisk(Settings.getBoolean(sketchImageView.getContext(), Settings.PREFERENCE_CACHE_PROCESSED_IMAGE));
            if (thumbnailMode) {
                if (options.getResize() == null && !options.isResizeByFixedSize()) {
                    options.setResizeByFixedSize(true);
                }
            } else {
                options.setResizeByFixedSize(false);
            }

            boolean playGifOnList = Settings.getBoolean(sketchImageView.getContext(), Settings.PREFERENCE_PLAY_GIF_ON_LIST);
            if (playGifOnList != options.isDecodeGifImage()) {
                options.setDecodeGifImage(playGifOnList);
            }

            sketchImageView.displayImage(imageUri);
        }
    }
}
