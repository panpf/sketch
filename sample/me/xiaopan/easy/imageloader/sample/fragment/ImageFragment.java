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

package me.xiaopan.easy.imageloader.sample.fragment;

import me.xiaoapn.easy.imagelader.R;
import me.xiaoapn.easy.imageloader.ImageLoader;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ImageFragment extends TitleFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_image, null);
		ImageView imageView = (ImageView) rootView.findViewById(R.id.image1);
		ImageLoader.getInstance().load("http://s1.dwstatic.com/group1/M00/AF/39/cfc4623b24057a642f6e812269175ada.jpg", imageView);
		ImageView imageView2 = (ImageView) rootView.findViewById(R.id.image2);
		ImageLoader.getInstance().load("http://s1.dwstatic.com/group1/M00/98/47/db8bbf7cf28ac4d4ce101ed5d2683ab0.jpg", imageView2);
		return rootView;
	}
	
	@Override
	public String getTitle() {
		return "使用默认选项加载图片";
	}
}