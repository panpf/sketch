package me.xiaopan.sketchsample.adapter.itemfactory;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import me.xiaopan.assemblyadapter.AssemblyRecyclerItemFactory;
import me.xiaopan.sketchsample.ImageOptions;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.adapter.BindAssemblyRecyclerItem;
import me.xiaopan.sketchsample.bean.UnsplashImage;
import me.xiaopan.sketchsample.widget.SampleImageView;

public class UnsplashPhotosItemFactory extends AssemblyRecyclerItemFactory<UnsplashPhotosItemFactory.UnsplashPhotosItem> {
    private UnsplashPhotosItemEventListener unsplashPhotosItemEventListener;

    public UnsplashPhotosItemFactory(UnsplashPhotosItemEventListener unsplashPhotosItemEventListener) {
        this.unsplashPhotosItemEventListener = unsplashPhotosItemEventListener;
    }

    @Override
    public boolean isTarget(Object o) {
        return o instanceof UnsplashImage;
    }

    @Override
    public UnsplashPhotosItem createAssemblyItem(ViewGroup viewGroup) {
        return new UnsplashPhotosItem(R.layout.list_item_image_unsplash, viewGroup);
    }

    public interface UnsplashPhotosItemEventListener {
        void onClickImage(int position, UnsplashImage image, String optionsKey);

        void onClickUser(int position, UnsplashImage.User user);
    }

    public class UnsplashPhotosItem extends BindAssemblyRecyclerItem<UnsplashImage> {
        @BindView(R.id.image_unsplashImageItem)
        SampleImageView imageView;
        @BindView(R.id.image_unsplashImageItem_userProfile)
        SampleImageView userProfileImageView;
        @BindView(R.id.text_unsplashImageItem_userName)
        TextView userNameTextView;
        @BindView(R.id.text_unsplashImageItem_date)
        TextView dateTextView;
        @BindView(R.id.layout_unsplashImageItem_root)
        ViewGroup rootViewGroup;

        public UnsplashPhotosItem(int itemLayoutId, ViewGroup parent) {
            super(itemLayoutId, parent);
        }

        @Override
        protected void onConfigViews(Context context) {
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (unsplashPhotosItemEventListener != null) {
                        unsplashPhotosItemEventListener.onClickImage(getAdapterPosition(), getData(), imageView.getOptionsKey());
                    }
                }
            });
            imageView.setOptions(ImageOptions.LIST_FULL);

            imageView.setPage(SampleImageView.Page.UNSPLASH_LIST);

            userProfileImageView.setOptions(ImageOptions.CIRCULAR_STROKE);

            userProfileImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (unsplashPhotosItemEventListener != null) {
                        unsplashPhotosItemEventListener.onClickUser(getAdapterPosition(), getData().user);
                    }
                }
            });

            userNameTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    userProfileImageView.performClick();
                }
            });
        }

        @Override
        protected void onSetData(int i, UnsplashImage image) {
            int itemWidth = imageView.getContext().getResources().getDisplayMetrics().widthPixels;

            ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
            layoutParams.width = itemWidth;
            layoutParams.height = (int) (itemWidth / (image.width / (float) image.height));
            imageView.setLayoutParams(layoutParams);

            layoutParams = rootViewGroup.getLayoutParams();
            layoutParams.width = itemWidth;
            layoutParams.height = (int) (itemWidth / (image.width / (float) image.height));
            rootViewGroup.setLayoutParams(layoutParams);

            imageView.displayImage(image.urls.regular);

            userProfileImageView.displayImage(image.user.profileImage.large);

            userNameTextView.setText(image.user.name);
            dateTextView.setText(image.getFormattedUpdateDate());
        }
    }
}