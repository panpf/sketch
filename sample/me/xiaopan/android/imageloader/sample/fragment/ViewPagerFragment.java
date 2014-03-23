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

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import me.xiaoapn.android.imageloader.R;
import me.xiaopan.android.imageloader.sample.adapter.ImageFragmentAdapter;

public class ViewPagerFragment extends Fragment {
	public static final String PARAM_OPTIONAL_INT_CURRENT_POSITION = "PARAM_OPTIONAL_INT_CURRENT_POSITION";;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewPager viewPager = new ViewPager(getActivity());
		viewPager.setId(R.id.viewPlayer);
		viewPager.setBackgroundColor(Color.BLACK);
		if(getArguments() != null){
			viewPager.setAdapter(new ImageFragmentAdapter(getChildFragmentManager(), getArguments().getStringArray(GridFragment.PARAM_REQUIRED_STRING_ARRAY_URLS)));
			viewPager.setCurrentItem(getArguments().getInt(PARAM_OPTIONAL_INT_CURRENT_POSITION, 0));
		}
		return viewPager;
	}
}
