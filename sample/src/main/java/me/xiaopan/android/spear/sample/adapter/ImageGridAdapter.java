/*
 * Copyright (C) 2013 Peng fei Pan <sky@xiaopan.me>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.xiaopan.android.spear.sample.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import me.xiaopan.android.spear.Spear;
import me.xiaopan.android.spear.sample.DisplayOptionsType;
import me.xiaopan.android.spear.widget.SpearImageView;

public class ImageGridAdapter extends BaseAdapter {
	private Context context;
	private String[] imageUris;
    private int imageWidth = -1;
	
	@SuppressWarnings("deprecation")
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	public ImageGridAdapter(Context context, String[] imageUris, int column, int horizontalSpacing){
		this.context = context;
		this.imageUris = imageUris;
        if(column > 1){
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = windowManager.getDefaultDisplay();
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2){
                imageWidth = (display.getWidth()-((column-1)*horizontalSpacing))/column;
            }else{
                Point point = new Point();
                display.getSize(point);
                imageWidth = (point.x-((column-1)*horizontalSpacing))/column;
            }
        }
	}

	@Override
	public Object getItem(int position) {
		return imageUris[position];
	}

	@Override
	public int getCount() {
		return imageUris!=null?imageUris.length:0;
	}

	@Override
	public long getItemId(int position) {
		return position;
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
            spearImageView.setDisplayOptions(DisplayOptionsType.GRID_VIEW);
            convertView = spearImageView;
        }else{
            spearImageView = (SpearImageView) convertView;
        }
        spearImageView.setImageByUri(imageUris[position]);
        return convertView;
	}
}