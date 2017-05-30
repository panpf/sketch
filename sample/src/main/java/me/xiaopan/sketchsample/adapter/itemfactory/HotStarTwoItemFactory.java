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
import me.xiaopan.sketchsample.bean.TwoStar;
import me.xiaopan.sketchsample.widget.MyImageView;

public class HotStarTwoItemFactory extends AssemblyRecyclerItemFactory<HotStarTwoItemFactory.HotStarThreeLeftItem> {

    private HotStarThreeLeftItemFactory.OnStarClickListener imageClickListener;
    private int marginBorder;
    private int availableScreenWidth;

    public HotStarTwoItemFactory(HotStarThreeLeftItemFactory.OnStarClickListener imageClickListener) {
        this.imageClickListener = imageClickListener;
    }

    @Override
    public boolean isTarget(Object o) {
        return o instanceof TwoStar;
    }

    @Override
    public HotStarThreeLeftItem createAssemblyItem(ViewGroup viewGroup) {
        return new HotStarThreeLeftItem(R.layout.list_item_hot_star_two, viewGroup);
    }

    public class HotStarThreeLeftItem extends BindAssemblyRecyclerItem<TwoStar> {
        @BindView(R.id.image_hotStarTwoItem_one)
        MyImageView oneImageView;
        @BindView(R.id.image_hotStarTwoItem_two)
        MyImageView twoImageView;
        @BindView(R.id.text_hotStarTwoItem_one)
        TextView oneNameTextView;
        @BindView(R.id.text_hotStarTwoItem_two)
        TextView twoNameTextView;

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

            if (marginBorder == 0) {
                marginBorder = (int) context.getResources().getDimension(R.dimen.home_category_margin_border_2);
                availableScreenWidth = context.getResources().getDisplayMetrics().widthPixels - (marginBorder * 2);
            }

            int itemWidth = (availableScreenWidth - marginBorder) / 2;
            int itemHeight = (int) (itemWidth / 0.75);

            ViewGroup.LayoutParams params = oneImageView.getLayoutParams();
            params.width = itemWidth;
            params.height = (itemHeight - marginBorder) / 2;
            oneImageView.setLayoutParams(params);
            oneImageView.setOptionsByName(ImageOptions.RECT);

            params = twoImageView.getLayoutParams();
            params.width = itemWidth;
            params.height = (itemHeight - marginBorder) / 2;
            twoImageView.setLayoutParams(params);
            twoImageView.setOptionsByName(ImageOptions.RECT);

            oneImageView.setPage(MyImageView.Page.PHOTO_ALBUM_LIST);
            twoImageView.setPage(MyImageView.Page.PHOTO_ALBUM_LIST);
        }

        @Override
        protected void onSetData(int position, TwoStar twoStar) {
            oneNameTextView.setText(twoStar.star1.getName());
            oneImageView.setTag(twoStar.star1);
            oneImageView.displayImage(twoStar.star1.getWidthImage().getUrl());

            if (twoStar.star2 != null) {
                twoNameTextView.setText(twoStar.star2.getName());
                twoImageView.setTag(twoStar.star2);
                twoImageView.displayImage(twoStar.star2.getWidthImage().getUrl());

                twoNameTextView.setVisibility(View.VISIBLE);
                twoImageView.setVisibility(View.VISIBLE);
            } else {
                twoNameTextView.setVisibility(View.INVISIBLE);
                twoImageView.setVisibility(View.INVISIBLE);
            }

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
