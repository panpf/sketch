package me.xiaopan.android.spear.sample.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import me.xiaoapn.android.spear.sample.R;
import me.xiaopan.android.spear.display.ColorFadeInImageDisplayer;
import me.xiaopan.android.spear.request.DisplayListener;
import me.xiaopan.android.spear.request.DisplayOptions;
import me.xiaopan.android.spear.sample.net.request.SearchImageRequest;
import me.xiaopan.android.spear.util.FailureCause;
import me.xiaopan.android.spear.widget.SpearImageView;

/**
 * 新的图片适配器
 */
public class SearchImageAdapter extends RecyclerView.Adapter{
    private int imageSize = -1;
    private int screenWidth;
    private Context context;
    private OnItemClickListener onItemClickListener;
    private List<SearchImageRequest.Image> imageList;
    private OnLoadMoreListener onLoadMoreListener;
    private LayoutType layoutType;

    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public SearchImageAdapter(Context context, List<SearchImageRequest.Image> imageList, RecyclerView recyclerView, OnItemClickListener onItemClickListener){
        this.context = context;
        this.imageList = imageList;
        this.onItemClickListener = onItemClickListener;

        screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        int screenHeight = context.getResources().getDisplayMetrics().heightPixels;

        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if(layoutManager instanceof GridLayoutManager){
            layoutType = LayoutType.GRID;
            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            int maxSize = gridLayoutManager.getOrientation() == GridLayoutManager.VERTICAL?screenWidth:screenHeight;
            imageSize = maxSize/gridLayoutManager.getSpanCount();
        }else if(layoutManager instanceof StaggeredGridLayoutManager){
            layoutType = LayoutType.STAGGERED;
            StaggeredGridLayoutManager gridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
            int maxSize = gridLayoutManager.getOrientation() == GridLayoutManager.VERTICAL?screenWidth:screenHeight;
            imageSize = maxSize/gridLayoutManager.getSpanCount();
        }else{
            layoutType = LayoutType.LINEAR;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0){
            return 0;
        }else{
            return 1;
        }
    }

    @Override
    public int getItemCount() {
        return imageList!=null?imageList.size()+1:1;
    }

    public List<SearchImageRequest.Image> getImageList() {
        return imageList;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        switch(viewType){
            case 0 :
                FrameLayout layout = new FrameLayout(context);
                SpearImageView headerSpearImageView = new SpearImageView(context);
                headerSpearImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                headerSpearImageView.setLayoutParams(new FrameLayout.LayoutParams(screenWidth, (int) (screenWidth / 3.2f)));
                headerSpearImageView.setId(R.id.card_fiveItem_one);
                layout.addView(headerSpearImageView);
                viewHolder = new HeaderViewHolder(layout);
                break;
            case 1 :
                View view = LayoutInflater.from(context).inflate(R.layout.list_item_image2, viewGroup, false);
                if(layoutType == LayoutType.GRID && imageSize != -1){
                    View imageView = view.findViewById(R.id.image_item);
                    ViewGroup.LayoutParams params = imageView.getLayoutParams();
                    params.width = imageSize;
                    params.height = imageSize;
                    imageView.setLayoutParams(params);

                    View nameTextView = view.findViewById(R.id.text_item_name);
                    params = imageView.getLayoutParams();
                    params.width = imageSize;
                    nameTextView.setLayoutParams(params);
                }

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(onItemClickListener != null){
                            int position = (Integer) v.getTag();
                            if(position < imageList.size()){
                                SearchImageRequest.Image image = imageList.get(position);
                                onItemClickListener.onItemClick(position, image);
                            }
                        }
                    }
                });
                viewHolder = new MyViewHolder(view);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if(viewHolder instanceof HeaderViewHolder){
            ((HeaderViewHolder) viewHolder).imageView.setImageByUri("http://img0.bdstatic.com/img/image/yangmi.png");
        }else if(viewHolder instanceof MyViewHolder){
            position -= 1;
            SearchImageRequest.Image image = imageList.get(position);
            MyViewHolder myViewHolder = (MyViewHolder) viewHolder;

            if(layoutType == LayoutType.STAGGERED || layoutType == LayoutType.LINEAR){
                ViewGroup.LayoutParams params = myViewHolder.imageView.getLayoutParams();
                params.width = image.getWidth();
                params.height = image.getHeight();
                myViewHolder.imageView.setLayoutParams(params);

                params = myViewHolder.nameTextView.getLayoutParams();
                params.width = image.getWidth();
                myViewHolder.nameTextView.setLayoutParams(params);
            }

            viewHolder.itemView.setTag(position);
            myViewHolder.nameTextView.setText(image.getImageSizeStr());
            myViewHolder.imageView.setImageByUri(image.getSourceUrl());

            if(onLoadMoreListener != null && onLoadMoreListener.isEnable() && position == imageList.size() - 1){
                onLoadMoreListener.onLoadMore();
            }
        }
    }

    private static class MyViewHolder extends RecyclerView.ViewHolder{
        private SpearImageView imageView;
        private TextView nameTextView;

        public MyViewHolder(View itemView) {
            super(itemView);

            imageView = (SpearImageView) itemView.findViewById(R.id.image_item);
            DisplayOptions displayOptions = new DisplayOptions(itemView.getContext());
            displayOptions.displayer(new ColorFadeInImageDisplayer(Color.WHITE));
            imageView.setDisplayOptions(displayOptions);


            nameTextView = (TextView) itemView.findViewById(R.id.text_item_name);
            imageView.setDisplayListener(new DisplayListener() {
                @Override
                public void onStarted() {

                }

                @Override
                public void onCompleted(String uri, ImageView imageView, BitmapDrawable drawable, From from) {
                    String size = drawable.getIntrinsicWidth()+"x"+drawable.getIntrinsicHeight();
                    nameTextView.append("; "+size);
                }

                @Override
                public void onFailed(FailureCause failureCause) {

                }

                @Override
                public void onCanceled() {

                }
            });
        }
    }

    private static class HeaderViewHolder extends RecyclerView.ViewHolder{
        private SpearImageView imageView;

        public HeaderViewHolder(View itemView) {
            super(itemView);

            imageView = (SpearImageView) itemView.findViewById(R.id.card_fiveItem_one);
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
