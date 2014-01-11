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

package me.xiaopan.easy.imageloader.sample;

import me.xiaoapn.easy.imageloader.ImageLoader;
import me.xiaoapn.easy.imageloader.R;
import me.xiaoapn.easy.imageloader.display.ZoomInBitmapDisplayer;
import me.xiaoapn.easy.imageloader.display.ZoomOutBitmapDisplayer;
import me.xiaoapn.easy.imageloader.process.ReflectionBitmapProcessor;
import me.xiaoapn.easy.imageloader.task.Options;
import android.app.Application;

public class MyApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		
		ImageLoader.getInstance().init(getBaseContext());
		ImageLoader.getInstance().getConfiguration().setDebugMode(true);
		
		Options defaultOptions = ImageLoader.getInstance().getConfiguration().getDefaultOptions();
		defaultOptions.setLoadingDrawable(getResources(), R.drawable.image_loading);	//设置加载中显示的图片
		defaultOptions.setFailureDrawable(getResources(), R.drawable.image_load_failure); 	//设置加载失败时显示的图片
		defaultOptions.setBitmapDisplayer(new ZoomOutBitmapDisplayer());
		defaultOptions.setBitmapProcessor(new ReflectionBitmapProcessor());
		
		Options viewPagerOptions = defaultOptions.copy();
		viewPagerOptions.setBitmapProcessor(new ReflectionBitmapProcessor());
		viewPagerOptions.setLoadingDrawable(null);
		ImageLoader.getInstance().getConfiguration().putOptions(OptionsType.VIEW_PAGER, viewPagerOptions);
		
		Options listViewOptions = defaultOptions.copy();
		listViewOptions.setBitmapProcessor(new ReflectionBitmapProcessor());
		listViewOptions.setBitmapDisplayer(new ZoomInBitmapDisplayer());
		ImageLoader.getInstance().getConfiguration().putOptions(OptionsType.LIST_VIEW, listViewOptions);
		
		Options galleryOptions = defaultOptions.copy();
		galleryOptions.setBitmapProcessor(new ReflectionBitmapProcessor());
		galleryOptions.setBitmapDisplayer(new ZoomOutBitmapDisplayer());
		ImageLoader.getInstance().getConfiguration().putOptions(OptionsType.GALLERY, galleryOptions);
		
		Options simpleOptions = defaultOptions.copy();
		simpleOptions.setBitmapProcessor(new ReflectionBitmapProcessor());
		simpleOptions.setBitmapDisplayer(new ZoomOutBitmapDisplayer());
		simpleOptions.getCacheConfig().setCacheInMemory(false);
		ImageLoader.getInstance().getConfiguration().putOptions(OptionsType.SIMPLE, simpleOptions);
	}
}