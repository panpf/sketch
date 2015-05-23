package me.xiaopan.sketchsample.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import me.xiaopan.sketchsample.OptionsType;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.util.DeviceUtils;
import me.xiaopan.sketchsample.widget.MyImageView;

public class PhotoAlbumImageAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<String> imageUris;
    private int itemWidth = -1;
    private View.OnClickListener itemClickListener;
    private int spanCount = -1;
    private int borderMargin;
    private int middleMargin;

    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public PhotoAlbumImageAdapter(final Context context, final List<String> imageUris, final OnImageClickListener onImageClickListener, final RecyclerView recyclerView){
        this.context = context;
        this.imageUris = imageUris;
        this.itemClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onImageClickListener != null && v.getTag() != null && v.getTag() instanceof ItemViewHolder){
                    onImageClickListener.onImageClick(((ItemViewHolder) v.getTag()).getPosition());
                }
            }
        };

        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if(layoutManager instanceof GridLayoutManager){
            spanCount = ((GridLayoutManager) recyclerView.getLayoutManager()).getSpanCount();
        }else if(layoutManager instanceof StaggeredGridLayoutManager){
            spanCount = ((StaggeredGridLayoutManager) recyclerView.getLayoutManager()).getSpanCount();
        }
        if(spanCount != -1){
            borderMargin = DeviceUtils.dp2px(context, 8);
            middleMargin = DeviceUtils.dp2px(context, 4);
            int maxScreenWidth = context.getResources().getDisplayMetrics().widthPixels - ((borderMargin * (spanCount+1)));
            itemWidth = maxScreenWidth/spanCount;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return imageUris!=null?imageUris.size():0;
    }

    public List<String> getImageUrlList() {
        return imageUris;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemViewHolder itemViewHolder = new ItemViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item_photo_album_image, parent, false));

        itemViewHolder.sketchImageView.setOnClickListener(itemClickListener);
        itemViewHolder.sketchImageView.setDisplayOptions(OptionsType.RECT);
        if(itemWidth != -1){
            ViewGroup.LayoutParams layoutParams = itemViewHolder.sketchImageView.getLayoutParams();
            layoutParams.width = itemWidth;
            layoutParams.height = itemWidth;
            itemViewHolder.sketchImageView.setLayoutParams(layoutParams);
        }

        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;

        if(spanCount != -1){
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) itemViewHolder.sketchImageView.getLayoutParams();
            int remainder = position % spanCount;
            if(remainder == 0){
                marginLayoutParams.leftMargin = borderMargin;
                marginLayoutParams.rightMargin = middleMargin;
            }else if(remainder == spanCount-1){
                marginLayoutParams.leftMargin = middleMargin;
                marginLayoutParams.rightMargin = borderMargin;
            }else{
                marginLayoutParams.leftMargin = middleMargin;
                marginLayoutParams.rightMargin = middleMargin;
            }

            if(position < spanCount){
                marginLayoutParams.topMargin = borderMargin;
                marginLayoutParams.bottomMargin = middleMargin;
            }else if(position >= getItemCount() - 1 - (remainder == 0?spanCount:remainder)){
                marginLayoutParams.topMargin = middleMargin;
                marginLayoutParams.bottomMargin = borderMargin;
            }else{
                marginLayoutParams.topMargin = middleMargin;
                marginLayoutParams.bottomMargin = middleMargin;
            }
            itemViewHolder.sketchImageView.setLayoutParams(marginLayoutParams);
        }

        itemViewHolder.sketchImageView.displayImage(imageUris.get(position));
        itemViewHolder.sketchImageView.setTag(itemViewHolder);
    }

    private static class ItemViewHolder extends RecyclerView.ViewHolder{
        private MyImageView sketchImageView;
        public ItemViewHolder(View itemView) {
            super(itemView);

            this.sketchImageView = (MyImageView) itemView.findViewById(R.id.image_photoAlbumImageItem_one);
        }
    }

    public interface OnImageClickListener{
        void onImageClick(int position);
    }
}
