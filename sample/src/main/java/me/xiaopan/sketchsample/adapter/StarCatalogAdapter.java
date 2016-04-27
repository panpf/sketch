package me.xiaopan.sketchsample.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.xiaopan.sketch.SketchImageView;
import me.xiaopan.sketchsample.OptionsType;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.net.request.StarCatalogRequest;
import me.xiaopan.sketchsample.widget.MyImageView;

/**
 * 明星目录适配器
 */
public class StarCatalogAdapter extends RecyclerView.Adapter{
    private static final int ITEM_TYPE_DATA = 0;
    private static final int ITEM_TYPE_CATEGORY_TITLE = 1;

    private Context context;
    private List<Object> items;
    private View.OnClickListener onClickListener;
    private int itemWidth;

    public StarCatalogAdapter(Context context, StarCatalogRequest.Result result, final OnImageClickListener onImageClickListener) {
        this.context = context;
        append(result);
        int space = (int) context.getResources().getDimension(R.dimen.home_category_margin_border);
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        itemWidth = (screenWidth - (space*4))/3;
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
        List<StarCatalogRequest.Star> starList = result.getStarList();
        if(starList == null){
            return;
        }
        if(items == null){
            items = new ArrayList<Object>(starList.size());
        }
        items.add(result.getTitle());
        for(int w = 0, size = starList.size(); w < size;){
            int number = size - w;
            if(number == 1){
                DataItem dataItem = new DataItem();
                dataItem.star1 = starList.get(w++);
                items.add(dataItem);
            }else if(number == 2){
                DataItem dataItem = new DataItem();
                dataItem.star1 = starList.get(w++);
                dataItem.star2 = starList.get(w++);
                items.add(dataItem);
            }else{
                DataItem dataItem = new DataItem();
                dataItem.star1 = starList.get(w++);
                dataItem.star2 = starList.get(w++);
                dataItem.star3 = starList.get(w++);
                items.add(dataItem);
            }
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        Object object = items.get(position);
        if(object instanceof DataItem){
            return ITEM_TYPE_DATA;
        }else if(object instanceof String){
            return ITEM_TYPE_CATEGORY_TITLE;
        }else{
            return super.getItemViewType(position);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        if(viewType == ITEM_TYPE_CATEGORY_TITLE){
            viewHolder = new CategoryTitleHolder(LayoutInflater.from(context).inflate(R.layout.list_item_title, parent, false));
        }else if(viewType == ITEM_TYPE_DATA){
            ItemHolder itemHolder = new ItemHolder(LayoutInflater.from(context).inflate(R.layout.list_item_star_head_portrait, parent, false));

            itemHolder.oneLayout.setOnClickListener(onClickListener);
            itemHolder.twoLayout.setOnClickListener(onClickListener);
            itemHolder.threeLayout.setOnClickListener(onClickListener);

            itemHolder.oneImageView.setOnClickListener(onClickListener);
            itemHolder.twoImageView.setOnClickListener(onClickListener);
            itemHolder.threeImageView.setOnClickListener(onClickListener);

            itemHolder.oneImageView.setOptionsByName(OptionsType.NORMAL_CIRCULAR);
            itemHolder.twoImageView.setOptionsByName(OptionsType.NORMAL_CIRCULAR);
            itemHolder.threeImageView.setOptionsByName(OptionsType.NORMAL_CIRCULAR);

            itemHolder.oneImageView.setImageShape(SketchImageView.ImageShape.CIRCLE);
            itemHolder.twoImageView.setImageShape(SketchImageView.ImageShape.CIRCLE);
            itemHolder.threeImageView.setImageShape(SketchImageView.ImageShape.CIRCLE);

            ViewGroup.LayoutParams params = itemHolder.oneImageView.getLayoutParams();
            params.width = itemWidth;
            params.height = itemWidth;
            itemHolder.oneImageView.setLayoutParams(params);

            params = itemHolder.twoImageView.getLayoutParams();
            params.width = itemWidth;
            params.height = itemWidth;
            itemHolder.twoImageView.setLayoutParams(params);

            params = itemHolder.threeImageView.getLayoutParams();
            params.width = itemWidth;
            params.height = itemWidth;
            itemHolder.threeImageView.setLayoutParams(params);

            params = itemHolder.oneNameTextView.getLayoutParams();
            params.width = itemWidth;
            itemHolder.oneNameTextView.setLayoutParams(params);

            params = itemHolder.twoNameTextView.getLayoutParams();
            params.width = itemWidth;
            itemHolder.twoNameTextView.setLayoutParams(params);

            params = itemHolder.threeNameTextView.getLayoutParams();
            params.width = itemWidth;
            itemHolder.threeNameTextView.setLayoutParams(params);

            viewHolder = itemHolder;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ItemHolder){
            DataItem dataItem = (DataItem) items.get(position);
            ItemHolder itemHolder = (ItemHolder) holder;

            itemHolder.oneNameTextView.setText(dataItem.star1.getName());
            itemHolder.oneImageView.displayImage(dataItem.star1.getAvatarUrl());
            itemHolder.oneLayout.setTag(dataItem.star1);
            itemHolder.oneImageView.setTag(dataItem.star1);

            if(dataItem.star2 != null){
                itemHolder.twoNameTextView.setText(dataItem.star2.getName());
                itemHolder.twoImageView.displayImage(dataItem.star2.getAvatarUrl());
                itemHolder.twoLayout.setTag(dataItem.star2);
                itemHolder.twoImageView.setTag(dataItem.star2);
                itemHolder.twoLayout.setVisibility(View.VISIBLE);
            }else{
                itemHolder.twoLayout.setVisibility(View.INVISIBLE);
            }

            if(dataItem.star3 != null){
                itemHolder.threeNameTextView.setText(dataItem.star3.getName());
                itemHolder.threeImageView.displayImage(dataItem.star3.getAvatarUrl());
                itemHolder.threeLayout.setTag(dataItem.star3);
                itemHolder.threeImageView.setTag(dataItem.star3);
                itemHolder.threeLayout.setVisibility(View.VISIBLE);
            }else{
                itemHolder.threeLayout.setVisibility(View.INVISIBLE);
            }
        }else if(holder instanceof CategoryTitleHolder){
            String title = (String) items.get(position);
            CategoryTitleHolder titleHolder = (CategoryTitleHolder) holder;
            titleHolder.categoryTitleTextView.setText(title);
        }
    }

    private static class CategoryTitleHolder extends RecyclerView.ViewHolder{
        private TextView categoryTitleTextView;

        public CategoryTitleHolder(View itemView) {
            super(itemView);
            categoryTitleTextView = (TextView) itemView;
        }
    }

    private static class ItemHolder extends RecyclerView.ViewHolder{
        private View oneLayout;
        private MyImageView oneImageView;
        private TextView oneNameTextView;

        private View twoLayout;
        private MyImageView twoImageView;
        private TextView twoNameTextView;

        private View threeLayout;
        private MyImageView threeImageView;
        private TextView threeNameTextView;

        public ItemHolder(View itemView) {
            super(itemView);

            oneLayout = itemView.findViewById(R.id.layout_starHeadPortraitItem_one);
            oneImageView = (MyImageView) itemView.findViewById(R.id.image_starHeadPortraitItem_one);
            oneNameTextView = (TextView) itemView.findViewById(R.id.text_starHeadPortraitItem_one);

            twoLayout = itemView.findViewById(R.id.layout_starHeadPortraitItem_two);
            twoImageView = (MyImageView) itemView.findViewById(R.id.image_starHeadPortraitItem_two);
            twoNameTextView = (TextView) itemView.findViewById(R.id.text_starHeadPortraitItem_two);

            threeLayout = itemView.findViewById(R.id.layout_starHeadPortraitItem_three);
            threeImageView = (MyImageView) itemView.findViewById(R.id.image_starHeadPortraitItem_three);
            threeNameTextView = (TextView) itemView.findViewById(R.id.text_starHeadPortraitItem_three);
        }
    }

    public interface OnImageClickListener{
        void onClickImage(StarCatalogRequest.Star star);
    }

    private static class DataItem{
        private StarCatalogRequest.Star star1;
        private StarCatalogRequest.Star star2;
        private StarCatalogRequest.Star star3;
    }
}