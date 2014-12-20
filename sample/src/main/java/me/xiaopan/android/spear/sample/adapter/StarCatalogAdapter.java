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

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new OneItemHolder(LayoutInflater.from(context).inflate(R.layout.list_item_star_head_portrait, parent, false), onClickListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        OneItemHolder oneItemHolder = (OneItemHolder) holder;
        StarCatalogRequest.Star star = items.get(position);
        oneItemHolder.oneNameTextView.setText(star.getName());
        if(star.getAvatarUrl() != null && !("".equals(star.getAvatarUrl()))){
            oneItemHolder.oneSpearImageView.setImageByUri(star.getAvatarUrl());
            oneItemHolder.oneSpearImageView.setVisibility(View.VISIBLE);
        }else{
            oneItemHolder.oneSpearImageView.setVisibility(View.GONE);
        }

        oneItemHolder.itemView.setTag(star);
    }

    private static class OneItemHolder extends RecyclerView.ViewHolder{
        private SpearImageView oneSpearImageView;
        private TextView oneNameTextView;

        public OneItemHolder(View itemView, View.OnClickListener onClickListener) {
            super(itemView);
            oneSpearImageView = (SpearImageView) itemView.findViewById(R.id.spearImage_starHeadPortraitItem);
            oneNameTextView = (TextView) itemView.findViewById(R.id.text_starHeadPortraitItem);

            itemView.setOnClickListener(onClickListener);
            oneSpearImageView.setDisplayOptions(DisplayOptionsType.STAR_HEAD_PORTRAIT);
            oneSpearImageView.setEnablePressRipple(true);

            int space = (int) itemView.getResources().getDimension(R.dimen.home_category_margin_border);
            int screenWidth = itemView.getContext().getResources().getDisplayMetrics().widthPixels;
            int itemWidth = (screenWidth - (space*6))/3;

            ViewGroup.LayoutParams params = oneSpearImageView.getLayoutParams();
            params.width = itemWidth;
            params.height = itemWidth;
            oneSpearImageView.setLayoutParams(params);

            params = oneNameTextView.getLayoutParams();
            params.width = itemWidth;
            oneNameTextView.setLayoutParams(params);
        }
    }

    public interface OnImageClickListener{
        public void onClickImage(StarCatalogRequest.Star star);
    }
}