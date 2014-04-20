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

package me.xiaopan.android.imageloader.sample.fragment;

import java.io.File;
import java.io.IOException;

import me.xiaoapn.android.imageloader.R;
import me.xiaopan.android.imageloader.ImageLoader;
import me.xiaopan.android.imageloader.sample.adapter.ImageFragmentAdapter;
import me.xiaopan.android.imageloader.task.load.LoadListener;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class ViewPagerFragment extends Fragment {
	public static final String PARAM_OPTIONAL_INT_CURRENT_POSITION = "PARAM_OPTIONAL_INT_CURRENT_POSITION";;
	private String[] uris;
	private ViewPager viewPager;
	private Handler handler;
	
	public ViewPagerFragment(){
		setHasOptionsMenu(true);
		handler = new Handler();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		viewPager = new ViewPager(getActivity());
		viewPager.setId(R.id.viewPlayer);
		viewPager.setBackgroundColor(Color.BLACK);
		if(getArguments() != null){
			uris = getArguments().getStringArray(GridFragment.PARAM_REQUIRED_STRING_ARRAY_URLS);
			viewPager.setAdapter(new ImageFragmentAdapter(getChildFragmentManager(), uris));
			viewPager.setCurrentItem(getArguments().getInt(PARAM_OPTIONAL_INT_CURRENT_POSITION, 0));
		}
		return viewPager;
	}

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    	inflater.inflate(R.menu.menu_share, menu);
    	inflater.inflate(R.menu.menu_wallpaper, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		String currentUri = uris[viewPager.getCurrentItem()];
		switch(item.getItemId()){
			case R.id.menu_share: 
				if(uris != null){
					File file = ImageLoader.getInstance(getActivity()).getCacheFileByUri(currentUri);
					if(file.exists()){
						Intent shareIntent = new Intent();
						shareIntent.setAction(Intent.ACTION_SEND);
						shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
						shareIntent.setType("image/*");
						startActivity(Intent.createChooser(shareIntent, "分享"));
					}else{
						Toast.makeText(getActivity(), "Not found cache file", Toast.LENGTH_SHORT).show();
					}
				}
				break;
			case R.id.menu_wallpaper : 
				ImageLoader.getInstance(getActivity()).load(currentUri, new LoadListener() {
					@Override
					public void onUpdateProgress(long totalLength, long completedLength) {
						
					}
					
					@Override
					public void onStart() {
						
					}
					
					@Override
					public void onFailure() {
						handler.post(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(getActivity(), "Apply Failured", Toast.LENGTH_SHORT).show();
							}
						});
					}
					
					@Override
					public void onComplete(final Bitmap bitmap) {
						handler.post(new Runnable() {
							@Override
							public void run() {
								try {
									getActivity().setWallpaper(bitmap);
									Toast.makeText(getActivity(), "Apply Success", Toast.LENGTH_SHORT).show();
								} catch (IOException e) {
									e.printStackTrace();
									Toast.makeText(getActivity(), "Apply Failured", Toast.LENGTH_SHORT).show();
								}
							}
						});
					}
					
					@Override
					public void onCancel() {
						
					}
				});
				break;
		}
		return super.onOptionsItemSelected(item);
	}
}
