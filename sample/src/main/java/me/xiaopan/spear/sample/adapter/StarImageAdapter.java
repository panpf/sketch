package me.xiaopan.spear.sample.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.xiaopan.spear.sample.OptionsType;
import me.xiaopan.spear.sample.R;
import me.xiaopan.spear.sample.net.request.StarImageRequest;
import me.xiaopan.spear.sample.widget.MyImageView;

/**
 * 明星图片适配器
 */
public class StarImageAdapter extends RecyclerView.Adapter{
    private static final int ITEM_TYPE_ITEM = 0;
    private static final int ITEM_TYPE_LOAD_MORE_FOOTER = 1;
    private static final int ITEM_TYPE_HEADER = 2;
    private int imageSize = -1;
    private int headerWidth;
    private int column = 3;
    private int marginBorder;
    private int margin;
    private Context context;
    private List<StarImageRequest.Image> imageList;
    private OnLoadMoreListener onLoadMoreListener;
    private String headerImageUrl;
    private View.OnClickListener itemClickListener;
    private List<String> imageUrlList;
    private LoadMoreFooterViewHolder loadMoreFooterViewHolder;

    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public StarImageAdapter(Context context, String headerImageUrl, List<StarImageRequest.Image> imageList, final OnItemClickListener onItemClickListener){
        this.context = context;
        this.headerImageUrl = headerImageUrl;
        this.imageList = imageList;
        this.imageUrlList = new ArrayList<>(imageList.size());
        for(StarImageRequest.Image image : imageList){
            imageUrlList.add(image.getSourceUrl());
        }

        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        margin = (int) context.getResources().getDimension(R.dimen.home_category_margin);
        marginBorder = (int) context.getResources().getDimension(R.dimen.home_category_margin_border);
        headerWidth = screenWidth - (marginBorder*2);
        int maxSize = screenWidth - (marginBorder * 4);
        imageSize = maxSize/column;

        itemClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener != null){
                    int position = (Integer) v.getTag();
                    if(position < StarImageAdapter.this.imageList.size()){
                        StarImageRequest.Image image = StarImageAdapter.this.imageList.get(position);
                        onItemClickListener.onItemClick(position, image);
                    }
                }
            }
        };
    }

    @Override
    public int getItemViewType(int position) {
        if(headerImageUrl != null && position == 0){
            return ITEM_TYPE_HEADER;
        }else if(onLoadMoreListener != null && position == getItemCount()-1){
            return ITEM_TYPE_LOAD_MORE_FOOTER;
        }else{
            return ITEM_TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        if(imageList == null){
            return 0;
        }
        return (imageList.size()%column!=0?(imageList.size()/column)+1:(imageList.size()/column))   // ITEM行数
                +(headerImageUrl !=null?1:0) // 加上头
                +(onLoadMoreListener!=null?1:0); // 加上尾巴
    }

    public int getDataSize(){
        return imageList.size();
    }

    public void append(List<StarImageRequest.Image> imageList) {
        this.imageList.addAll(imageList);
        for(StarImageRequest.Image image : imageList){
            imageUrlList.add(image.getSourceUrl());
        }
    }

    public List<String> getImageUrlList() {
        return imageUrlList;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        switch(viewType){
            case ITEM_TYPE_HEADER :
                HeadViewHolder headViewHolder = new HeadViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item_star_image_header, viewGroup, false));
                headViewHolder.imageView.setDisplayOptions(OptionsType.Rectangle);
                headViewHolder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                ViewGroup.LayoutParams headerParams = headViewHolder.imageView.getLayoutParams();
                headerParams.width = headerWidth;
                headerParams.height = (int) (headerWidth / 3.2f);
                headViewHolder.imageView.setLayoutParams(headerParams);
                viewHolder = headViewHolder;
                break;
            case ITEM_TYPE_ITEM :
                ItemViewHolder itemViewHolder = new ItemViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item_star_image, viewGroup, false));
                ViewGroup.LayoutParams itemParams = itemViewHolder.oneImageView.getLayoutParams();
                itemParams.width = imageSize;
                itemParams.height = imageSize;
                itemViewHolder.oneImageView.setLayoutParams(itemParams);

                itemParams = itemViewHolder.twoImageView.getLayoutParams();
                itemParams.width = imageSize;
                itemParams.height = imageSize;
                itemViewHolder.twoImageView.setLayoutParams(itemParams);

                itemParams = itemViewHolder.threeImageView.getLayoutParams();
                itemParams.width = imageSize;
                itemParams.height = imageSize;
                itemViewHolder.threeImageView.setLayoutParams(itemParams);

                itemViewHolder.oneImageView.setDisplayOptions(OptionsType.Rectangle);
                itemViewHolder.twoImageView.setDisplayOptions(OptionsType.Rectangle);
                itemViewHolder.threeImageView.setDisplayOptions(OptionsType.Rectangle);

                itemViewHolder.oneImageView.setOnClickListener(itemClickListener);
                itemViewHolder.twoImageView.setOnClickListener(itemClickListener);
                itemViewHolder.threeImageView.setOnClickListener(itemClickListener);
                viewHolder = itemViewHolder;
                break;
            case ITEM_TYPE_LOAD_MORE_FOOTER:
                loadMoreFooterViewHolder = new LoadMoreFooterViewHolder(LayoutInflater.from(context).inflate(R.layout.list_footer_load_more, viewGroup, false));
                viewHolder = loadMoreFooterViewHolder;
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch(getItemViewType(position)){
            case ITEM_TYPE_HEADER :
                HeadViewHolder headViewHolder = (HeadViewHolder) viewHolder;
                headViewHolder.imageView.displayImage(headerImageUrl);
                break;
            case ITEM_TYPE_ITEM :
                ItemViewHolder itemViewHolder = (ItemViewHolder) viewHolder;
                if(headerImageUrl != null){
                    position -= 1;
                }

                int topMargin;
                int bottomMargin;
                if(position == 0){
                    topMargin = headerImageUrl !=null?margin:marginBorder;
                    bottomMargin = margin;
                }else if(position == getItemCount()-1){
                    topMargin = margin;
                    bottomMargin = marginBorder;
                }else{
                    topMargin = margin;
                    bottomMargin = margin;
                }

                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) itemViewHolder.oneImageView.getLayoutParams();
                params.topMargin = topMargin;
                params.bottomMargin = bottomMargin;
                itemViewHolder.oneImageView.setLayoutParams(params);

                params = (ViewGroup.MarginLayoutParams) itemViewHolder.twoImageView.getLayoutParams();
                params.topMargin = topMargin;
                params.bottomMargin = bottomMargin;
                itemViewHolder.twoImageView.setLayoutParams(params);

                params = (ViewGroup.MarginLayoutParams) itemViewHolder.threeImageView.getLayoutParams();
                params.topMargin = topMargin;
                params.bottomMargin = bottomMargin;
                itemViewHolder.threeImageView.setLayoutParams(params);

                int oneReadPosition = (position*column);
                bind(itemViewHolder.oneImageView, oneReadPosition<imageList.size()?imageList.get(oneReadPosition):null, oneReadPosition);

                int twoReadPosition = (position*column)+1;
                bind(itemViewHolder.twoImageView, twoReadPosition<imageList.size()?imageList.get(twoReadPosition):null, twoReadPosition);

                int threeReadPosition = (position*column)+2;
                bind(itemViewHolder.threeImageView, threeReadPosition<imageList.size()?imageList.get(threeReadPosition):null, threeReadPosition);
                break;
            case ITEM_TYPE_LOAD_MORE_FOOTER :
                LoadMoreFooterViewHolder footerViewHolder = (LoadMoreFooterViewHolder) viewHolder;
                if(!onLoadMoreListener.isEnd()){
                    footerViewHolder.progressBar.setVisibility(View.VISIBLE);
                    footerViewHolder.contextTextView.setText("别着急，您的包裹马上就来！");
                    onLoadMoreListener.onLoadMore();
                }else{
                    footerViewHolder.progressBar.setVisibility(View.GONE);
                    footerViewHolder.contextTextView.setText("没有您的包裹了！");
                }
                break;
        }
    }

    private void bind(MyImageView imageView, StarImageRequest.Image image, int position){
        if(image != null){
            imageView.setTag(position);
            imageView.displayImage(image.getSourceUrl());
            imageView.setVisibility(View.VISIBLE);
        }else{
            imageView.setVisibility(View.INVISIBLE);
        }
    }

    public void loadMoreFail(){
        if(loadMoreFooterViewHolder != null){
            loadMoreFooterViewHolder.contextTextView.setText("Sorry！您的包裹运送失败，您再试一次吧！");
            loadMoreFooterViewHolder.progressBar.setVisibility(View.GONE);
        }
    }

    private static class ItemViewHolder extends RecyclerView.ViewHolder{
        private MyImageView oneImageView;
        private MyImageView twoImageView;
        private MyImageView threeImageView;

        @SuppressWarnings("deprecation")
        public ItemViewHolder(View itemView) {
            super(itemView);
            oneImageView = (MyImageView) itemView.findViewById(R.id.image_starImageItem_one);
            twoImageView = (MyImageView) itemView.findViewById(R.id.image_starImageItem_two);
            threeImageView = (MyImageView) itemView.findViewById(R.id.image_starImageItem_three);
        }
    }

    private static class HeadViewHolder extends RecyclerView.ViewHolder{
        private MyImageView imageView;

        public HeadViewHolder(View itemView) {
            super(itemView);
            imageView = (MyImageView) itemView.findViewById(R.id.image_starImageHeaderItem);
        }
    }

    private static class LoadMoreFooterViewHolder extends RecyclerView.ViewHolder{
        private ProgressBar progressBar;
        private TextView contextTextView;

        public LoadMoreFooterViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress_loadMoreFooter);
            contextTextView = (TextView) itemView.findViewById(R.id.text_loadMoreFooter_content);
        }
    }

    public interface OnItemClickListener{
        void onItemClick(int position, StarImageRequest.Image image);
    }

    public interface OnLoadMoreListener{
        boolean isEnd();
        void onLoadMore();
    }
}
