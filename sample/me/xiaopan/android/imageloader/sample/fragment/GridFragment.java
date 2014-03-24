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

import me.xiaoapn.android.imageloader.R;
import me.xiaopan.android.imageloader.sample.adapter.GridImageAdapter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class GridFragment extends Fragment {
	public static final String PARAM_REQUIRED_STRING_ARRAY_URLS = "PARAM_REQUIRED_STRING_ARRAY_URLS";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		GridView gridView = new GridView(getActivity());
		gridView.setBackgroundColor(Color.BLACK);
		gridView.setPadding(0, 0, 0, 0);
		gridView.setNumColumns(3);
		gridView.setVerticalSpacing(2);
		gridView.setHorizontalSpacing(2);
		gridView.setAdapter(new GridImageAdapter(getActivity(), getArguments().getStringArray(PARAM_REQUIRED_STRING_ARRAY_URLS), 3));
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ViewPagerFragment viewPagerFragment = new ViewPagerFragment();
				Bundle bundle = new Bundle();
				bundle.putStringArray(GridFragment.PARAM_REQUIRED_STRING_ARRAY_URLS, getArguments().getStringArray(GridFragment.PARAM_REQUIRED_STRING_ARRAY_URLS));
				bundle.putInt(ViewPagerFragment.PARAM_OPTIONAL_INT_CURRENT_POSITION, position);
				viewPagerFragment.setArguments(bundle);
				getFragmentManager().beginTransaction().setCustomAnimations(R.anim.base_slide_to_left_in, R.anim.base_slide_to_left_out, R.anim.base_slide_to_right_in, R.anim.base_slide_to_right_out).add(R.id.fragment_display, viewPagerFragment).addToBackStack(ViewPagerFragment.class.getSimpleName()).commitAllowingStateLoss();
			}
		});
		return gridView;
	}
}
