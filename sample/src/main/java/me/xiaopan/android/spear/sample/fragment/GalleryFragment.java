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

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Gallery;

import me.xiaopan.android.spear.sample.activity.ViewPagerActivity;
import me.xiaopan.android.spear.sample.adapter.GalleryImageAdapter;

@SuppressWarnings("deprecation")
public class GalleryFragment extends Fragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Gallery gallery = new Gallery(getActivity());
		gallery.setBackgroundColor(Color.BLACK);
		gallery.setSpacing(5);
		gallery.setAdapter(new GalleryImageAdapter(getActivity(), getArguments().getStringArray(GridFragment.PARAM_REQUIRED_STRING_ARRAY_URLS)));
		gallery.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Bundle bundle = new Bundle();
				bundle.putStringArray(GridFragment.PARAM_REQUIRED_STRING_ARRAY_URLS, getArguments().getStringArray(GridFragment.PARAM_REQUIRED_STRING_ARRAY_URLS));
				bundle.putInt(ViewPagerFragment.PARAM_OPTIONAL_INT_CURRENT_POSITION, position);
				Intent intent = new Intent(getActivity(), ViewPagerActivity.class);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		return gallery;
	}
}
