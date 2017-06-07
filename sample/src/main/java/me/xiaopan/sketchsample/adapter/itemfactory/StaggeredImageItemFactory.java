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
import me.xiaopan.sketchsample.bean.BaiduImage;
import me.xiaopan.sketchsample.widget.SampleImageView;

public class StaggeredImageItemFactory extends AssemblyRecyclerItemFactory<StaggeredImageItemFactory.StaggeredImageItem> {
    private OnItemClickListener onItemClickListener;
    private int itemWidth;

    public StaggeredImageItemFactory(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public boolean isTarget(Object o) {
        return o instanceof BaiduImage;
    }

    @Override
    public StaggeredImageItem createAssemblyItem(ViewGroup viewGroup) {
        return new StaggeredImageItem(R.layout.list_item_image_staggered, viewGroup);
    }

    public interface OnItemClickListener {
        void onItemClick(int position, BaiduImage image, String loadingImageOptionsInfo);
    }

    public class StaggeredImageItem extends BindAssemblyRecyclerItem<BaiduImage> {
        @BindView(R.id.image_staggeredImageItem)
        SampleImageView imageView;

        public StaggeredImageItem(int itemLayoutId, ViewGroup parent) {
            super(itemLayoutId, parent);
        }

        @Override
        protected void onConfigViews(Context context) {
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(getAdapterPosition(), getData(), imageView.getOptionsKey());
                    }
                }
            });
            imageView.setOptions(ImageOptions.RECT);

            imageView.setPage(SampleImageView.Page.SEARCH_LIST);
        }

        @Override
        protected void onSetData(int i, BaiduImage image) {
            if (itemWidth == 0) {
                int screenWidth = imageView.getContext().getResources().getDisplayMetrics().widthPixels;
                itemWidth = (screenWidth - (SketchUtils.dp2px(imageView.getContext(), 4) * 3)) / 2;
            }

            ViewGroup.LayoutParams headParams = imageView.getLayoutParams();
            headParams.width = itemWidth;
            headParams.height = (int) (itemWidth / (image.getWidth() / (float) image.getHeight()));
            imageView.setLayoutParams(headParams);

            imageView.displayImage(image.getSourceUrl());
        }
    }
}
