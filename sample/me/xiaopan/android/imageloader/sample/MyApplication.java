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
		
		ImageLoader.getInstance(getBaseContext()).getConfiguration().setDebugMode(true);
		
		Options defaultOptions = new Options(getBaseContext())
			.setLoadingDrawableResId(R.drawable.image_loading)
			.setFailureDrawableResId(R.drawable.image_load_failure)
			.setBitmapProcessor(new ReflectionBitmapProcessor());
		ImageLoader.getInstance(getBaseContext()).getConfiguration().putOptions(OptionsType.DEFAULT, defaultOptions);

		Options viewPagerOptions = new Options(getBaseContext())
			.setFailureDrawableResId(R.drawable.image_load_failure)
			.setBitmapDisplayer(new ZoomOutBitmapDisplayer());
		ImageLoader.getInstance(getBaseContext()).getConfiguration().putOptions(OptionsType.VIEW_PAGER, viewPagerOptions);
		
		Options listOptions = new Options(getBaseContext())
			.setLoadingDrawableResId(R.drawable.image_loading)
			.setFailureDrawableResId(R.drawable.image_load_failure)
			.setBitmapProcessor(new CircleBitmapProcessor());
		ImageLoader.getInstance(getBaseContext()).getConfiguration().putOptions(OptionsType.LIST_VIEW, listOptions);

		Options galleryOptions = new Options(getBaseContext())
			.setLoadingDrawableResId(R.drawable.image_loading)
			.setFailureDrawableResId(R.drawable.image_load_failure)
			.setBitmapProcessor(new RoundedCornerBitmapProcessor());
		ImageLoader.getInstance(getBaseContext()).getConfiguration().putOptions(OptionsType.GALLERY, galleryOptions);
		
		Options simpleOptions = new Options(getBaseContext())
			.setLoadingDrawableResId(R.drawable.image_loading)
			.setFailureDrawableResId(R.drawable.image_load_failure)
			.setEnableMenoryCache(false)
			.setBitmapProcessor(null);
		ImageLoader.getInstance(getBaseContext()).getConfiguration().putOptions(OptionsType.SIMPLE, simpleOptions);
	}
}