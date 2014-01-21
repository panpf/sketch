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

package me.xiaopan.android.imageloader.sample;

import me.xiaoapn.android.imageloader.R;
import me.xiaopan.android.imageloader.ImageLoader;
import me.xiaopan.android.imageloader.display.ZoomOutBitmapDisplayer;
import me.xiaopan.android.imageloader.process.CircleBitmapProcessor;
import me.xiaopan.android.imageloader.process.ReflectionBitmapProcessor;
import me.xiaopan.android.imageloader.process.RoundedCornerBitmapProcessor;
import me.xiaopan.android.imageloader.task.Options;
import android.app.Application;

public class MyApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		
		ImageLoader.getInstance().init(getBaseContext());
		ImageLoader.getInstance().getConfiguration().setDebugMode(true);
		
		Options defaultOptions = ImageLoader.getInstance().getConfiguration().getDefaultOptions()
			.setLoadingDrawable(R.drawable.image_loading)
			.setFailureDrawable(R.drawable.image_load_failure)
			.setBitmapProcessor(new ReflectionBitmapProcessor());

		Options viewPagerOptions = defaultOptions.copy()
			.setLoadingDrawable(null)
			.setBitmapDisplayer(new ZoomOutBitmapDisplayer());
		
		Options listOptions = defaultOptions.copy()
			.setBitmapProcessor(new CircleBitmapProcessor())
			.processDrawables();

		Options galleryOptions = defaultOptions.copy()
			.setBitmapProcessor(new RoundedCornerBitmapProcessor())
			.processDrawables();
		
		Options simpleOptions = defaultOptions.copy()
			.setEnableMenoryCache(false)
			.setBitmapProcessor(null);
		
		defaultOptions.processDrawables();
		
		ImageLoader.getInstance().getConfiguration().putOptions(OptionsType.VIEW_PAGER, viewPagerOptions);
		ImageLoader.getInstance().getConfiguration().putOptions(OptionsType.LIST_VIEW, listOptions);
		ImageLoader.getInstance().getConfiguration().putOptions(OptionsType.GALLERY, galleryOptions);
		ImageLoader.getInstance().getConfiguration().putOptions(OptionsType.SIMPLE, simpleOptions);
	}
}