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
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.xiaoapn.android.spear.sample.R;

public class TextListAdapter extends BaseAdapter {
	private Context context;
	private List<String> contents;
	private boolean full = true;
	
	public TextListAdapter(Context context, List<String> contents){
		this.context = context;
		this.contents = contents;
	}
	
	public TextListAdapter(Context context, String... contents){
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
	public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView;
		if(convertView == null){
			textView = new TextView(context);
            textView.setPadding(dp2px(context, 16), dp2px(context, 16), dp2px(context, 16), dp2px(context, 16));
            textView.setTextColor(context.getResources().getColorStateList(R.color.selector_text_title));
            textView.setGravity(Gravity.CENTER_VERTICAL);
            textView.setMinHeight(dp2px(context, 52));

            convertView = textView;
		}else{
			textView = (TextView) convertView;
		}
		
		textView.setText(contents.get(position));
		
		return convertView;
	}

	public boolean isFull() {
		return full;
	}

	public void setFull(boolean full) {
		this.full = full;
	}

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}