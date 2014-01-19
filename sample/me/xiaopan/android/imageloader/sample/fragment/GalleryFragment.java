/*
 * Copyright 2013 Peng fei Pan
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.xiaopan.android.imageloader.sample.fragment;

import me.xiaoapn.android.imageloader.R;
import me.xiaopan.android.imageloader.sample.adapter.GalleryImageAdapter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Gallery;

@SuppressWarnings("deprecation")
public class GalleryFragment extends Fragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Gallery gallery = (Gallery) new Gallery(getActivity());
		gallery.setBackgroundColor(Color.BLACK);
		gallery.setSpacing(2);
		gallery.setAdapter(new GalleryImageAdapter(getActivity(), getArguments().getStringArray(GridFragment.PARAM_REQUIRED_STRING_ARRAY_URLS)));
		gallery.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				PagerFragment pagerFragment = new PagerFragment();
				Bundle bundle = new Bundle();
				bundle.putStringArray(GridFragment.PARAM_REQUIRED_STRING_ARRAY_URLS, getArguments().getStringArray(GridFragment.PARAM_REQUIRED_STRING_ARRAY_URLS));
				bundle.putInt(PagerFragment.PARAM_OPTIONAL_INT_CURRENT_POSITION, position);
				pagerFragment.setArguments(bundle);
				getFragmentManager().beginTransaction().setCustomAnimations(R.anim.base_slide_to_left_in, R.anim.base_slide_to_left_out, R.anim.base_slide_to_right_in, R.anim.base_slide_to_right_out).add(R.id.fragment_main, pagerFragment).addToBackStack(PagerFragment.class.getSimpleName()).commitAllowingStateLoss();
			}
		});
		return gallery;
	}
}
