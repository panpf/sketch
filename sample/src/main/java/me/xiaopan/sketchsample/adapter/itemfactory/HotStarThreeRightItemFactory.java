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
import me.xiaopan.sketchsample.bean.ThreeStarRight;
import me.xiaopan.sketchsample.widget.MyImageView;

public class HotStarThreeRightItemFactory extends AssemblyRecyclerItemFactory<HotStarThreeRightItemFactory.HotStarThreeLeftItem> {

    private HotStarThreeLeftItemFactory.OnStarClickListener imageClickListener;
    private int marginBorder;
    private int availableScreenWidth;

    public HotStarThreeRightItemFactory(HotStarThreeLeftItemFactory.OnStarClickListener imageClickListener) {
        this.imageClickListener = imageClickListener;
    }

    @Override
    public boolean isTarget(Object o) {
        return o instanceof ThreeStarRight;
    }

    @Override
    public HotStarThreeLeftItem createAssemblyItem(ViewGroup viewGroup) {
        return new HotStarThreeLeftItem(R.layout.list_item_hot_star_three_right, viewGroup);
    }

    public class HotStarThreeLeftItem extends BindAssemblyRecyclerItem<ThreeStarRight> {
        @BindView(R.id.image_hotStarThreeRightItem_one)
        MyImageView oneImageView;
        @BindView(R.id.image_hotStarThreeRightItem_two)
        MyImageView twoImageView;
        @BindView(R.id.image_hotStarThreeRightItem_three)
        MyImageView threeImageView;
        @BindView(R.id.text_hotStarThreeRightItem_one)
        TextView oneNameTextView;
        @BindView(R.id.text_hotStarThreeRightItem_two)
        TextView twoNameTextView;
        @BindView(R.id.text_hotStarThreeRightItem_three)
        TextView threeNameTextView;

        public HotStarThreeLeftItem(int itemLayoutId, ViewGroup parent) {
            super(itemLayoutId, parent);
        }

        @Override
        protected void onConfigViews(Context context) {
            oneImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (imageClickListener != null) {
                        imageClickListener.onClickImage(getData().star1);
                    }
                }
            });
            twoImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (imageClickListener != null) {
                        imageClickListener.onClickImage(getData().star2);
                    }
                }
            });
            threeImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (imageClickListener != null) {
                        imageClickListener.onClickImage(getData().star3);
                    }
                }
            });

            if (marginBorder == 0) {
                marginBorder = (int) context.getResources().getDimension(R.dimen.home_category_margin_border_2);
                availableScreenWidth = context.getResources().getDisplayMetrics().widthPixels - (marginBorder * 2);
            }

            int itemWidth = (availableScreenWidth - marginBorder) / 2;
            int itemHeight = (int) (itemWidth / 0.75);

            ViewGroup.LayoutParams params = threeImageView.getLayoutParams();
            params.width = itemWidth;
            params.height = itemHeight;
            threeImageView.setLayoutParams(params);
            threeImageView.setOptionsByName(ImageOptions.RECT);

            params = twoImageView.getLayoutParams();
            params.width = itemWidth;
            params.height = (itemHeight - marginBorder) / 2;
            twoImageView.setLayoutParams(params);
            twoImageView.setOptionsByName(ImageOptions.RECT);

            params = oneImageView.getLayoutParams();
            params.width = itemWidth;
            params.height = (itemHeight - marginBorder) / 2;
            oneImageView.setLayoutParams(params);
            oneImageView.setOptionsByName(ImageOptions.RECT);
        }

        @Override
        protected void onSetData(int position, ThreeStarRight threeStarRight) {
            oneNameTextView.setText(threeStarRight.star1.getName());
            twoNameTextView.setText(threeStarRight.star2.getName());
            threeNameTextView.setText(threeStarRight.star3.getName());

            oneImageView.setTag(threeStarRight.star1);
            twoImageView.setTag(threeStarRight.star2);
            threeImageView.setTag(threeStarRight.star3);

            oneImageView.displayImage(threeStarRight.star1.getWidthImage().getUrl());
            twoImageView.displayImage(threeStarRight.star2.getWidthImage().getUrl());
            threeImageView.displayImage(threeStarRight.star3.getHeightImage().getUrl());

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
