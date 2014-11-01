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

package me.xiaopan.android.spear.sample.fragment;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Color;
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

import java.io.IOException;

import me.xiaoapn.android.spear.sample.R;
import me.xiaopan.android.spear.Spear;
import me.xiaopan.android.spear.request.LoadListener;
import me.xiaopan.android.spear.sample.adapter.ImageFragmentAdapter;
import me.xiaopan.android.spear.util.FailureCause;

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
    	inflater.inflate(R.menu.menu_wallpaper, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(uris != null){
			switch(item.getItemId()){
				case R.id.menu_wallpaper :
					setWallpaper(uris[viewPager.getCurrentItem()]);
					break;
			}
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void setWallpaper(String imageUri){
		Spear.with(getActivity()).load(imageUri, new LoadListener() {
            @Override
            public void onStarted() {

            }

            @Override
            public void onCompleted(final Bitmap bitmap, From from) {
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
            public void onFailed(FailureCause failureCause) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "Apply Failured", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCanceled() {

            }
        }).fire();
	}
}
