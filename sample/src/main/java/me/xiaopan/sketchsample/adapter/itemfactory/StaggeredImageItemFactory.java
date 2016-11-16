package me.xiaopan.sketchsample.adapter.itemfactory;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import me.xiaopan.assemblyadapter.AssemblyRecyclerItemFactory;
import me.xiaopan.sketch.util.SketchUtils;
import me.xiaopan.sketchsample.ImageOptions;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.adapter.BindAssemblyRecyclerItem;
import me.xiaopan.sketchsample.net.request.StarImageRequest;
import me.xiaopan.sketchsample.util.Settings;
import me.xiaopan.sketchsample.widget.MyImageView;

public class StaggeredImageItemFactory extends AssemblyRecyclerItemFactory<StaggeredImageItemFactory.StaggeredImageItem> {
    private OnItemClickListener onItemClickListener;
    private int itemWidth;

    public StaggeredImageItemFactory(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public boolean isTarget(Object o) {
        return o instanceof StarImageRequest.Image;
    }

    @Override
    public StaggeredImageItem createAssemblyItem(ViewGroup viewGroup) {
        return new StaggeredImageItem(R.layout.list_item_image, viewGroup);
    }

    public interface OnItemClickListener {
        void onItemClick(int position, StarImageRequest.Image image, String loadingImageOptionsInfo);
    }

    public class StaggeredImageItem extends BindAssemblyRecyclerItem<StarImageRequest.Image> {
        @BindView(R.id.image_imageItem)
        MyImageView imageView;

        public StaggeredImageItem(int itemLayoutId, ViewGroup parent) {
            super(itemLayoutId, parent);
        }

        @Override
        protected void onConfigViews(Context context) {
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(getAdapterPosition(), getData(), imageView.getOptionsId());
                    }
                }
            });
            imageView.setOptionsByName(ImageOptions.RECT);
        }

        @Override
        protected void onSetData(int i, StarImageRequest.Image image) {
            if (itemWidth == 0) {
                int screenWidth = imageView.getContext().getResources().getDisplayMetrics().widthPixels;
                itemWidth = (screenWidth - (SketchUtils.dp2px(imageView.getContext(), 8) * 3)) / 2;
            }

            ViewGroup.LayoutParams headParams = imageView.getLayoutParams();
            headParams.width = itemWidth;
            headParams.height = (int) (itemWidth / (image.getWidth() / (float) image.getHeight()));
            imageView.setLayoutParams(headParams);

            boolean playGifOnList = Settings.getBoolean(imageView.getContext(), Settings.PREFERENCE_PLAY_GIF_ON_LIST);
            if (playGifOnList != imageView.getOptions().isDecodeGifImage()) {
                imageView.getOptions().setDecodeGifImage(playGifOnList);
            }

            imageView.displayImage(image.getSourceUrl());
        }
    }
}
