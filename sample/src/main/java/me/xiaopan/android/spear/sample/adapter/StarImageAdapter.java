package me.xiaopan.android.spear.sample.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.List;

import me.xiaoapn.android.spear.sample.R;
import me.xiaopan.android.spear.sample.DisplayOptionsType;
import me.xiaopan.android.spear.sample.net.request.StarImageRequest;
import me.xiaopan.android.spear.widget.SpearImageView;

/**
 * 明星图片适配器
 */
public class StarImageAdapter extends RecyclerView.Adapter{
    private static final int ITEM_TYPE_ITEM = 0;
    private static final int ITEM_TYPE_HEADER = 1;
    private int imageSize = -1;
    private int screenWidth;
    private int column = 3;
    private int marginBorder;
    private int margin;
    private Context context;
    private OnItemClickListener onItemClickListener;
    private List<StarImageRequest.Image> imageList;
    private OnLoadMoreListener onLoadMoreListener;
    private String backgroundImageUrl;

    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public StarImageAdapter(Context context, String backgroundImageUrl, List<StarImageRequest.Image> imageList, OnItemClickListener onItemClickListener){
        this.context = context;
        this.backgroundImageUrl = backgroundImageUrl;
        this.imageList = imageList;
        this.onItemClickListener = onItemClickListener;

        screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        margin = (int) context.getResources().getDimension(R.dimen.home_category_margin);
        marginBorder = (int) context.getResources().getDimension(R.dimen.home_category_margin_border);
        int maxSize = screenWidth - (marginBorder * 4);
        imageSize = maxSize/column;
    }

    @Override
    public int getItemViewType(int position) {
        if(backgroundImageUrl == null){
            return ITEM_TYPE_ITEM;
        }else{
            return position==0?ITEM_TYPE_HEADER:ITEM_TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        if(backgroundImageUrl == null){
            return imageList!=null?(imageList.size()%column!=0?(imageList.size()/column):(imageList.size()/column)):0;
        }else{
            return imageList!=null?(imageList.size()%column!=0?(imageList.size()/column)+1:(imageList.size()/column))+1:1;
        }
    }

    public List<StarImageRequest.Image> getImageList() {
        return imageList;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        switch(viewType){
            case ITEM_TYPE_HEADER :
                viewHolder = new HeaderViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item_star_image_header, viewGroup, false), this);
                break;
            case ITEM_TYPE_ITEM :
                viewHolder = new ItemViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item_star_image, viewGroup, false), this);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch(getItemViewType(position)){
            case ITEM_TYPE_HEADER :
                ((HeaderViewHolder) viewHolder).bindData(position);
                break;
            case ITEM_TYPE_ITEM :
                ((ItemViewHolder) viewHolder).bindData(position);
            break;
        }

        if(onLoadMoreListener != null && onLoadMoreListener.isEnable() && position == getItemCount() - 1){
            onLoadMoreListener.onLoadMore();
        }
    }

    private static class ItemViewHolder extends RecyclerView.ViewHolder{
        private SpearImageView oneSpearImageView;
        private SpearImageView twoSpearImageView;
        private SpearImageView threeSpearImageView;

        private StarImageAdapter adapter;

        @SuppressWarnings("deprecation")
        public ItemViewHolder(View itemView, final StarImageAdapter adapter) {
            super(itemView);
            this.adapter = adapter;

            oneSpearImageView = (SpearImageView) itemView.findViewById(R.id.image_starImageItem_one);
            twoSpearImageView = (SpearImageView) itemView.findViewById(R.id.image_starImageItem_two);
            threeSpearImageView = (SpearImageView) itemView.findViewById(R.id.image_starImageItem_three);

            ViewGroup.LayoutParams params = oneSpearImageView.getLayoutParams();
            params.width = adapter.imageSize;
            params.height = adapter.imageSize;
            oneSpearImageView.setLayoutParams(params);

            params = twoSpearImageView.getLayoutParams();
            params.width = adapter.imageSize;
            params.height = adapter.imageSize;
            twoSpearImageView.setLayoutParams(params);

            params = threeSpearImageView.getLayoutParams();
            params.width = adapter.imageSize;
            params.height = adapter.imageSize;
            threeSpearImageView.setLayoutParams(params);

            oneSpearImageView.setDisplayOptions(DisplayOptionsType.STAR_HOME_ITEM);
            twoSpearImageView.setDisplayOptions(DisplayOptionsType.STAR_HOME_ITEM);
            threeSpearImageView.setDisplayOptions(DisplayOptionsType.STAR_HOME_ITEM);

            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(adapter.onItemClickListener != null){
                        int position = (Integer) v.getTag();
                        if(position < adapter.imageList.size()){
                            StarImageRequest.Image image = adapter.imageList.get(position);
                            adapter.onItemClickListener.onItemClick(position, image);
                        }
                    }
                }
            };

            oneSpearImageView.setOnClickListener(onClickListener);
            twoSpearImageView.setOnClickListener(onClickListener);
            threeSpearImageView.setOnClickListener(onClickListener);
        }

        public void bindData(int position){
            if(adapter.backgroundImageUrl != null){
                position -= 1;
            }

            int topMargin;
            int bottomMargin;
            if(position == 0){
                topMargin = adapter.backgroundImageUrl!=null?0:adapter.marginBorder;
                bottomMargin = adapter.margin;
            }else if(position == adapter.getItemCount()-1){
                topMargin = adapter.margin;
                bottomMargin = adapter.marginBorder;
            }else{
                topMargin = adapter.margin;
                bottomMargin = adapter.margin;
            }

            int oneReadPosition = (position*adapter.column);
            bind(oneSpearImageView, oneReadPosition<adapter.imageList.size()?adapter.imageList.get(oneReadPosition):null, oneReadPosition);

            int twoReadPosition = (position*adapter.column)+1;
            bind(twoSpearImageView, twoReadPosition<adapter.imageList.size()?adapter.imageList.get(twoReadPosition):null, twoReadPosition);

            int threeReadPosition = (position*adapter.column)+2;
            bind(threeSpearImageView, threeReadPosition<adapter.imageList.size()?adapter.imageList.get(threeReadPosition):null, threeReadPosition);


            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) oneSpearImageView.getLayoutParams();
            params.topMargin = topMargin;
            params.bottomMargin = bottomMargin;
            oneSpearImageView.setLayoutParams(params);

            params = (ViewGroup.MarginLayoutParams) twoSpearImageView.getLayoutParams();
            params.topMargin = topMargin;
            params.bottomMargin = bottomMargin;
            twoSpearImageView.setLayoutParams(params);

            params = (ViewGroup.MarginLayoutParams) threeSpearImageView.getLayoutParams();
            params.topMargin = topMargin;
            params.bottomMargin = bottomMargin;
            threeSpearImageView.setLayoutParams(params);
        }

        private void bind(SpearImageView spearImageView, StarImageRequest.Image image, int position){
            if(image != null){
                spearImageView.setTag(position);
                spearImageView.setImageByUri(image.getSourceUrl());
                spearImageView.setVisibility(View.VISIBLE);
            }else{
                spearImageView.setVisibility(View.INVISIBLE);
            }
        }
    }

    private static class HeaderViewHolder extends RecyclerView.ViewHolder{
        private SpearImageView spearImageView;
        private StarImageAdapter adapter;

        public HeaderViewHolder(View itemView, StarImageAdapter adapter) {
            super(itemView);

            this.adapter = adapter;

            spearImageView = (SpearImageView) itemView.findViewById(R.id.image_starImageHeaderItem);
            spearImageView.setDisplayOptions(DisplayOptionsType.STAR_HOME_HEADER);
            spearImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            spearImageView.setLayoutParams(new FrameLayout.LayoutParams(adapter.screenWidth, (int) (adapter.screenWidth / 3.2f)));
        }

        public void bindData(int position){
            spearImageView.setImageByUri(adapter.backgroundImageUrl);
        }
    }

    public interface OnItemClickListener{
        public void onItemClick(int position, StarImageRequest.Image image);
    }

    public interface OnLoadMoreListener{
        public void setEnable(boolean enable);
        public boolean isEnable();
        public void onLoadMore();
    }
}
