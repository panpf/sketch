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

package me.xiaopan.android.imageloader.sample.activity;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import me.xiaoapn.android.imageloader.R;
import me.xiaopan.android.imageloader.sample.adapter.BlackStringAdapter;

public class MainActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		
		getListView().setAdapter(new BlackStringAdapter(getBaseContext(), new String[]{"download", "load", "display", "同一张图片应用于不同场景"}));

        getListView().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Class<?> targetClass = null;
				switch(position - getListView().getHeaderViewsCount()){
					case 0 : 
						targetClass = DownloadActivity.class;
						break;
					case 1 : 
						targetClass = LoadActivity.class;
						break;
					case 2 : 
						targetClass = DisplayActivity.class;
						break;
                    case 3 :
                        targetClass = NormalActivity.class;
						break;
				}
				if(targetClass != null){
					startActivity(new Intent(getBaseContext(), targetClass));
				}
			}
		});
	}
}
