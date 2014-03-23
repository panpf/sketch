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

package me.xiaopan.android.imageloader.sample.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import me.xiaopan.android.imageloader.sample.fragment.ImageFragment;

public class ImageFragmentAdapter extends FragmentStatePagerAdapter {
	private String[] uris;
	
	public ImageFragmentAdapter(FragmentManager fm, String[] uris) {
		super(fm);
		this.uris = uris;
	}

	@Override
	public int getCount() {
		return uris.length;
	}

	@Override
	public Fragment getItem(int arg0) {
		ImageFragment imageFragment = new ImageFragment();
		Bundle bundle = new Bundle();
		bundle.putString(ImageFragment.PARAM_REQUIRED_IMAGE_URI, uris[arg0]);
		imageFragment.setArguments(bundle);
		return imageFragment;
	}
}
