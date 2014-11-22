package me.xiaopan.android.spear.sample.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.util.List;

import me.xiaoapn.android.spear.sample.R;
import me.xiaopan.android.spear.sample.DisplayOptionsType;
import me.xiaopan.android.spear.sample.net.request.ImageSearchRequest;
import me.xiaopan.android.spear.widget.SpearImageView;

/**
 * 新的图片适配器
 */
public class ImageRecyclerAdapter extends RecyclerView.Adapter{
    private int imageSize = -1;
    private boolean staggeredLayout;
    private Context context;
    private OnItemClickListener onItemClickListener;
    private List<ImageSearchRequest.Image> imageList;
    private OnLoadMoreListener onLoadMoreListener;

    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public ImageRecyclerAdapter(Context context, List<ImageSearchRequest.Image> imageList, RecyclerView recyclerView, OnItemClickListener onItemClickListener){
        this.context = context;
        this.imageList = imageList;
        this.onItemClickListener = onItemClickListener;
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if(layoutManager instanceof GridLayoutManager){
            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = windowManager.getDefaultDisplay();
            int maxSize = 0;
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2){
                maxSize = gridLayoutManager.getOrientation() == GridLayoutManager.VERTICAL?display.getWidth():display.getHeight();
            }else{
                Point point = new Point();
                display.getSize(point);
                maxSize = gridLayoutManager.getOrientation() == GridLayoutManager.VERTICAL?point.x:point.y;
            }
            imageSize = maxSize/gridLayoutManager.getSpanCount();
        }else if(layoutManager instanceof StaggeredGridLayoutManager){
            staggeredLayout = true;
            StaggeredGridLayoutManager gridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = windowManager.getDefaultDisplay();
            int maxSize = 0;
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2){
                maxSize = gridLayoutManager.getOrientation() == GridLayoutManager.VERTICAL?display.getWidth():display.getHeight();
            }else{
                Point point = new Point();
                display.getSize(point);
                maxSize = gridLayoutManager.getOrientation() == GridLayoutManager.VERTICAL?point.x:point.y;
            }
            imageSize = maxSize/gridLayoutManager.getSpanCount();
        }
    }

    @Override
    public int getItemCount() {
        return imageList!=null?imageList.size():0;
    }

    public List<ImageSearchRequest.Image> getImageList() {
        return imageList;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_image, viewGroup, false);
        if(imageSize != -1){
            View imageView = view.findViewById(R.id.image_item);
            imageView.setLayoutParams(new CardView.LayoutParams(imageSize, imageSize));
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener != null){
                    int position = (Integer) v.getTag();
                    if(position < imageList.size()){
                        ImageSearchRequest.Image image = imageList.get(position);
                        onItemClickListener.onItemClick(position, image);
                    }
                }
            }
        });
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        ImageSearchRequest.Image image = imageList.get(i);
        MyViewHolder myViewHolder = (MyViewHolder) viewHolder;

        if(staggeredLayout){
            ViewGroup.LayoutParams params = myViewHolder.imageView.getLayoutParams();
            params.width = image.getWidth();
            params.height = image.getHeight();
            myViewHolder.imageView.setLayoutParams(params);
        }

        viewHolder.itemView.setTag(i);
        myViewHolder.imageView.setImageByUri(imageList.get(i).getSourceUrl());

        if(onLoadMoreListener != null && onLoadMoreListener.isEnable() && i == imageList.size() - 1){
            onLoadMoreListener.onLoadMore();
        }
    }

    private static class MyViewHolder extends RecyclerView.ViewHolder{
        private SpearImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);

            imageView = (SpearImageView) itemView.findViewById(R.id.image_item);
            imageView.setDisplayOptions(DisplayOptionsType.GRID_VIEW);
        }
    }

    public interface OnItemClickListener{
        public void onItemClick(int position, ImageSearchRequest.Image image);
    }

    public interface OnLoadMoreListener{
        public void setEnable(boolean enable);
        public boolean isEnable();
        public void onLoadMore();
    }
}
