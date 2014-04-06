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

package me.xiaopan.android.imageloader.sample;

import me.xiaoapn.android.imageloader.R;
import me.xiaopan.android.imageloader.ImageLoader;
import me.xiaopan.android.imageloader.display.ZoomOutBitmapDisplayer;
import me.xiaopan.android.imageloader.process.CircleBitmapProcessor;
import me.xiaopan.android.imageloader.process.ReflectionBitmapProcessor;
import me.xiaopan.android.imageloader.process.RoundedCornerBitmapProcessor;
import me.xiaopan.android.imageloader.task.display.DisplayOptions;
import android.app.Application;

public class MyApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

        ImageLoader.getInstance(getBaseContext()).getConfiguration().setDebugMode(true) //开启Debug，在控制台输出LOG
        .putOptions(DisplayOptionsType.GRID_VIEW, new DisplayOptions(getBaseContext())
            .setLoadingDrawable(R.drawable.image_loading)
            .setLoadFailDrawable(R.drawable.image_load_fail)
            .setProcessor(new ReflectionBitmapProcessor()))
        .putOptions(DisplayOptionsType.VIEW_PAGER, new DisplayOptions(getBaseContext())
            .setLoadFailDrawable(R.drawable.image_load_fail)
            .setDisplayer(new ZoomOutBitmapDisplayer()))
        .putOptions(DisplayOptionsType.LIST_VIEW, new DisplayOptions(getBaseContext())
            .setLoadingDrawable(R.drawable.image_loading)
            .setLoadFailDrawable(R.drawable.image_load_fail)
            .setProcessor(new CircleBitmapProcessor()))
        .putOptions(DisplayOptionsType.GALLERY, new DisplayOptions(getBaseContext())
            .setLoadingDrawable(R.drawable.image_loading)
            .setLoadFailDrawable(R.drawable.image_load_fail)
            .setProcessor(new RoundedCornerBitmapProcessor()));
	}
}