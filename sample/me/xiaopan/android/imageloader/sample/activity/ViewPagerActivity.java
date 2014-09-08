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

package me.xiaopan.android.imageloader.sample.activity;

import me.xiaoapn.android.imageloader.R;
import me.xiaopan.android.imageloader.sample.fragment.ViewPagerFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.widget.FrameLayout;

public class ViewPagerActivity extends FragmentActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		FrameLayout frameLayout = new FrameLayout(getBaseContext());
		frameLayout.setId(R.id.fragment_display);
		setContentView(frameLayout);
		
		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
		Fragment fragment = new ViewPagerFragment();
		fragment.setArguments(getIntent().getExtras());
		fragmentTransaction.replace(R.id.fragment_display, fragment);
		fragmentTransaction.commitAllowingStateLoss();
	}
}
