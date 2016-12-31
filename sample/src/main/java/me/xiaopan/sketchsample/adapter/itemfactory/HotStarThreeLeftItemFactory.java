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
import me.xiaopan.sketchsample.bean.ThreeStarLeft;
import me.xiaopan.sketchsample.net.request.HotStarRequest;
import me.xiaopan.sketchsample.widget.MyImageView;

public class HotStarThreeLeftItemFactory extends AssemblyRecyclerItemFactory<HotStarThreeLeftItemFactory.HotStarThreeLeftItem> {

    private OnStarClickListener onStarClickListener;
    private int marginBorder;
    private int availableScreenWidth;

    public HotStarThreeLeftItemFactory(OnStarClickListener onStarClickListener) {
        this.onStarClickListener = onStarClickListener;
    }

    @Override
    public boolean isTarget(Object o) {
        return o instanceof ThreeStarLeft;
    }

    @Override
    public HotStarThreeLeftItem createAssemblyItem(ViewGroup viewGroup) {
        return new HotStarThreeLeftItem(R.layout.list_item_hot_star_three_left, viewGroup);
    }

    public interface OnStarClickListener {
        void onClickImage(HotStarRequest.Star star);
    }

    public class HotStarThreeLeftItem extends BindAssemblyRecyclerItem<ThreeStarLeft> {
        @BindView(R.id.image_hotStarThreeLeftItem_one)
        MyImageView oneImageView;
        @BindView(R.id.image_hotStarThreeLeftItem_two)
        MyImageView twoImageView;
        @BindView(R.id.image_hotStarThreeLeftItem_three)
        MyImageView threeImageView;
        @BindView(R.id.text_hotStarThreeLeftItem_one)
        TextView oneNameTextView;
        @BindView(R.id.text_hotStarThreeLeftItem_two)
        TextView twoNameTextView;
        @BindView(R.id.text_hotStarThreeLeftItem_three)
        TextView threeNameTextView;

        public HotStarThreeLeftItem(int itemLayoutId, ViewGroup parent) {
            super(itemLayoutId, parent);
        }

        @Override
        protected void onConfigViews(Context context) {
            oneImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onStarClickListener != null) {
                        onStarClickListener.onClickImage(getData().star1);
                    }
                }
            });
            twoImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onStarClickListener != null) {
                        onStarClickListener.onClickImage(getData().star2);
                    }
                }
            });
            threeImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onStarClickListener != null) {
                        onStarClickListener.onClickImage(getData().star3);
                    }
                }
            });

            if (marginBorder == 0) {
                marginBorder = (int) context.getResources().getDimension(R.dimen.home_category_margin_border_2);
                availableScreenWidth = context.getResources().getDisplayMetrics().widthPixels - (marginBorder * 2);
            }

            int itemWidth = (availableScreenWidth - marginBorder) / 2;
            int itemHeight = (int) (itemWidth / 0.75);

            ViewGroup.LayoutParams params = oneImageView.getLayoutParams();
            params.width = itemWidth;
            params.height = itemHeight;
            oneImageView.setLayoutParams(params);
            oneImageView.setOptionsByName(ImageOptions.RECT);

            params = twoImageView.getLayoutParams();
            params.width = itemWidth;
            params.height = (itemHeight - marginBorder) / 2;
            twoImageView.setLayoutParams(params);
            twoImageView.setOptionsByName(ImageOptions.RECT);

            params = threeImageView.getLayoutParams();
            params.width = itemWidth;
            params.height = (itemHeight - marginBorder) / 2;
            threeImageView.setLayoutParams(params);
            threeImageView.setOptionsByName(ImageOptions.RECT);
        }

        @Override
        protected void onSetData(int position, ThreeStarLeft threeStarLeft) {
            oneNameTextView.setText(threeStarLeft.star1.getName());
            twoNameTextView.setText(threeStarLeft.star2.getName());
            threeNameTextView.setText(threeStarLeft.star3.getName());

            oneImageView.setTag(threeStarLeft.star1);
            twoImageView.setTag(threeStarLeft.star2);
            threeImageView.setTag(threeStarLeft.star3);

            oneImageView.displayImage(threeStarLeft.star1.getHeightImage().getUrl());
            twoImageView.displayImage(threeStarLeft.star2.getWidthImage().getUrl());
            threeImageView.displayImage(threeStarLeft.star3.getWidthImage().getUrl());

            int topMargin;
            int bottomMargin;
            if (position == 0) {
                topMargin = marginBorder;
                bottomMargin = marginBorder / 2;
            } else if (position == getAdapter().getItemCount() - 1) {
                topMargin = marginBorder / 2;
                bottomMargin = marginBorder;
            } else {
                topMargin = marginBorder / 2;
                bottomMargin = marginBorder / 2;
            }

            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) getItemView().getLayoutParams();
            params.topMargin = topMargin;
            params.bottomMargin = bottomMargin;
            getItemView().setLayoutParams(params);
        }
    }
}
