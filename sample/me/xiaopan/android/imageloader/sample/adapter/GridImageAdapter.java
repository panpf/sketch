/*
 * Copyright 2013 Peng fei Pan
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.xiaopan.android.imageloader.sample.adapter;

import me.xiaoapn.android.imageloader.R;
import me.xiaopan.android.imageloader.ImageLoader;
import me.xiaopan.android.imageloader.sample.OptionsType;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class GridImageAdapter extends BaseAdapter {
	private Context context;
	private String[] imageUris;
	private int cloumn;
	private int screenWidth;
	
	@SuppressWarnings("deprecation")
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	public GridImageAdapter(Context context, String[] imageUris, int cloumn){
		this.context = context;
		this.imageUris = imageUris;
		this.cloumn = cloumn;
		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = windowManager.getDefaultDisplay();
		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2){
			screenWidth = display.getWidth();
		}else{
			Point point = new Point();
			display.getSize(point);
			screenWidth = point.x;
		}
	}

	@Override
	public Object getItem(int position) {
		return imageUris[position];
	}

	@Override
	public int getCount() {
		return imageUris.length;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		if(convertView == null){
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.grid_item_image, null);
			viewHolder.image = (ImageView) convertView.findViewById(R.id.image_gridItem);
			if(cloumn > 1){
				viewHolder.image.setLayoutParams(new FrameLayout.LayoutParams(screenWidth/cloumn, screenWidth/cloumn));
				viewHolder.image.setScaleType(ScaleType.CENTER_CROP);
			}
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		Log.w("ImageAdapter", "ImageAdapter：Uri="+imageUris[position]+"; ImageViewCode="+viewHolder.hashCode());
		ImageLoader.getInstance(context).display(imageUris[position], viewHolder.image, OptionsType.DEFAULT);
		return convertView;
	}
	
	class ViewHolder{
		ImageView image;
	}
}