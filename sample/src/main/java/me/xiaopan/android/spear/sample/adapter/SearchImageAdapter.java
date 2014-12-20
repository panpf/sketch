package me.xiaopan.android.spear.sample.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import me.xiaoapn.android.spear.sample.R;
import me.xiaopan.android.spear.sample.DisplayOptionsType;
import me.xiaopan.android.spear.sample.net.request.SearchImageRequest;
import me.xiaopan.android.spear.widget.SpearImageView;

/**
 * 新的图片适配器
 */
public class SearchImageAdapter extends RecyclerView.Adapter{
    private int itemSize = -1;
    private int columns = -1;
    private Context context;
    private OnItemClickListener onItemClickListener;
    private List<SearchImageRequest.Image> imageList;
    private OnLoadMoreListener onLoadMoreListener;
    private LayoutType layoutType;
    private boolean vertical;

    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public SearchImageAdapter(Context context, List<SearchImageRequest.Image> imageList, RecyclerView recyclerView, OnItemClickListener onItemClickListener){
        this.context = context;
        this.imageList = imageList;
        this.onItemClickListener = onItemClickListener;

        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if(layoutManager instanceof GridLayoutManager){
            layoutType = LayoutType.GRID;
            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            vertical = gridLayoutManager.getOrientation() == GridLayoutManager.VERTICAL;
            int maxSize = vertical?context.getResources().getDisplayMetrics().widthPixels:context.getResources().getDisplayMetrics().heightPixels;
            columns = gridLayoutManager.getSpanCount();
            maxSize -= dp2px(context, 8) * columns * 2;
            itemSize = maxSize / columns;
        }else if(layoutManager instanceof StaggeredGridLayoutManager){
            layoutType = LayoutType.STAGGERED;
            StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
            vertical = staggeredGridLayoutManager.getOrientation() == StaggeredGridLayoutManager.VERTICAL;
            int maxSize = vertical?context.getResources().getDisplayMetrics().widthPixels:context.getResources().getDisplayMetrics().heightPixels;
            columns = staggeredGridLayoutManager.getSpanCount();
            maxSize -= dp2px(context, 8) * columns * 2;
            itemSize = maxSize / columns;
        }else{
            layoutType = LayoutType.LINEAR;
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            vertical = linearLayoutManager.getOrientation() == LinearLayoutManager.VERTICAL;
            int maxSize = vertical?context.getResources().getDisplayMetrics().widthPixels:context.getResources().getDisplayMetrics().heightPixels;
            maxSize -= dp2px(context, 8) * 2;
            itemSize = maxSize;
        }
    }

    /**
     * dp单位转换为px
     * @param context 上下文，需要通过上下文获取到当前屏幕的像素密度
     * @param dpValue dp值
     * @return px值
     */
    private int dp2px(Context context, float dpValue){
        return (int)(dpValue * (context.getResources().getDisplayMetrics().density) + 0.5f);
    }

    @Override
    public int getItemCount() {
        return imageList!=null?imageList.size():0;
    }

    public List<SearchImageRequest.Image> getImageList() {
        return imageList;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        MyViewHolder viewHolder = new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item_search_image, viewGroup, false));

        if(layoutType == LayoutType.GRID && itemSize != -1){
            ViewGroup.LayoutParams params = viewHolder.spearImageView.getLayoutParams();
            params.width = itemSize;
            params.height = itemSize;
            viewHolder.spearImageView.setLayoutParams(params);
            viewHolder.spearImageView.setDisplayOptions(DisplayOptionsType.SEARCH_ITEM_GRID);
        }else if(layoutType == LayoutType.STAGGERED){
            viewHolder.spearImageView.setDisplayOptions(DisplayOptionsType.SEARCH_ITEM_STAGGERED);
        }else{
            viewHolder.spearImageView.setDisplayOptions(DisplayOptionsType.SEARCH_ITEM_LINEAR);
        }

        viewHolder.spearImageView.setEnablePressRipple(true);
        viewHolder.spearImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) v.getTag();
                    int position = viewHolder.getPosition();
                    if (position < imageList.size()) {
                        SearchImageRequest.Image image = imageList.get(position);
                        onItemClickListener.onItemClick(position, image);
                    }
                }
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        SearchImageRequest.Image image = imageList.get(position);
        MyViewHolder myViewHolder = (MyViewHolder) viewHolder;

        if(layoutType == LayoutType.STAGGERED){
            ViewGroup.LayoutParams params = myViewHolder.spearImageView.getLayoutParams();
            if(vertical){
                params.width = itemSize;
                params.height = (int) (image.getHeight()/(((float)image.getWidth()/itemSize)));
            }else{
                params.width = (int) (image.getWidth()/(((float)image.getHeight()/itemSize)));
                params.height = itemSize;
            }
            myViewHolder.spearImageView.setLayoutParams(params);
        }else if(layoutType == LayoutType.LINEAR){
            ViewGroup.LayoutParams params = myViewHolder.spearImageView.getLayoutParams();
            if(vertical){
                params.width = itemSize;
                params.height = (int) (image.getHeight()/(((float)image.getWidth()/itemSize)));
            }else{
                params.width = (int) (image.getWidth()/(((float)image.getHeight()/itemSize)));
                params.height = itemSize;
            }
            myViewHolder.spearImageView.setLayoutParams(params);
        }

        viewHolder.itemView.setTag(viewHolder);
        myViewHolder.spearImageView.setImageByUri(image.getSourceUrl());

        if(onLoadMoreListener != null && onLoadMoreListener.isEnable() && position == imageList.size() - 1){
            onLoadMoreListener.onLoadMore();
        }
    }

    private static class MyViewHolder extends RecyclerView.ViewHolder{
        private SpearImageView spearImageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            spearImageView = (SpearImageView) itemView.findViewById(R.id.image_searchImageItem);
        }
    }

    public interface OnItemClickListener{
        public void onItemClick(int position, SearchImageRequest.Image image);
    }

    public interface OnLoadMoreListener{
        public void setEnable(boolean enable);
        public boolean isEnable();
        public void onLoadMore();
    }

    private enum LayoutType{
        LINEAR,
        GRID,
        STAGGERED,
    }
}
