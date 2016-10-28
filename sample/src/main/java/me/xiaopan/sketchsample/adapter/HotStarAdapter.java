package me.xiaopan.sketchsample.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.xiaopan.sketchsample.ImageOptions;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.net.request.HotStarRequest;
import me.xiaopan.sketchsample.widget.MyImageView;

/**
 * 热门明星适配器
 */
public class HotStarAdapter extends RecyclerView.Adapter {
    private static final int ITEM_TYPE_CATEGORY_TITLE = 0;
    private static final int ITEM_TYPE_THREE_ITEM_LEFT = 1;
    private static final int ITEM_TYPE_THREE_ITEM_RIGHT = 2;
    private static final int ITEM_TYPE_TWO_ITEM = 3;
    private Context context;
    private List<Object> items;
    private int marginBorder;
    private int availableScreenWidth;
    private View.OnClickListener itemClickListener;

    public HotStarAdapter(Context context, List<HotStarRequest.HotStar> hotStarList, final OnImageClickListener onImageClickListener) {
        this.context = context;
        append(hotStarList);
        this.marginBorder = (int) context.getResources().getDimension(R.dimen.home_category_margin_border);
        this.availableScreenWidth = context.getResources().getDisplayMetrics().widthPixels - (marginBorder * 2);
        this.itemClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getTag() instanceof HotStarRequest.Star) {
                    if (onImageClickListener != null) {
                        onImageClickListener.onClickImage((HotStarRequest.Star) v.getTag());
                    }
                }
            }
        };
    }

    private void parse(List<HotStarRequest.Star> starList) {
        if (starList == null) {
            return;
        }
        boolean left = true;
        for (int w = 0, size = starList.size(); w < size; ) {
            int number = size - w;
            if (number == 1) {
                TwoItem oneItem = new TwoItem();
                oneItem.star1 = starList.get(w++);
                items.add(oneItem);
            } else if (number == 2) {
                TwoItem twoItem = new TwoItem();
                twoItem.star1 = starList.get(w++);
                twoItem.star2 = starList.get(w++);
                items.add(twoItem);
            } else {
                if (left) {
                    ThreeItemLeft threeItemLeft = new ThreeItemLeft();
                    threeItemLeft.star1 = starList.get(w++);
                    threeItemLeft.star2 = starList.get(w++);
                    threeItemLeft.star3 = starList.get(w++);
                    items.add(threeItemLeft);
                } else {
                    ThreeItemRight threeItemRight = new ThreeItemRight();
                    threeItemRight.star1 = starList.get(w++);
                    threeItemRight.star2 = starList.get(w++);
                    threeItemRight.star3 = starList.get(w++);
                    items.add(threeItemRight);
                }
                left = !left;
            }
        }
    }

    public void append(List<HotStarRequest.HotStar> hotStarList) {
        if (items == null) {
            items = new ArrayList<Object>();
        }
        for (HotStarRequest.HotStar hotStar : hotStarList) {
            items.add(hotStar.getName());
            parse(hotStar.getStarList());
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        Object item = items.get(position);
        if (item instanceof String) {
            return ITEM_TYPE_CATEGORY_TITLE;
        } else if (item instanceof ThreeItemLeft) {
            return ITEM_TYPE_THREE_ITEM_LEFT;
        } else if (item instanceof ThreeItemRight) {
            return ITEM_TYPE_THREE_ITEM_RIGHT;
        } else if (item instanceof TwoItem) {
            return ITEM_TYPE_TWO_ITEM;
        } else {
            return -1;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        if (viewType == ITEM_TYPE_CATEGORY_TITLE) {
            viewHolder = new CategoryTitleHolder(LayoutInflater.from(context).inflate(R.layout.list_item_title, parent, false));
        } else if (viewType == ITEM_TYPE_THREE_ITEM_LEFT) {
            ThreeItemLeftHolder threeItemLeftHolder = new ThreeItemLeftHolder(LayoutInflater.from(context).inflate(R.layout.list_item_hot_star_three_left, parent, false));

            threeItemLeftHolder.oneImageView.setOnClickListener(itemClickListener);
            threeItemLeftHolder.twoImageView.setOnClickListener(itemClickListener);
            threeItemLeftHolder.threeImageView.setOnClickListener(itemClickListener);

            int itemWidth = (availableScreenWidth - marginBorder) / 2;
            int itemHeight = (int) (itemWidth / 0.75);

            ViewGroup.LayoutParams params = threeItemLeftHolder.oneImageView.getLayoutParams();
            params.width = itemWidth;
            params.height = itemHeight;
            threeItemLeftHolder.oneImageView.setLayoutParams(params);
            threeItemLeftHolder.oneImageView.setOptionsByName(ImageOptions.NORMAL_RECT);

            params = threeItemLeftHolder.twoImageView.getLayoutParams();
            params.width = itemWidth;
            params.height = (itemHeight - marginBorder) / 2;
            threeItemLeftHolder.twoImageView.setLayoutParams(params);
            threeItemLeftHolder.twoImageView.setOptionsByName(ImageOptions.NORMAL_RECT);

            params = threeItemLeftHolder.threeImageView.getLayoutParams();
            params.width = itemWidth;
            params.height = (itemHeight - marginBorder) / 2;
            threeItemLeftHolder.threeImageView.setLayoutParams(params);
            threeItemLeftHolder.threeImageView.setOptionsByName(ImageOptions.NORMAL_RECT);

            viewHolder = threeItemLeftHolder;
        } else if (viewType == ITEM_TYPE_THREE_ITEM_RIGHT) {
            ThreeItemRightHolder threeItemRightHolder = new ThreeItemRightHolder(LayoutInflater.from(context).inflate(R.layout.list_item_hot_star_three_right, parent, false));

            threeItemRightHolder.oneImageView.setOnClickListener(itemClickListener);
            threeItemRightHolder.twoImageView.setOnClickListener(itemClickListener);
            threeItemRightHolder.threeImageView.setOnClickListener(itemClickListener);

            int itemWidth = (availableScreenWidth - marginBorder) / 2;
            int itemHeight = (int) (itemWidth / 0.75);

            ViewGroup.LayoutParams params = threeItemRightHolder.threeImageView.getLayoutParams();
            params.width = itemWidth;
            params.height = itemHeight;
            threeItemRightHolder.threeImageView.setLayoutParams(params);
            threeItemRightHolder.threeImageView.setOptionsByName(ImageOptions.NORMAL_RECT);

            params = threeItemRightHolder.twoImageView.getLayoutParams();
            params.width = itemWidth;
            params.height = (itemHeight - marginBorder) / 2;
            threeItemRightHolder.twoImageView.setLayoutParams(params);
            threeItemRightHolder.twoImageView.setOptionsByName(ImageOptions.NORMAL_RECT);

            params = threeItemRightHolder.oneImageView.getLayoutParams();
            params.width = itemWidth;
            params.height = (itemHeight - marginBorder) / 2;
            threeItemRightHolder.oneImageView.setLayoutParams(params);
            threeItemRightHolder.oneImageView.setOptionsByName(ImageOptions.NORMAL_RECT);

            viewHolder = threeItemRightHolder;
        } else if (viewType == ITEM_TYPE_TWO_ITEM) {
            TwoItemHolder twoItemHolder = new TwoItemHolder(LayoutInflater.from(context).inflate(R.layout.list_item_hot_star_two, parent, false));

            twoItemHolder.oneImageView.setOnClickListener(itemClickListener);
            twoItemHolder.twoImageView.setOnClickListener(itemClickListener);

            int itemWidth = (availableScreenWidth - marginBorder) / 2;
            int itemHeight = (int) (itemWidth / 0.75);

            ViewGroup.LayoutParams params = twoItemHolder.oneImageView.getLayoutParams();
            params.width = itemWidth;
            params.height = (itemHeight - marginBorder) / 2;
            twoItemHolder.oneImageView.setLayoutParams(params);
            twoItemHolder.oneImageView.setOptionsByName(ImageOptions.NORMAL_RECT);

            params = twoItemHolder.twoImageView.getLayoutParams();
            params.width = itemWidth;
            params.height = (itemHeight - marginBorder) / 2;
            twoItemHolder.twoImageView.setLayoutParams(params);
            twoItemHolder.twoImageView.setOptionsByName(ImageOptions.NORMAL_RECT);

            viewHolder = twoItemHolder;
        } else {
            return null;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CategoryTitleHolder) {
            CategoryTitleHolder categoryTitleHolder = (CategoryTitleHolder) holder;
            categoryTitleHolder.categoryTitleTextView.setText((String) items.get(position));
        } else if (holder instanceof ThreeItemLeftHolder) {
            ThreeItemLeftHolder threeItemLeftHolder = (ThreeItemLeftHolder) holder;
            ThreeItemLeft threeItemLeft = (ThreeItemLeft) items.get(position);

            threeItemLeftHolder.oneNameTextView.setText(threeItemLeft.star1.getName());
            threeItemLeftHolder.twoNameTextView.setText(threeItemLeft.star2.getName());
            threeItemLeftHolder.threeNameTextView.setText(threeItemLeft.star3.getName());

            threeItemLeftHolder.oneImageView.setTag(threeItemLeft.star1);
            threeItemLeftHolder.twoImageView.setTag(threeItemLeft.star2);
            threeItemLeftHolder.threeImageView.setTag(threeItemLeft.star3);

            threeItemLeftHolder.oneImageView.displayImage(threeItemLeft.star1.getHeightImage().getUrl());
            threeItemLeftHolder.twoImageView.displayImage(threeItemLeft.star2.getWidthImage().getUrl());
            threeItemLeftHolder.threeImageView.displayImage(threeItemLeft.star3.getWidthImage().getUrl());
        } else if (holder instanceof ThreeItemRightHolder) {
            ThreeItemRightHolder threeItemRightHolder = (ThreeItemRightHolder) holder;
            ThreeItemRight threeItemRight = (ThreeItemRight) items.get(position);

            threeItemRightHolder.oneNameTextView.setText(threeItemRight.star1.getName());
            threeItemRightHolder.twoNameTextView.setText(threeItemRight.star2.getName());
            threeItemRightHolder.threeNameTextView.setText(threeItemRight.star3.getName());

            threeItemRightHolder.oneImageView.setTag(threeItemRight.star1);
            threeItemRightHolder.twoImageView.setTag(threeItemRight.star2);
            threeItemRightHolder.threeImageView.setTag(threeItemRight.star3);

            threeItemRightHolder.oneImageView.displayImage(threeItemRight.star1.getWidthImage().getUrl());
            threeItemRightHolder.twoImageView.displayImage(threeItemRight.star2.getWidthImage().getUrl());
            threeItemRightHolder.threeImageView.displayImage(threeItemRight.star3.getHeightImage().getUrl());
        } else if (holder instanceof TwoItemHolder) {
            TwoItemHolder twoItemHolder = (TwoItemHolder) holder;
            TwoItem twoItem = (TwoItem) items.get(position);

            twoItemHolder.oneNameTextView.setText(twoItem.star1.getName());
            twoItemHolder.oneImageView.setTag(twoItem.star1);
            twoItemHolder.oneImageView.displayImage(twoItem.star1.getWidthImage().getUrl());


            if (twoItem.star2 != null) {
                twoItemHolder.twoNameTextView.setText(twoItem.star2.getName());
                twoItemHolder.twoImageView.setTag(twoItem.star2);
                twoItemHolder.twoImageView.displayImage(twoItem.star2.getWidthImage().getUrl());

                twoItemHolder.twoNameTextView.setVisibility(View.VISIBLE);
                twoItemHolder.twoImageView.setVisibility(View.VISIBLE);
            } else {
                twoItemHolder.twoNameTextView.setVisibility(View.INVISIBLE);
                twoItemHolder.twoImageView.setVisibility(View.INVISIBLE);
            }
        }

        int topMargin;
        int bottomMargin;
        if (position == 0) {
            topMargin = marginBorder;
            bottomMargin = marginBorder / 2;
        } else if (position == getItemCount() - 1) {
            topMargin = marginBorder / 2;
            bottomMargin = marginBorder;
        } else {
            topMargin = marginBorder / 2;
            bottomMargin = marginBorder / 2;
        }
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
        params.topMargin = topMargin;
        params.bottomMargin = bottomMargin;
        holder.itemView.setLayoutParams(params);
    }

    private static class ThreeItemLeftHolder extends RecyclerView.ViewHolder {
        private MyImageView oneImageView;
        private MyImageView twoImageView;
        private MyImageView threeImageView;
        private TextView oneNameTextView;
        private TextView twoNameTextView;
        private TextView threeNameTextView;

        public ThreeItemLeftHolder(View itemView) {
            super(itemView);
            oneImageView = (MyImageView) itemView.findViewById(R.id.image_hotStarThreeLeftItem_one);
            twoImageView = (MyImageView) itemView.findViewById(R.id.image_hotStarThreeLeftItem_two);
            threeImageView = (MyImageView) itemView.findViewById(R.id.image_hotStarThreeLeftItem_three);
            oneNameTextView = (TextView) itemView.findViewById(R.id.text_hotStarThreeLeftItem_one);
            twoNameTextView = (TextView) itemView.findViewById(R.id.text_hotStarThreeLeftItem_two);
            threeNameTextView = (TextView) itemView.findViewById(R.id.text_hotStarThreeLeftItem_three);
        }
    }

    private static class ThreeItemRightHolder extends RecyclerView.ViewHolder {
        private MyImageView oneImageView;
        private MyImageView twoImageView;
        private MyImageView threeImageView;
        private TextView oneNameTextView;
        private TextView twoNameTextView;
        private TextView threeNameTextView;

        public ThreeItemRightHolder(View itemView) {
            super(itemView);
            oneImageView = (MyImageView) itemView.findViewById(R.id.image_hotStarThreeRightItem_one);
            twoImageView = (MyImageView) itemView.findViewById(R.id.image_hotStarThreeRightItem_two);
            threeImageView = (MyImageView) itemView.findViewById(R.id.image_hotStarThreeRightItem_three);
            oneNameTextView = (TextView) itemView.findViewById(R.id.text_hotStarThreeRightItem_one);
            twoNameTextView = (TextView) itemView.findViewById(R.id.text_hotStarThreeRightItem_two);
            threeNameTextView = (TextView) itemView.findViewById(R.id.text_hotStarThreeRightItem_three);
        }
    }

    private static class TwoItemHolder extends RecyclerView.ViewHolder {
        private MyImageView oneImageView;
        private MyImageView twoImageView;
        private TextView oneNameTextView;
        private TextView twoNameTextView;

        public TwoItemHolder(View itemView) {
            super(itemView);
            oneImageView = (MyImageView) itemView.findViewById(R.id.image_hotStarTwoItem_one);
            twoImageView = (MyImageView) itemView.findViewById(R.id.image_hotStarTwoItem_two);
            oneNameTextView = (TextView) itemView.findViewById(R.id.text_hotStarTwoItem_one);
            twoNameTextView = (TextView) itemView.findViewById(R.id.text_hotStarTwoItem_two);
        }
    }

    private static class CategoryTitleHolder extends RecyclerView.ViewHolder {
        private TextView categoryTitleTextView;

        public CategoryTitleHolder(View itemView) {
            super(itemView);
            categoryTitleTextView = (TextView) itemView;
        }
    }

    private static class ThreeItemLeft {
        private HotStarRequest.Star star1;
        private HotStarRequest.Star star2;
        private HotStarRequest.Star star3;
    }

    private static class ThreeItemRight {
        private HotStarRequest.Star star1;
        private HotStarRequest.Star star2;
        private HotStarRequest.Star star3;
    }

    private static class TwoItem {
        private HotStarRequest.Star star1;
        private HotStarRequest.Star star2;
    }

    public interface OnImageClickListener {
        void onClickImage(HotStarRequest.Star star);
    }
}
