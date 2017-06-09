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
import me.xiaopan.sketchsample.bean.VideoItem;
import me.xiaopan.sketchsample.util.VideoThumbnailPreprocessor;
import me.xiaopan.sketchsample.widget.SampleImageView;

public class MyVideoItemFactory extends AssemblyRecyclerItemFactory<MyVideoItemFactory.MyVideoItem> {
    private MyVideoItemListener listener;

    public MyVideoItemFactory(MyVideoItemListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean isTarget(Object o) {
        return o instanceof VideoItem;
    }

    @Override
    public MyVideoItem createAssemblyItem(ViewGroup viewGroup) {
        return new MyVideoItem(R.layout.list_item_my_video, viewGroup);
    }

    public interface MyVideoItemListener {
        void onClickVideo(int position, VideoItem videoItem);
    }

    public class MyVideoItem extends BindAssemblyRecyclerItem<VideoItem> {
        @BindView(R.id.image_myVideoItem_icon)
        SampleImageView iconImageView;

        @BindView(R.id.text_myVideoItem_name)
        TextView nameTextView;

        @BindView(R.id.text_myVideoItem_size)
        TextView sizeTextView;

        @BindView(R.id.text_myVideoItem_date)
        TextView dateTextView;

        @BindView(R.id.text_myVideoItem_duration)
        TextView durationTextView;

        public MyVideoItem(int itemLayoutId, ViewGroup parent) {
            super(itemLayoutId, parent);
        }

        @Override
        protected void onConfigViews(Context context) {
            iconImageView.setOptions(ImageOptions.RECT);
            iconImageView.setPage(SampleImageView.Page.PHOTO_LIST);

            getItemView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onClickVideo(getPosition(), getData());
                    }
                }
            });

            iconImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getItemView().performClick();
                }
            });
        }

        @Override
        protected void onSetData(int i, VideoItem videoItem) {
            iconImageView.displayImage(VideoThumbnailPreprocessor.createUri(videoItem.path));
            nameTextView.setText(videoItem.title);
            sizeTextView.setText(videoItem.getTempFormattedSize(sizeTextView.getContext()));
            dateTextView.setText(String.valueOf(videoItem.getTempFormattedDate()));
            durationTextView.setText(String.valueOf(videoItem.getTempFormattedDuration()));
        }
    }
}
