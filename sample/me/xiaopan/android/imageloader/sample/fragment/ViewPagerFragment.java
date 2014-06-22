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
import me.xiaopan.android.imageloader.util.Scheme;
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
	public static final String PARAM_OPTIONAL_INT_CURRENT_POSITION = "PARAM_OPTIONAL_INT_CURRENT_POSITION";
	private String[] uris;
	private ViewPager viewPager;
	private Handler handler;
	
	public ViewPagerFragment(){
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		handler = new Handler();
		if(getArguments() != null){
			uris = getArguments().getStringArray(GridFragment.PARAM_REQUIRED_STRING_ARRAY_URLS);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		viewPager = new ViewPager(getActivity());
		viewPager.setId(R.id.viewPlayer);
		viewPager.setBackgroundColor(Color.BLACK);
		if(uris != null){
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
		if(uris != null){
			switch(item.getItemId()){
				case R.id.menu_share: 
					shared(uris[viewPager.getCurrentItem()]);
					break;
				case R.id.menu_wallpaper : 
					setWallpaper(uris[viewPager.getCurrentItem()]);
					break;
			}
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void shared(String imageUri){
		Scheme scheme = Scheme.valueOfUri(imageUri);
		if(scheme == null) return;
		if(scheme == Scheme.ASSETS || scheme == Scheme.DRAWABLE){
			Toast.makeText(getActivity(), "Not support shared", Toast.LENGTH_SHORT).show();
			return;
		}
		
		Uri shareUri;
		if(scheme == Scheme.HTTP || scheme == Scheme.HTTPS){
			File file = ImageLoader.getInstance(getActivity()).getCacheFileByUri(imageUri.toString());
			if(file == null){
				Toast.makeText(getActivity(), "Has not been downloaded", Toast.LENGTH_SHORT).show();
				return;
			}
			shareUri = Uri.fromFile(file);
		}else{
			shareUri = Uri.parse(imageUri);
		}
		
		Intent shareIntent = new Intent();
		shareIntent.setAction(Intent.ACTION_SEND);
		shareIntent.putExtra(Intent.EXTRA_STREAM, shareUri);
		shareIntent.setType("image/*");
		startActivity(Intent.createChooser(shareIntent, "分享"));
	}
	
	private void setWallpaper(String imageUri){
		ImageLoader.getInstance(getActivity()).load(imageUri, new LoadListener() {
			@Override
			public void onStart() {
				
			}
			
			@Override
			public void onUpdateProgress(long totalLength, long completedLength) {
				
			}
			
			@Override
			public void onSuccess(final Bitmap bitmap) {
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
			public void onFailure() {
				handler.post(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(getActivity(), "Apply Failured", Toast.LENGTH_SHORT).show();
					}
				});
			}
			
			@Override
			public void onCancel() {
				
			}
		});
	}
}
