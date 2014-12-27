package me.xiaopan.android.spear.sample.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import me.xiaoapn.android.spear.sample.R;
import me.xiaopan.android.spear.sample.DisplayOptionsType;
import me.xiaopan.android.spear.sample.net.request.StarCatalogRequest;
import me.xiaopan.android.spear.widget.SpearImageView;

/**
 * 明星目录适配器
 */
public class StarCatalogAdapter extends RecyclerView.Adapter{
    private Context context;
    private List<StarCatalogRequest.Star> items;
    private View.OnClickListener onClickListener;

    public StarCatalogAdapter(Context context, StarCatalogRequest.Result result, final OnImageClickListener onImageClickListener) {
        this.context = context;
        this.items = result.getWomanStarList();
        this.onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getTag() instanceof StarCatalogRequest.Star){
                    if(onImageClickListener != null){
                        onImageClickListener.onClickImage((StarCatalogRequest.Star) v.getTag());
                    }
                }
            }
        };
    }

    public void append(StarCatalogRequest.Result result){
        items.addAll(result.getWomanStarList());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        OneItemHolder oneItemHolder = new OneItemHolder(LayoutInflater.from(context).inflate(R.layout.list_item_star_head_portrait, parent, false));

        oneItemHolder.itemView.setOnClickListener(onClickListener);
        oneItemHolder.oneSpearImageView.setDisplayOptions(DisplayOptionsType.STAR_HEAD_PORTRAIT);
        oneItemHolder.oneSpearImageView.setEnablePressRipple(true);

        int space = (int) oneItemHolder.itemView.getResources().getDimension(R.dimen.home_category_margin_border);
        int screenWidth = oneItemHolder.itemView.getContext().getResources().getDisplayMetrics().widthPixels;
        int itemWidth = (screenWidth - (space*6))/3;
        int itemHeight = itemWidth;

        ViewGroup.LayoutParams params = oneItemHolder.oneSpearImageView.getLayoutParams();
        params.width = itemWidth;
        params.height = itemHeight;
        oneItemHolder.oneSpearImageView.setLayoutParams(params);

        params = oneItemHolder.oneNameTextView.getLayoutParams();
        params.width = itemWidth;
        oneItemHolder.oneNameTextView.setLayoutParams(params);

        return oneItemHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        OneItemHolder oneItemHolder = (OneItemHolder) holder;
        StarCatalogRequest.Star star = items.get(position);
        oneItemHolder.oneNameTextView.setText(star.getName());
        oneItemHolder.oneSpearImageView.setImageByUri(star.getAvatarUrl());
        oneItemHolder.itemView.setTag(star);
    }

    private static class OneItemHolder extends RecyclerView.ViewHolder{
        private SpearImageView oneSpearImageView;
        private TextView oneNameTextView;

        public OneItemHolder(View itemView) {
            super(itemView);
            oneSpearImageView = (SpearImageView) itemView.findViewById(R.id.spearImage_starHeadPortraitItem);
            oneNameTextView = (TextView) itemView.findViewById(R.id.text_starHeadPortraitItem);
        }
    }

    public interface OnImageClickListener{
        public void onClickImage(StarCatalogRequest.Star star);
    }
}