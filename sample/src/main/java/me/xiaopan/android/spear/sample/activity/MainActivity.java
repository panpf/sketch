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

package me.xiaopan.android.spear.sample.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import me.xiaoapn.android.spear.sample.R;
import me.xiaopan.android.spear.sample.adapter.TextListAdapter;

public class MainActivity extends ActionBarActivity {
    private ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list);

        listView = (ListView) findViewById(android.R.id.list);
		listView.setAdapter(new TextListAdapter(getBaseContext(), new String[]{"download", "load", "display", "同一张图片应用于不同场景", "图片搜索"}));
        listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Class<?> targetClass = null;
				switch(position - listView.getHeaderViewsCount()){
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
                    case 4 :
                        targetClass = SearchActivity.class;
						break;
				}
				if(targetClass != null){
					startActivity(new Intent(getBaseContext(), targetClass));
				}
			}
		});

		setContentView(listView);
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_github, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_github :
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_github))));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
