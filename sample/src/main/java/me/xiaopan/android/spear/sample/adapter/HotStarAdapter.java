package me.xiaopan.android.spear.sample.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.xiaoapn.android.spear.sample.R;
import me.xiaopan.android.spear.sample.DisplayOptionsType;
import me.xiaopan.android.spear.sample.net.request.HotStarRequest;
import me.xiaopan.android.spear.widget.SpearImageView;

/**
 * 热门明星适配器
 */
public class HotStarAdapter extends RecyclerView.Adapter{
    private static final int ITEM_TYPE_CATEGORY_TITLE = 0;
    private static final int ITEM_TYPE_THREE_ITEM_LEFT = 1;
    private static final int ITEM_TYPE_THREE_ITEM_RIGHT = 2;
    private static final int ITEM_TYPE_TWO_ITEM = 3;
    private static final int ITEM_TYPE_ONE_ITEM = 4;
    private Context context;
    private List<Object> items;
    private int marginBorder;
    private View.OnClickListener onClickListener;

    public HotStarAdapter(Context context, List<HotStarRequest.HotStar> hotStarList, final OnImageClickListener onImageClickListener) {
        this.context = context;
        marginBorder = (int) context.getResources().getDimension(R.dimen.home_category_margin_border);
        this.items = new ArrayList<>();
        for(HotStarRequest.HotStar hotStar : hotStarList){
            items.add(hotStar.getName());
            parse(hotStar.getStarList());
        }
        this.onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getTag() instanceof HotStarRequest.Star){
                    if(onImageClickListener != null){
                        onImageClickListener.onClickImage((HotStarRequest.Star) v.getTag());
                    }
                }
            }
        };
    }

    private void parse(List<HotStarRequest.Star> starList){
        boolean left = true;
        for(int w = 0, size = starList.size(); w < size;){
            int number = size - w;
            if(number == 1){
                OneItem oneItem = new OneItem();
                oneItem.star1 = starList.get(w++);
                items.add(oneItem);
            }else if(number == 2){
                TwoItem twoItem = new TwoItem();
                twoItem.star1 = starList.get(w++);
                twoItem.star2 = starList.get(w++);
                items.add(twoItem);
            }else{
                if(left){
                    ThreeItemLeft threeItemLeft = new ThreeItemLeft();
                    threeItemLeft.star1 = starList.get(w++);
                    threeItemLeft.star2 = starList.get(w++);
                    threeItemLeft.star3 = starList.get(w++);
                    items.add(threeItemLeft);
                }else{
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

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        Object item = items.get(position);
        if(item instanceof String){
            return ITEM_TYPE_CATEGORY_TITLE;
        }else if(item instanceof ThreeItemLeft){
            return ITEM_TYPE_THREE_ITEM_LEFT;
        }else if(item instanceof ThreeItemRight){
            return ITEM_TYPE_THREE_ITEM_RIGHT;
        }else if(item instanceof TwoItem){
            return ITEM_TYPE_TWO_ITEM;
        }else if(item instanceof OneItem){
            return ITEM_TYPE_ONE_ITEM;
        }else{
            return -1;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == ITEM_TYPE_CATEGORY_TITLE){
            return new CategoryTitleHolder(LayoutInflater.from(context).inflate(R.layout.list_item_hot_star_category_title, parent, false));
        }else if(viewType == ITEM_TYPE_THREE_ITEM_LEFT){
            return new ThreeItemLeftHolder(LayoutInflater.from(context).inflate(R.layout.list_item_hot_star_three_left, parent, false), onClickListener);
        }else if(viewType == ITEM_TYPE_THREE_ITEM_RIGHT){
            return new ThreeItemRightHolder(LayoutInflater.from(context).inflate(R.layout.list_item_hot_star_three_right, parent, false), onClickListener);
        }else if(viewType == ITEM_TYPE_TWO_ITEM){
            return new TwoItemHolder(LayoutInflater.from(context).inflate(R.layout.list_item_hot_star_two, parent, false), onClickListener);
        }else if(viewType == ITEM_TYPE_ONE_ITEM){
            return new OneItemHolder(LayoutInflater.from(context).inflate(R.layout.list_item_hot_star_one, parent, false), onClickListener);
        }else{
            return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof CategoryTitleHolder){
            CategoryTitleHolder categoryTitleHolder = (CategoryTitleHolder) holder;
            categoryTitleHolder.categoryTitleTextView.setText((String)items.get(position));
        }else if(holder instanceof ThreeItemLeftHolder){
            ThreeItemLeftHolder threeItemLeftHolder = (ThreeItemLeftHolder) holder;
            ThreeItemLeft threeItemLeft = (ThreeItemLeft) items.get(position);
            threeItemLeftHolder.oneNameTextView.setText(threeItemLeft.star1.getName());
            threeItemLeftHolder.twoNameTextView.setText(threeItemLeft.star2.getName());
            threeItemLeftHolder.threeNameTextView.setText(threeItemLeft.star3.getName());
            threeItemLeftHolder.oneSpearImageView.setImageByUri(threeItemLeft.star1.getHeightImage().getUrl());
            threeItemLeftHolder.twoSpearImageView.setImageByUri(threeItemLeft.star2.getWidthImage().getUrl());
            threeItemLeftHolder.threeSpearImageView.setImageByUri(threeItemLeft.star3.getWidthImage().getUrl());
            threeItemLeftHolder.oneSpearImageView.setTag(threeItemLeft.star1);
            threeItemLeftHolder.twoSpearImageView.setTag(threeItemLeft.star2);
            threeItemLeftHolder.threeSpearImageView.setTag(threeItemLeft.star3);
        }else if(holder instanceof ThreeItemRightHolder){
            ThreeItemRightHolder threeItemRightHolder = (ThreeItemRightHolder) holder;
            ThreeItemRight threeItemRight = (ThreeItemRight) items.get(position);
            threeItemRightHolder.oneNameTextView.setText(threeItemRight.star1.getName());
            threeItemRightHolder.twoNameTextView.setText(threeItemRight.star2.getName());
            threeItemRightHolder.threeNameTextView.setText(threeItemRight.star3.getName());
            threeItemRightHolder.oneSpearImageView.setImageByUri(threeItemRight.star1.getWidthImage().getUrl());
            threeItemRightHolder.twoSpearImageView.setImageByUri(threeItemRight.star2.getWidthImage().getUrl());
            threeItemRightHolder.threeSpearImageView.setImageByUri(threeItemRight.star3.getHeightImage().getUrl());
            threeItemRightHolder.oneSpearImageView.setTag(threeItemRight.star1);
            threeItemRightHolder.twoSpearImageView.setTag(threeItemRight.star2);
            threeItemRightHolder.threeSpearImageView.setTag(threeItemRight.star3);
        }else if(holder instanceof TwoItemHolder){
            TwoItemHolder twoItemHolder = (TwoItemHolder) holder;
            TwoItem twoItem = (TwoItem) items.get(position);
            twoItemHolder.oneNameTextView.setText(twoItem.star1.getName());
            twoItemHolder.twoNameTextView.setText(twoItem.star2.getName());
            twoItemHolder.oneSpearImageView.setImageByUri(twoItem.star1.getWidthImage().getUrl());
            twoItemHolder.twoSpearImageView.setImageByUri(twoItem.star2.getWidthImage().getUrl());
            twoItemHolder.oneSpearImageView.setTag(twoItem.star1);
            twoItemHolder.twoSpearImageView.setTag(twoItem.star2);
        }else if(holder instanceof OneItemHolder){
            OneItemHolder oneItemHolder = (OneItemHolder) holder;
            OneItem oneItem = (OneItem) items.get(position);
            oneItemHolder.oneNameTextView.setText(oneItem.star1.getName());
            oneItemHolder.oneSpearImageView.setImageByUri(oneItem.star1.getWidthImage().getUrl());
            oneItemHolder.oneSpearImageView.setTag(oneItem.star1);
        }

        int topMargin;
        int bottomMargin;
        if(position == 0){
            topMargin = marginBorder;
            bottomMargin = marginBorder/2;
        }else if(position == getItemCount()-1){
            topMargin = marginBorder/2;
            bottomMargin = marginBorder;
        }else{
            topMargin = marginBorder/2;
            bottomMargin = marginBorder/2;
        }
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
        params.topMargin = topMargin;
        params.bottomMargin = bottomMargin;
        holder.itemView.setLayoutParams(params);
    }

    private static class ThreeItemLeftHolder extends RecyclerView.ViewHolder{
        private SpearImageView oneSpearImageView;
        private SpearImageView twoSpearImageView;
        private SpearImageView threeSpearImageView;
        private TextView oneNameTextView;
        private TextView twoNameTextView;
        private TextView threeNameTextView;

        public ThreeItemLeftHolder(View itemView, View.OnClickListener onClickListener) {
            super(itemView);
            oneSpearImageView = (SpearImageView) itemView.findViewById(R.id.spearImage_hotStarThreeLeftItem_one);
            twoSpearImageView = (SpearImageView) itemView.findViewById(R.id.spearImage_hotStarThreeLeftItem_two);
            threeSpearImageView = (SpearImageView) itemView.findViewById(R.id.spearImage_hotStarThreeLeftItem_three);
            oneNameTextView = (TextView) itemView.findViewById(R.id.text_hotStarThreeLeftItem_one);
            twoNameTextView = (TextView) itemView.findViewById(R.id.text_hotStarThreeLeftItem_two);
            threeNameTextView = (TextView) itemView.findViewById(R.id.text_hotStarThreeLeftItem_three);

            oneSpearImageView.setOnClickListener(onClickListener);
            twoSpearImageView.setOnClickListener(onClickListener);
            threeSpearImageView.setOnClickListener(onClickListener);

            oneSpearImageView.setEnablePressRipple(true);
            twoSpearImageView.setEnablePressRipple(true);
            threeSpearImageView.setEnablePressRipple(true);

            int space = (int) itemView.getResources().getDimension(R.dimen.home_category_margin_border);
            int screenWidth = itemView.getContext().getResources().getDisplayMetrics().widthPixels;
            int availableScreenWidth = screenWidth - (space*3);
            int itemWidth = (int) (availableScreenWidth * 0.5);

            int oneHeight = (int) (itemWidth/0.75);
            ViewGroup.LayoutParams params = oneSpearImageView.getLayoutParams();
            params.width = itemWidth;
            params.height = oneHeight;
            oneSpearImageView.setLayoutParams(params);
            oneSpearImageView.setDisplayOptions(DisplayOptionsType.HOT_STAR_ONE);

            params = twoSpearImageView.getLayoutParams();
            params.width = (int) (availableScreenWidth * 0.5);
            params.height = (oneHeight-space)/2;
            twoSpearImageView.setLayoutParams(params);
            twoSpearImageView.setDisplayOptions(DisplayOptionsType.HOT_STAR_TWO);

            params = threeSpearImageView.getLayoutParams();
            params.width = (int) (availableScreenWidth * 0.5);
            params.height = (oneHeight-space)/2;
            threeSpearImageView.setLayoutParams(params);
            threeSpearImageView.setDisplayOptions(DisplayOptionsType.HOT_STAR_TWO);
        }
    }

    private static class ThreeItemRightHolder extends RecyclerView.ViewHolder{
        private SpearImageView oneSpearImageView;
        private SpearImageView twoSpearImageView;
        private SpearImageView threeSpearImageView;
        private TextView oneNameTextView;
        private TextView twoNameTextView;
        private TextView threeNameTextView;

        public ThreeItemRightHolder(View itemView, View.OnClickListener onClickListener) {
            super(itemView);
            oneSpearImageView = (SpearImageView) itemView.findViewById(R.id.spearImage_hotStarThreeRightItem_one);
            twoSpearImageView = (SpearImageView) itemView.findViewById(R.id.spearImage_hotStarThreeRightItem_two);
            threeSpearImageView = (SpearImageView) itemView.findViewById(R.id.spearImage_hotStarThreeRightItem_three);
            oneNameTextView = (TextView) itemView.findViewById(R.id.text_hotStarThreeRightItem_one);
            twoNameTextView = (TextView) itemView.findViewById(R.id.text_hotStarThreeRightItem_two);
            threeNameTextView = (TextView) itemView.findViewById(R.id.text_hotStarThreeRightItem_three);

            oneSpearImageView.setOnClickListener(onClickListener);
            twoSpearImageView.setOnClickListener(onClickListener);
            threeSpearImageView.setOnClickListener(onClickListener);

            oneSpearImageView.setEnablePressRipple(true);
            twoSpearImageView.setEnablePressRipple(true);
            threeSpearImageView.setEnablePressRipple(true);

            int space = (int) itemView.getResources().getDimension(R.dimen.home_category_margin_border);
            int screenWidth = itemView.getContext().getResources().getDisplayMetrics().widthPixels;
            int availableScreenWidth = screenWidth - (space*3);
            int itemWidth = (int) (availableScreenWidth * 0.5);

            int oneHeight = (int) (itemWidth/0.75);
            ViewGroup.LayoutParams params = threeSpearImageView.getLayoutParams();
            params.width = itemWidth;
            params.height = oneHeight;
            threeSpearImageView.setLayoutParams(params);
            threeSpearImageView.setDisplayOptions(DisplayOptionsType.HOT_STAR_ONE);

            params = twoSpearImageView.getLayoutParams();
            params.width = (int) (availableScreenWidth * 0.5);
            params.height = (oneHeight-space)/2;
            twoSpearImageView.setLayoutParams(params);
            twoSpearImageView.setDisplayOptions(DisplayOptionsType.HOT_STAR_TWO);

            params = oneSpearImageView.getLayoutParams();
            params.width = (int) (availableScreenWidth * 0.5);
            params.height = (oneHeight-space)/2;
            oneSpearImageView.setLayoutParams(params);
            oneSpearImageView.setDisplayOptions(DisplayOptionsType.HOT_STAR_TWO);
        }
    }

    private static class TwoItemHolder extends RecyclerView.ViewHolder{
        private SpearImageView oneSpearImageView;
        private SpearImageView twoSpearImageView;
        private TextView oneNameTextView;
        private TextView twoNameTextView;

        public TwoItemHolder(View itemView, View.OnClickListener onClickListener) {
            super(itemView);
            oneSpearImageView = (SpearImageView) itemView.findViewById(R.id.spearImage_hotStarTwoItem_one);
            twoSpearImageView = (SpearImageView) itemView.findViewById(R.id.spearImage_hotStarTwoItem_two);
            oneNameTextView = (TextView) itemView.findViewById(R.id.text_hotStarTwoItem_one);
            twoNameTextView = (TextView) itemView.findViewById(R.id.text_hotStarTwoItem_two);

            oneSpearImageView.setOnClickListener(onClickListener);
            twoSpearImageView.setOnClickListener(onClickListener);

            oneSpearImageView.setEnablePressRipple(true);
            twoSpearImageView.setEnablePressRipple(true);

            int space = (int) itemView.getResources().getDimension(R.dimen.home_category_margin_border);
            int screenWidth = itemView.getContext().getResources().getDisplayMetrics().widthPixels;
            int availableScreenWidth = screenWidth - (space*3);
            int itemWidth = (int) (availableScreenWidth * 0.5);

            int oneHeight = (int) (itemWidth/1.36);
            ViewGroup.LayoutParams params = oneSpearImageView.getLayoutParams();
            params.width = itemWidth;
            params.height = oneHeight;
            oneSpearImageView.setLayoutParams(params);
            oneSpearImageView.setDisplayOptions(DisplayOptionsType.HOT_STAR_THREE);

            params = twoSpearImageView.getLayoutParams();
            params.width = itemWidth;
            params.height = oneHeight;
            twoSpearImageView.setLayoutParams(params);
            twoSpearImageView.setDisplayOptions(DisplayOptionsType.HOT_STAR_THREE);
        }
    }

    private static class OneItemHolder extends RecyclerView.ViewHolder{
        private SpearImageView oneSpearImageView;
        private TextView oneNameTextView;

        public OneItemHolder(View itemView, View.OnClickListener onClickListener) {
            super(itemView);
            oneSpearImageView = (SpearImageView) itemView.findViewById(R.id.spearImage_hotStarOneItem_one);
            oneNameTextView = (TextView) itemView.findViewById(R.id.text_hotStarOneItem_one);

            oneSpearImageView.setOnClickListener(onClickListener);

            oneSpearImageView.setEnablePressRipple(true);

            int space = (int) itemView.getResources().getDimension(R.dimen.home_category_margin_border);
            int screenWidth = itemView.getContext().getResources().getDisplayMetrics().widthPixels;
            int itemWidth = screenWidth - (space*3);

            int oneHeight = (int) (itemWidth/1.36);
            ViewGroup.LayoutParams params = oneSpearImageView.getLayoutParams();
            params.width = itemWidth;
            params.height = oneHeight;
            oneSpearImageView.setLayoutParams(params);
            oneSpearImageView.setDisplayOptions(DisplayOptionsType.HOT_STAR_THREE);
        }
    }

    private static class CategoryTitleHolder extends RecyclerView.ViewHolder{
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

    private static class OneItem {
        private HotStarRequest.Star star1;
    }

    public interface OnImageClickListener{
        public void onClickImage(HotStarRequest.Star star);
    }
}
