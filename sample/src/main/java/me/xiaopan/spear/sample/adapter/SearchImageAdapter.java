package me.xiaopan.spear.sample.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.etsy.android.grid.StaggeredGridView;

import java.util.ArrayList;
import java.util.List;

import me.xiaopan.spear.sample.OptionsType;
import me.xiaopan.spear.sample.R;
import me.xiaopan.spear.sample.net.request.StarImageRequest;
import me.xiaopan.spear.sample.widget.MyImageView;

/**
 * 搜索图片适配器
 */
public class SearchImageAdapter extends BaseAdapter {
    private int itemWidth;
    private Context context;
    private List<StarImageRequest.Image> imageList;
    private View.OnClickListener itemClickListener;
    private List<String> urlList;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public SearchImageAdapter(Context context, StaggeredGridView staggeredGridView, List<StarImageRequest.Image> imageList, final OnItemClickListener onItemClickListener){
        this.context = context;
        append(imageList);
        itemWidth = staggeredGridView.getColumnWidth();
        initListener(onItemClickListener);
    }

    private void initListener(final OnItemClickListener onItemClickListener){
        itemClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener != null){
                    ItemViewHolder viewHolder = (ItemViewHolder) v.getTag();
                    onItemClickListener.onItemClick(viewHolder.position, viewHolder.image);
                }
            }
        };
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    public int getDataSize(){
        return imageList.size();
    }

    public void append(List<StarImageRequest.Image> imageList) {
        if(this.imageList == null){
            this.imageList = imageList;
        }else{
            this.imageList.addAll(imageList);
        }
        if(urlList == null){
            urlList = new ArrayList<String>();
        }
        for(StarImageRequest.Image image : imageList){
            urlList.add(image.getSourceUrl());
        }
    }

    public List<String> getImageUrlList() {
        return urlList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = onCreateViewHolder(parent);
        }

        onBindViewHolder(convertView.getTag(), position);
        return convertView;
    }

    public View onCreateViewHolder(ViewGroup viewGroup) {
        View headItemView = LayoutInflater.from(context).inflate(R.layout.list_item_star_image_header, viewGroup, false);
        ItemViewHolder itemViewHolder = new ItemViewHolder(headItemView);
        itemViewHolder.imageView.setOnClickListener(itemClickListener);
        itemViewHolder.imageView.setDisplayOptions(OptionsType.Rectangle);
        headItemView.setTag(itemViewHolder);
        return headItemView;
    }

    public void onBindViewHolder(Object viewHolder, int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) viewHolder;
        StarImageRequest.Image image = imageList.get(position);

        ViewGroup.LayoutParams headParams = itemViewHolder.imageView.getLayoutParams();
        headParams.width = itemWidth;
        headParams.height = (int) (itemWidth / (image.getWidth()/(float) image.getHeight()));
        itemViewHolder.imageView.setLayoutParams(headParams);

        itemViewHolder.imageView.displayImage(image.getSourceUrl());

        itemViewHolder.image = image;
        itemViewHolder.position = position;
    }

    private static class ItemViewHolder {
        private MyImageView imageView;

        private int position;
        private StarImageRequest.Image image;

        public ItemViewHolder(View itemView) {
            imageView = (MyImageView) itemView.findViewById(R.id.image_starImageHeaderItem);
        }
    }

    public interface OnItemClickListener{
        void onItemClick(int position, StarImageRequest.Image image);
    }
}
