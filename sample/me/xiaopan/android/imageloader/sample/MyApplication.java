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

import android.app.Application;
import me.xiaoapn.android.imageloader.R;
import me.xiaopan.android.imageloader.ImageLoader;
import me.xiaopan.android.imageloader.display.ZoomOutBitmapDisplayer;
import me.xiaopan.android.imageloader.process.CircleBitmapProcessor;
import me.xiaopan.android.imageloader.process.ReflectionBitmapProcessor;
import me.xiaopan.android.imageloader.process.RoundedCornerBitmapProcessor;
import me.xiaopan.android.imageloader.task.display.DisplayOptions;

public class MyApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		
		ImageLoader.getInstance(getBaseContext()).getConfiguration().setDebugMode(true);
		
		DisplayOptions gridDisplayOptions = new DisplayOptions(getBaseContext());
        gridDisplayOptions.setLoadingDrawableResId(R.drawable.image_loading);
        gridDisplayOptions.setFailureDrawableResId(R.drawable.image_load_failure);
        gridDisplayOptions.setBitmapProcessor(new ReflectionBitmapProcessor());
		ImageLoader.getInstance(getBaseContext()).getConfiguration().putOptions(DisplayOptionsType.GRID_VIEW, gridDisplayOptions);

		DisplayOptions viewPagerDisplayOptions = new DisplayOptions(getBaseContext());
        viewPagerDisplayOptions.setFailureDrawableResId(R.drawable.image_load_failure);
        viewPagerDisplayOptions.setBitmapDisplayer(new ZoomOutBitmapDisplayer());
		ImageLoader.getInstance(getBaseContext()).getConfiguration().putOptions(DisplayOptionsType.VIEW_PAGER, viewPagerDisplayOptions);
		
		DisplayOptions listDisplayOptions = new DisplayOptions(getBaseContext());
        listDisplayOptions.setLoadingDrawableResId(R.drawable.image_loading);
        listDisplayOptions.setFailureDrawableResId(R.drawable.image_load_failure);
        listDisplayOptions.setBitmapProcessor(new CircleBitmapProcessor());
		ImageLoader.getInstance(getBaseContext()).getConfiguration().putOptions(DisplayOptionsType.LIST_VIEW, listDisplayOptions);

		DisplayOptions galleryDisplayOptions = new DisplayOptions(getBaseContext());
        galleryDisplayOptions.setLoadingDrawableResId(R.drawable.image_loading);
        galleryDisplayOptions.setFailureDrawableResId(R.drawable.image_load_failure);
        galleryDisplayOptions.setBitmapProcessor(new RoundedCornerBitmapProcessor());
		ImageLoader.getInstance(getBaseContext()).getConfiguration().putOptions(DisplayOptionsType.GALLERY, galleryDisplayOptions);
		
		DisplayOptions simpleDisplayOptions = new DisplayOptions(getBaseContext());
        simpleDisplayOptions.setLoadingDrawableResId(R.drawable.image_loading);
        simpleDisplayOptions.setFailureDrawableResId(R.drawable.image_load_failure);
        simpleDisplayOptions.setEnableMemoryCache(false);
        simpleDisplayOptions.setBitmapProcessor(null);
		ImageLoader.getInstance(getBaseContext()).getConfiguration().putOptions(DisplayOptionsType.SIMPLE, simpleDisplayOptions);
	}
}