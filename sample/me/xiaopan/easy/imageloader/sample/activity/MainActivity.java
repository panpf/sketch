/*
 * Copyright 2013 Peng fei Pan
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

package me.xiaopan.easy.imageloader.sample.activity;

import me.xiaoapn.easy.imageloader.R;
import me.xiaopan.easy.imageloader.sample.adapter.StringAdapter;
import me.xiaopan.easy.imageloader.sample.fragment.GalleryFragment;
import me.xiaopan.easy.imageloader.sample.fragment.GridFragment;
import me.xiaopan.easy.imageloader.sample.fragment.ListFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class MainActivity extends FragmentActivity {
	private DrawerLayout drawerLayout;
	private ListView listView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_main);
		drawerLayout.setDrawerShadow(R.drawable.shape_drawer_shaow_down_left, GravityCompat.START);
		drawerLayout.setDrawerShadow(R.drawable.shape_drawer_shaow_down_right, GravityCompat.END);
		
		listView = (ListView) findViewById(R.id.list_main);
		listView.setAdapter(new StringAdapter(getBaseContext(), "GridView", "ListView", "Gallery"));
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				set(position - listView.getHeaderViewsCount());
				drawerLayout.closeDrawers();
			}
		});
		
		set(0);
	}
	
	private void set(int index){
		Fragment fragment = null;
		Bundle bundle = null;
		switch(index){
			case 0 : 
				fragment = new GridFragment();
				bundle = new Bundle();
				bundle.putStringArray(GridFragment.PARAM_REQUIRED_STRING_ARRAY_URLS, getResources().getStringArray(R.array.shuzi));
				break;
			case 1 : 
				fragment = new ListFragment();
				bundle = new Bundle();
				bundle.putStringArray(GridFragment.PARAM_REQUIRED_STRING_ARRAY_URLS, getResources().getStringArray(R.array.shuzi));
				break;
			case 2 : 
				fragment = new GalleryFragment();
				bundle = new Bundle();
				bundle.putStringArray(GridFragment.PARAM_REQUIRED_STRING_ARRAY_URLS, getResources().getStringArray(R.array.urls));
				break;
		}
		
		if(fragment != null){
			if(bundle != null){
				
			}
			fragment.setArguments(bundle);
			getSupportFragmentManager().beginTransaction().replace(R.id.fragment_main, fragment).commit();
		}
	}
}
