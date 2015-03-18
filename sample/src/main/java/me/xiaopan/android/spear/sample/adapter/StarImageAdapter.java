package me.xiaopan.android.spear.sample.adapter;

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

import me.xiaoapn.android.spear.sample.R;
import me.xiaopan.android.spear.sample.DisplayOptionsType;
import me.xiaopan.android.spear.sample.net.request.StarImageRequest;
import me.xiaopan.android.spear.SpearImageView;

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
                HeaderViewHolder headerViewHolder = new HeaderViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item_star_image_header, viewGroup, false));
                headerViewHolder.spearImageView.setDisplayOptions(DisplayOptionsType.STAR_HOME_HEADER);
                headerViewHolder.spearImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                ViewGroup.LayoutParams headerParams = headerViewHolder.spearImageView.getLayoutParams();
                headerParams.width = headerWidth;
                headerParams.height = (int) (headerWidth / 3.2f);
                headerViewHolder.spearImageView.setLayoutParams(headerParams);
                viewHolder = headerViewHolder;
                break;
            case ITEM_TYPE_ITEM :
                ItemViewHolder itemViewHolder = new ItemViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item_star_image, viewGroup, false));
                ViewGroup.LayoutParams itemParams = itemViewHolder.oneSpearImageView.getLayoutParams();
                itemParams.width = imageSize;
                itemParams.height = imageSize;
                itemViewHolder.oneSpearImageView.setLayoutParams(itemParams);

                itemParams = itemViewHolder.twoSpearImageView.getLayoutParams();
                itemParams.width = imageSize;
                itemParams.height = imageSize;
                itemViewHolder.twoSpearImageView.setLayoutParams(itemParams);

                itemParams = itemViewHolder.threeSpearImageView.getLayoutParams();
                itemParams.width = imageSize;
                itemParams.height = imageSize;
                itemViewHolder.threeSpearImageView.setLayoutParams(itemParams);

                itemViewHolder.oneSpearImageView.setDisplayOptions(DisplayOptionsType.STAR_HOME_ITEM);
                itemViewHolder.twoSpearImageView.setDisplayOptions(DisplayOptionsType.STAR_HOME_ITEM);
                itemViewHolder.threeSpearImageView.setDisplayOptions(DisplayOptionsType.STAR_HOME_ITEM);

                itemViewHolder.oneSpearImageView.setEnableClickRipple(true);
                itemViewHolder.twoSpearImageView.setEnableClickRipple(true);
                itemViewHolder.threeSpearImageView.setEnableClickRipple(true);

                itemViewHolder.oneSpearImageView.setEnableShowProgress(true);
                itemViewHolder.twoSpearImageView.setEnableShowProgress(true);
                itemViewHolder.threeSpearImageView.setEnableShowProgress(true);

                itemViewHolder.oneSpearImageView.setOnClickListener(itemClickListener);
                itemViewHolder.twoSpearImageView.setOnClickListener(itemClickListener);
                itemViewHolder.threeSpearImageView.setOnClickListener(itemClickListener);
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
                HeaderViewHolder headerViewHolder = (HeaderViewHolder) viewHolder;
                headerViewHolder.spearImageView.displayUriImage(headerImageUrl);
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

                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) itemViewHolder.oneSpearImageView.getLayoutParams();
                params.topMargin = topMargin;
                params.bottomMargin = bottomMargin;
                itemViewHolder.oneSpearImageView.setLayoutParams(params);

                params = (ViewGroup.MarginLayoutParams) itemViewHolder.twoSpearImageView.getLayoutParams();
                params.topMargin = topMargin;
                params.bottomMargin = bottomMargin;
                itemViewHolder.twoSpearImageView.setLayoutParams(params);

                params = (ViewGroup.MarginLayoutParams) itemViewHolder.threeSpearImageView.getLayoutParams();
                params.topMargin = topMargin;
                params.bottomMargin = bottomMargin;
                itemViewHolder.threeSpearImageView.setLayoutParams(params);

                int oneReadPosition = (position*column);
                bind(itemViewHolder.oneSpearImageView, oneReadPosition<imageList.size()?imageList.get(oneReadPosition):null, oneReadPosition);

                int twoReadPosition = (position*column)+1;
                bind(itemViewHolder.twoSpearImageView, twoReadPosition<imageList.size()?imageList.get(twoReadPosition):null, twoReadPosition);

                int threeReadPosition = (position*column)+2;
                bind(itemViewHolder.threeSpearImageView, threeReadPosition<imageList.size()?imageList.get(threeReadPosition):null, threeReadPosition);
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

    private void bind(SpearImageView spearImageView, StarImageRequest.Image image, int position){
        if(image != null){
            spearImageView.setTag(position);
            spearImageView.displayUriImage(image.getSourceUrl());
            spearImageView.setVisibility(View.VISIBLE);
        }else{
            spearImageView.setVisibility(View.INVISIBLE);
        }
    }

    public void loadMoreFail(){
        if(loadMoreFooterViewHolder != null){
            loadMoreFooterViewHolder.contextTextView.setText("Sorry！您的包裹运送失败，您再试一次吧！");
            loadMoreFooterViewHolder.progressBar.setVisibility(View.GONE);
        }
    }

    private static class ItemViewHolder extends RecyclerView.ViewHolder{
        private SpearImageView oneSpearImageView;
        private SpearImageView twoSpearImageView;
        private SpearImageView threeSpearImageView;

        @SuppressWarnings("deprecation")
        public ItemViewHolder(View itemView) {
            super(itemView);
            oneSpearImageView = (SpearImageView) itemView.findViewById(R.id.image_starImageItem_one);
            twoSpearImageView = (SpearImageView) itemView.findViewById(R.id.image_starImageItem_two);
            threeSpearImageView = (SpearImageView) itemView.findViewById(R.id.image_starImageItem_three);
        }
    }

    private static class HeaderViewHolder extends RecyclerView.ViewHolder{
        private SpearImageView spearImageView;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            spearImageView = (SpearImageView) itemView.findViewById(R.id.image_starImageHeaderItem);
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
        public void onItemClick(int position, StarImageRequest.Image image);
    }

    public interface OnLoadMoreListener{
        public boolean isEnd();
        public void onLoadMore();
    }
}
