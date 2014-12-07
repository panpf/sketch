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

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import me.xiaopan.android.spear.sample.DisplayOptionsType;
import me.xiaopan.android.spear.widget.SpearImageView;

public class ImageListAdapter extends BaseAdapter {
	private Context context;
	private String[] imageUrls;
	
	public ImageListAdapter(Context context, String[] imageUrls){
		this.context = context;
		this.imageUrls = imageUrls;
	}

	@Override
	public Object getItem(int position) {
		return imageUrls[position];
	}

	@Override
	public int getCount() {
		return imageUrls.length;
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
            spearImageView.setDisplayOptions(DisplayOptionsType.LIST_VIEW);
            spearImageView.setLayoutParams(new AbsListView.LayoutParams(200, 200));
            convertView = spearImageView;
        }else{
            spearImageView = (SpearImageView) convertView;
        }

        spearImageView.setImageByUri(imageUrls[position]);
        return convertView;
    }
}