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

package me.xiaopan.android.spear.sample.activity;

import android.os.Bundle;

import me.xiaoapn.android.spear.sample.R;
import me.xiaopan.android.inject.InjectContentView;
import me.xiaopan.android.inject.InjectParentMember;
import me.xiaopan.android.spear.sample.MyActionBarActivity;
import me.xiaopan.android.spear.sample.fragment.SearchFragment;

/**
 * 图片搜索页面
 */
@InjectParentMember
@InjectContentView(R.layout.activity_only_fragment)
public class SearchActivity extends MyActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_onlyFragment_content, new SearchFragment())
                .commit();
	}
}
