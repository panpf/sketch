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

package me.xiaopan.android.imageloader.sample.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import me.xiaoapn.android.imageloader.R;

import java.util.ArrayList;
import java.util.List;

public class BlackStringAdapter extends BaseAdapter {
	private Context context;
	private List<String> contents;
	private boolean full = true;
	
	public BlackStringAdapter(Context context, List<String> contents){
		this.context = context;
		this.contents = contents;
	}
	
	public BlackStringAdapter(Context context, String... contents){
		this.context = context;
		this.contents = new ArrayList<String>(contents.length);
		for(String string : contents){
			this.contents.add(string);
		}
	}

	@Override
	public Object getItem(int position) {
		return contents.get(position);
	}
	
	@Override
	public int getCount() {
		return isFull()?contents.size():3;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int realPosition, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if(convertView == null){
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.list_item_text, null);
			viewHolder.text = (TextView) convertView.findViewById(R.id.text_textItem_text);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		viewHolder.text.setText(contents.get(realPosition));
		
		return convertView;
	}
	
	class ViewHolder{
		private TextView text;
	}

	public boolean isFull() {
		return full;
	}

	public void setFull(boolean full) {
		this.full = full;
	}
}