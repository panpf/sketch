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
import me.xiaopan.android.imageloader.task.DisplayOptions;
import android.app.Application;

public class MyApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		
		ImageLoader.getInstance(getBaseContext()).getConfiguration().setDebugMode(true);
		
		DisplayOptions gridDisplayOptions = new DisplayOptions(getBaseContext())
			.setLoadingDrawableResId(R.drawable.image_loading)
			.setFailureDrawableResId(R.drawable.image_load_failure)
			.setBitmapProcessor(new ReflectionBitmapProcessor());
		ImageLoader.getInstance(getBaseContext()).getConfiguration().putDisplayOptions(DisplayOptionsType.GRID_VIEW, gridDisplayOptions);

		DisplayOptions viewPagerDisplayOptions = new DisplayOptions(getBaseContext())
			.setFailureDrawableResId(R.drawable.image_load_failure)
			.setBitmapDisplayer(new ZoomOutBitmapDisplayer());
		ImageLoader.getInstance(getBaseContext()).getConfiguration().putDisplayOptions(DisplayOptionsType.VIEW_PAGER, viewPagerDisplayOptions);
		
		DisplayOptions listDisplayOptions = new DisplayOptions(getBaseContext())
			.setLoadingDrawableResId(R.drawable.image_loading)
			.setFailureDrawableResId(R.drawable.image_load_failure)
			.setBitmapProcessor(new CircleBitmapProcessor());
		ImageLoader.getInstance(getBaseContext()).getConfiguration().putDisplayOptions(DisplayOptionsType.LIST_VIEW, listDisplayOptions);

		DisplayOptions galleryDisplayOptions = new DisplayOptions(getBaseContext())
			.setLoadingDrawableResId(R.drawable.image_loading)
			.setFailureDrawableResId(R.drawable.image_load_failure)
			.setBitmapProcessor(new RoundedCornerBitmapProcessor());
		ImageLoader.getInstance(getBaseContext()).getConfiguration().putDisplayOptions(DisplayOptionsType.GALLERY, galleryDisplayOptions);
		
		DisplayOptions simpleDisplayOptions = new DisplayOptions(getBaseContext())
			.setLoadingDrawableResId(R.drawable.image_loading)
			.setFailureDrawableResId(R.drawable.image_load_failure)
			.setEnableMenoryCache(false)
			.setBitmapProcessor(null);
		ImageLoader.getInstance(getBaseContext()).getConfiguration().putDisplayOptions(DisplayOptionsType.SIMPLE, simpleDisplayOptions);
	}
}