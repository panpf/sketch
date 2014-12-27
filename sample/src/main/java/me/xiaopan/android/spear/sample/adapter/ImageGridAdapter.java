package me.xiaopan.android.spear.sample.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.List;

import me.xiaopan.android.spear.sample.DisplayOptionsType;
import me.xiaopan.android.spear.widget.SpearImageView;

public class ImageGridAdapter extends BaseAdapter {
    private Context context;
    private List<String> imageUris;
    private int imageWidth = -1;
    private OnImageClickListener onImageClickListener;

    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public ImageGridAdapter(Context context, List<String> imageUris, int column, int horizontalSpacing, OnImageClickListener onImageClickListener){
        this.context = context;
        this.imageUris = imageUris;
        this.onImageClickListener = onImageClickListener;
        if(column > 1){
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = windowManager.getDefaultDisplay();
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2){
                imageWidth = (display.getWidth()-((column+1)*horizontalSpacing))/column;
            }else{
                Point point = new Point();
                display.getSize(point);
                imageWidth = (point.x-((column+1)*horizontalSpacing))/column;
            }
        }
    }

    @Override
    public Object getItem(int position) {
        return imageUris.get(position);
    }

    @Override
    public int getCount() {
        return imageUris!=null?imageUris.size():0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public List<String> getImageUrlList() {
        return imageUris;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SpearImageView spearImageView;
        if(convertView == null){
            spearImageView = new SpearImageView(context);
            spearImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            if(imageWidth != -1){
                spearImageView.setLayoutParams(new AbsListView.LayoutParams(imageWidth, imageWidth));
            }
            spearImageView.setEnablePressRipple(true);
            spearImageView.setDisplayOptions(DisplayOptionsType.LOCAL_PHOTO_ALBUM_ITEM);
            spearImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onImageClickListener != null && v.getTag() != null && v.getTag() instanceof Integer){
                        onImageClickListener.onImageClick((Integer) v.getTag());
                    }
                }
            });
            convertView = spearImageView;
        }else{
            spearImageView = (SpearImageView) convertView;
        }
        spearImageView.setImageByUri(imageUris.get(position));
        spearImageView.setTag(position);
        return convertView;
    }

    public interface OnImageClickListener{
        public void onImageClick(int position);
    }
}
