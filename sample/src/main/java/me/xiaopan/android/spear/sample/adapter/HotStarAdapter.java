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
import me.xiaopan.android.spear.SpearImageView;
import me.xiaopan.android.spear.sample.util.Settings;

/**
 * 热门明星适配器
 */
public class HotStarAdapter extends RecyclerView.Adapter{
    private static final int ITEM_TYPE_CATEGORY_TITLE = 0;
    private static final int ITEM_TYPE_THREE_ITEM_LEFT = 1;
    private static final int ITEM_TYPE_THREE_ITEM_RIGHT = 2;
    private static final int ITEM_TYPE_TWO_ITEM = 3;
    private Context context;
    private List<Object> items;
    private int marginBorder;
    private int availableScreenWidth;
    private View.OnClickListener itemClickListener;
    private Settings settings;

    public HotStarAdapter(Context context, List<HotStarRequest.HotStar> hotStarList, final OnImageClickListener onImageClickListener) {
        this.context = context;
        this.settings = Settings.with(context);
        append(hotStarList);
        this.marginBorder = (int) context.getResources().getDimension(R.dimen.home_category_margin_border);
        this.availableScreenWidth = context.getResources().getDisplayMetrics().widthPixels - (marginBorder*2);
        this.itemClickListener = new View.OnClickListener() {
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
                TwoItem oneItem = new TwoItem();
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

    public void append(List<HotStarRequest.HotStar> hotStarList){
        if(items == null){
            items = new ArrayList<>();
        }
        for(HotStarRequest.HotStar hotStar : hotStarList){
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
        if(item instanceof String){
            return ITEM_TYPE_CATEGORY_TITLE;
        }else if(item instanceof ThreeItemLeft){
            return ITEM_TYPE_THREE_ITEM_LEFT;
        }else if(item instanceof ThreeItemRight){
            return ITEM_TYPE_THREE_ITEM_RIGHT;
        }else if(item instanceof TwoItem){
            return ITEM_TYPE_TWO_ITEM;
        }else{
            return -1;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        if(viewType == ITEM_TYPE_CATEGORY_TITLE){
            viewHolder = new CategoryTitleHolder(LayoutInflater.from(context).inflate(R.layout.list_item_title, parent, false));
        }else if(viewType == ITEM_TYPE_THREE_ITEM_LEFT){
            ThreeItemLeftHolder threeItemLeftHolder = new ThreeItemLeftHolder(LayoutInflater.from(context).inflate(R.layout.list_item_hot_star_three_left, parent, false));

            threeItemLeftHolder.oneSpearImageView.setOnClickListener(itemClickListener);
            threeItemLeftHolder.twoSpearImageView.setOnClickListener(itemClickListener);
            threeItemLeftHolder.threeSpearImageView.setOnClickListener(itemClickListener);

            threeItemLeftHolder.oneSpearImageView.setShowClickRipple(true);
            threeItemLeftHolder.twoSpearImageView.setShowClickRipple(true);
            threeItemLeftHolder.threeSpearImageView.setShowClickRipple(true);

            int itemWidth = (availableScreenWidth-marginBorder)/2;
            int itemHeight = (int) (itemWidth/0.75);

            ViewGroup.LayoutParams params = threeItemLeftHolder.oneSpearImageView.getLayoutParams();
            params.width = itemWidth;
            params.height = itemHeight;
            threeItemLeftHolder.oneSpearImageView.setLayoutParams(params);
            threeItemLeftHolder.oneSpearImageView.setDisplayOptions(DisplayOptionsType.Rectangle_0_75);

            params = threeItemLeftHolder.twoSpearImageView.getLayoutParams();
            params.width = itemWidth;
            params.height = (itemHeight-marginBorder)/2;
            threeItemLeftHolder.twoSpearImageView.setLayoutParams(params);
            threeItemLeftHolder.twoSpearImageView.setDisplayOptions(DisplayOptionsType.Rectangle_1_56);

            params = threeItemLeftHolder.threeSpearImageView.getLayoutParams();
            params.width = itemWidth;
            params.height = (itemHeight-marginBorder)/2;
            threeItemLeftHolder.threeSpearImageView.setLayoutParams(params);
            threeItemLeftHolder.threeSpearImageView.setDisplayOptions(DisplayOptionsType.Rectangle_1_56);

            viewHolder = threeItemLeftHolder;
        }else if(viewType == ITEM_TYPE_THREE_ITEM_RIGHT){
            ThreeItemRightHolder threeItemRightHolder = new ThreeItemRightHolder(LayoutInflater.from(context).inflate(R.layout.list_item_hot_star_three_right, parent, false));

            threeItemRightHolder.oneSpearImageView.setOnClickListener(itemClickListener);
            threeItemRightHolder.twoSpearImageView.setOnClickListener(itemClickListener);
            threeItemRightHolder.threeSpearImageView.setOnClickListener(itemClickListener);

            threeItemRightHolder.oneSpearImageView.setShowClickRipple(true);
            threeItemRightHolder.twoSpearImageView.setShowClickRipple(true);
            threeItemRightHolder.threeSpearImageView.setShowClickRipple(true);

            int itemWidth = (availableScreenWidth-marginBorder) / 2;
            int itemHeight = (int) (itemWidth/0.75);

            ViewGroup.LayoutParams params = threeItemRightHolder.threeSpearImageView.getLayoutParams();
            params.width = itemWidth;
            params.height = itemHeight;
            threeItemRightHolder.threeSpearImageView.setLayoutParams(params);
            threeItemRightHolder.threeSpearImageView.setDisplayOptions(DisplayOptionsType.Rectangle_0_75);

            params = threeItemRightHolder.twoSpearImageView.getLayoutParams();
            params.width = itemWidth;
            params.height = (itemHeight-marginBorder)/2;
            threeItemRightHolder.twoSpearImageView.setLayoutParams(params);
            threeItemRightHolder.twoSpearImageView.setDisplayOptions(DisplayOptionsType.Rectangle_1_56);

            params = threeItemRightHolder.oneSpearImageView.getLayoutParams();
            params.width = itemWidth;
            params.height = (itemHeight-marginBorder)/2;
            threeItemRightHolder.oneSpearImageView.setLayoutParams(params);
            threeItemRightHolder.oneSpearImageView.setDisplayOptions(DisplayOptionsType.Rectangle_1_56);

            viewHolder = threeItemRightHolder;
        }else if(viewType == ITEM_TYPE_TWO_ITEM){
            TwoItemHolder twoItemHolder = new TwoItemHolder(LayoutInflater.from(context).inflate(R.layout.list_item_hot_star_two, parent, false));

            twoItemHolder.oneSpearImageView.setOnClickListener(itemClickListener);
            twoItemHolder.twoSpearImageView.setOnClickListener(itemClickListener);

            twoItemHolder.oneSpearImageView.setShowClickRipple(true);
            twoItemHolder.twoSpearImageView.setShowClickRipple(true);

            int itemWidth = (availableScreenWidth-marginBorder) / 2;
            int itemHeight = (int) (itemWidth/0.75);

            ViewGroup.LayoutParams params = twoItemHolder.oneSpearImageView.getLayoutParams();
            params.width = itemWidth;
            params.height = (itemHeight-marginBorder)/2;
            twoItemHolder.oneSpearImageView.setLayoutParams(params);
            twoItemHolder.oneSpearImageView.setDisplayOptions(DisplayOptionsType.Rectangle_1_56);

            params = twoItemHolder.twoSpearImageView.getLayoutParams();
            params.width = itemWidth;
            params.height = (itemHeight-marginBorder)/2;
            twoItemHolder.twoSpearImageView.setLayoutParams(params);
            twoItemHolder.twoSpearImageView.setDisplayOptions(DisplayOptionsType.Rectangle_1_56);

            viewHolder = twoItemHolder;
        }else{
            return null;
        }
        return viewHolder;
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

            threeItemLeftHolder.oneSpearImageView.setTag(threeItemLeft.star1);
            threeItemLeftHolder.twoSpearImageView.setTag(threeItemLeft.star2);
            threeItemLeftHolder.threeSpearImageView.setTag(threeItemLeft.star3);

            threeItemLeftHolder.oneSpearImageView.setShowDownloadProgress(settings.isShowImageDownloadProgress());
            threeItemLeftHolder.twoSpearImageView.setShowDownloadProgress(settings.isShowImageDownloadProgress());
            threeItemLeftHolder.threeSpearImageView.setShowDownloadProgress(settings.isShowImageDownloadProgress());

            threeItemLeftHolder.oneSpearImageView.setShowFromFlag(settings.isShowImageFromFlag());
            threeItemLeftHolder.twoSpearImageView.setShowFromFlag(settings.isShowImageFromFlag());
            threeItemLeftHolder.threeSpearImageView.setShowFromFlag(settings.isShowImageFromFlag());

            threeItemLeftHolder.oneSpearImageView.setClickRedisplayOnPauseDownload(settings.isClickDisplayOnPauseDownload());
            threeItemLeftHolder.twoSpearImageView.setClickRedisplayOnPauseDownload(settings.isClickDisplayOnPauseDownload());
            threeItemLeftHolder.threeSpearImageView.setClickRedisplayOnPauseDownload(settings.isClickDisplayOnPauseDownload());

            threeItemLeftHolder.oneSpearImageView.setClickRedisplayOnFailed(settings.isClickDisplayOnFailed());
            threeItemLeftHolder.twoSpearImageView.setClickRedisplayOnFailed(settings.isClickDisplayOnFailed());
            threeItemLeftHolder.threeSpearImageView.setClickRedisplayOnFailed(settings.isClickDisplayOnFailed());

            threeItemLeftHolder.oneSpearImageView.displayImage(threeItemLeft.star1.getHeightImage().getUrl());
            threeItemLeftHolder.twoSpearImageView.displayImage(threeItemLeft.star2.getWidthImage().getUrl());
            threeItemLeftHolder.threeSpearImageView.displayImage(threeItemLeft.star3.getWidthImage().getUrl());
        }else if(holder instanceof ThreeItemRightHolder){
            ThreeItemRightHolder threeItemRightHolder = (ThreeItemRightHolder) holder;
            ThreeItemRight threeItemRight = (ThreeItemRight) items.get(position);

            threeItemRightHolder.oneNameTextView.setText(threeItemRight.star1.getName());
            threeItemRightHolder.twoNameTextView.setText(threeItemRight.star2.getName());
            threeItemRightHolder.threeNameTextView.setText(threeItemRight.star3.getName());

            threeItemRightHolder.oneSpearImageView.setTag(threeItemRight.star1);
            threeItemRightHolder.twoSpearImageView.setTag(threeItemRight.star2);
            threeItemRightHolder.threeSpearImageView.setTag(threeItemRight.star3);

            threeItemRightHolder.oneSpearImageView.setShowDownloadProgress(settings.isShowImageDownloadProgress());
            threeItemRightHolder.twoSpearImageView.setShowDownloadProgress(settings.isShowImageDownloadProgress());
            threeItemRightHolder.threeSpearImageView.setShowDownloadProgress(settings.isShowImageDownloadProgress());

            threeItemRightHolder.oneSpearImageView.setShowFromFlag(settings.isShowImageFromFlag());
            threeItemRightHolder.twoSpearImageView.setShowFromFlag(settings.isShowImageFromFlag());
            threeItemRightHolder.threeSpearImageView.setShowFromFlag(settings.isShowImageFromFlag());

            threeItemRightHolder.oneSpearImageView.setClickRedisplayOnPauseDownload(settings.isClickDisplayOnPauseDownload());
            threeItemRightHolder.twoSpearImageView.setClickRedisplayOnPauseDownload(settings.isClickDisplayOnPauseDownload());
            threeItemRightHolder.threeSpearImageView.setClickRedisplayOnPauseDownload(settings.isClickDisplayOnPauseDownload());

            threeItemRightHolder.oneSpearImageView.setClickRedisplayOnFailed(settings.isClickDisplayOnFailed());
            threeItemRightHolder.twoSpearImageView.setClickRedisplayOnFailed(settings.isClickDisplayOnFailed());
            threeItemRightHolder.threeSpearImageView.setClickRedisplayOnFailed(settings.isClickDisplayOnFailed());

            threeItemRightHolder.oneSpearImageView.displayImage(threeItemRight.star1.getWidthImage().getUrl());
            threeItemRightHolder.twoSpearImageView.displayImage(threeItemRight.star2.getWidthImage().getUrl());
            threeItemRightHolder.threeSpearImageView.displayImage(threeItemRight.star3.getHeightImage().getUrl());
        }else if(holder instanceof TwoItemHolder){
            TwoItemHolder twoItemHolder = (TwoItemHolder) holder;
            TwoItem twoItem = (TwoItem) items.get(position);

            twoItemHolder.oneNameTextView.setText(twoItem.star1.getName());
            twoItemHolder.oneSpearImageView.setTag(twoItem.star1);
            twoItemHolder.oneSpearImageView.setShowDownloadProgress(settings.isShowImageDownloadProgress());
            twoItemHolder.oneSpearImageView.setShowFromFlag(settings.isShowImageFromFlag());
            twoItemHolder.oneSpearImageView.setClickRedisplayOnPauseDownload(settings.isClickDisplayOnPauseDownload());
            twoItemHolder.oneSpearImageView.setClickRedisplayOnFailed(settings.isClickDisplayOnFailed());
            twoItemHolder.oneSpearImageView.displayImage(twoItem.star1.getWidthImage().getUrl());


            if(twoItem.star2 != null){
                twoItemHolder.twoNameTextView.setText(twoItem.star2.getName());
                twoItemHolder.twoSpearImageView.setTag(twoItem.star2);
                twoItemHolder.twoSpearImageView.setClickRedisplayOnPauseDownload(settings.isClickDisplayOnPauseDownload());
                twoItemHolder.twoSpearImageView.setClickRedisplayOnFailed(settings.isClickDisplayOnFailed());
                twoItemHolder.twoSpearImageView.setShowDownloadProgress(settings.isShowImageDownloadProgress());
                twoItemHolder.twoSpearImageView.setShowFromFlag(settings.isShowImageFromFlag());
                twoItemHolder.twoSpearImageView.displayImage(twoItem.star2.getWidthImage().getUrl());

                twoItemHolder.twoNameTextView.setVisibility(View.VISIBLE);
                twoItemHolder.twoSpearImageView.setVisibility(View.VISIBLE);
            }else{
                twoItemHolder.twoNameTextView.setVisibility(View.INVISIBLE);
                twoItemHolder.twoSpearImageView.setVisibility(View.INVISIBLE);
            }
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

        public ThreeItemLeftHolder(View itemView) {
            super(itemView);
            oneSpearImageView = (SpearImageView) itemView.findViewById(R.id.spearImage_hotStarThreeLeftItem_one);
            twoSpearImageView = (SpearImageView) itemView.findViewById(R.id.spearImage_hotStarThreeLeftItem_two);
            threeSpearImageView = (SpearImageView) itemView.findViewById(R.id.spearImage_hotStarThreeLeftItem_three);
            oneNameTextView = (TextView) itemView.findViewById(R.id.text_hotStarThreeLeftItem_one);
            twoNameTextView = (TextView) itemView.findViewById(R.id.text_hotStarThreeLeftItem_two);
            threeNameTextView = (TextView) itemView.findViewById(R.id.text_hotStarThreeLeftItem_three);
        }
    }

    private static class ThreeItemRightHolder extends RecyclerView.ViewHolder{
        private SpearImageView oneSpearImageView;
        private SpearImageView twoSpearImageView;
        private SpearImageView threeSpearImageView;
        private TextView oneNameTextView;
        private TextView twoNameTextView;
        private TextView threeNameTextView;

        public ThreeItemRightHolder(View itemView) {
            super(itemView);
            oneSpearImageView = (SpearImageView) itemView.findViewById(R.id.spearImage_hotStarThreeRightItem_one);
            twoSpearImageView = (SpearImageView) itemView.findViewById(R.id.spearImage_hotStarThreeRightItem_two);
            threeSpearImageView = (SpearImageView) itemView.findViewById(R.id.spearImage_hotStarThreeRightItem_three);
            oneNameTextView = (TextView) itemView.findViewById(R.id.text_hotStarThreeRightItem_one);
            twoNameTextView = (TextView) itemView.findViewById(R.id.text_hotStarThreeRightItem_two);
            threeNameTextView = (TextView) itemView.findViewById(R.id.text_hotStarThreeRightItem_three);
        }
    }

    private static class TwoItemHolder extends RecyclerView.ViewHolder{
        private SpearImageView oneSpearImageView;
        private SpearImageView twoSpearImageView;
        private TextView oneNameTextView;
        private TextView twoNameTextView;

        public TwoItemHolder(View itemView) {
            super(itemView);
            oneSpearImageView = (SpearImageView) itemView.findViewById(R.id.spearImage_hotStarTwoItem_one);
            twoSpearImageView = (SpearImageView) itemView.findViewById(R.id.spearImage_hotStarTwoItem_two);
            oneNameTextView = (TextView) itemView.findViewById(R.id.text_hotStarTwoItem_one);
            twoNameTextView = (TextView) itemView.findViewById(R.id.text_hotStarTwoItem_two);
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

    public interface OnImageClickListener{
        void onClickImage(HotStarRequest.Star star);
    }
}
