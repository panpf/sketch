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

package me.xiaopan.android.spear.sample;

import android.app.Application;
import android.graphics.Color;
import android.view.animation.OvershootInterpolator;

import me.xiaoapn.android.spear.sample.R;
import me.xiaopan.android.gohttp.GoHttp;
import me.xiaopan.android.spear.Spear;
import me.xiaopan.android.spear.display.ColorFadeInImageDisplayer;
import me.xiaopan.android.spear.display.OriginalFadeInImageDisplayer;
import me.xiaopan.android.spear.display.ZoomOutImageDisplayer;
import me.xiaopan.android.spear.process.CircleImageProcessor;
import me.xiaopan.android.spear.process.ReflectionImageProcessor;
import me.xiaopan.android.spear.process.RoundedCornerImageProcessor;
import me.xiaopan.android.spear.request.DisplayOptions;

public class MyApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

        GoHttp.with(getBaseContext()).setDebugMode(true);
        Spear.setDebugMode(true);
        Spear.putOptions(
            DisplayOptionsType.GRID_VIEW,
            new DisplayOptions(getBaseContext())
                .loadingDrawable(R.drawable.image_loading, true)
                .loadFailDrawable(R.drawable.image_load_fail, true)
                .displayer(new ColorFadeInImageDisplayer(Color.WHITE))
        );

        Spear.putOptions(
            DisplayOptionsType.VIEW_PAGER,
            new DisplayOptions(getBaseContext())
                .loadFailDrawable(R.drawable.image_load_fail, true)
                .displayer(new ColorFadeInImageDisplayer(Color.BLACK))
                .processor(new ReflectionImageProcessor())
        );

        Spear.putOptions(
            DisplayOptionsType.LIST_VIEW,
            new DisplayOptions(getBaseContext())
                .loadingDrawable(R.drawable.image_loading, true)
                .loadFailDrawable(R.drawable.image_load_fail, true)
                .displayer(new ZoomOutImageDisplayer(new OvershootInterpolator()))
                .processor(new CircleImageProcessor())
        );

        Spear.putOptions(
            DisplayOptionsType.GALLERY,
            new DisplayOptions(getBaseContext())
                .loadingDrawable(R.drawable.image_loading, true)
                .loadFailDrawable(R.drawable.image_load_fail, true)
                .displayer(new OriginalFadeInImageDisplayer())
                .processor(new RoundedCornerImageProcessor())
        );

        Spear.putOptions(
            DisplayOptionsType.CATEGORY,
            new DisplayOptions(getBaseContext())
                .loadingDrawable(R.drawable.image_loading, true)
                .loadFailDrawable(R.drawable.image_load_fail, true)
                .displayer(new OriginalFadeInImageDisplayer())
        );

        Spear.putOptions(
            DisplayOptionsType.STAR_HEADER,
            new DisplayOptions(getBaseContext())
                .displayer(new OriginalFadeInImageDisplayer())
        );

        Spear.putOptions(
            DisplayOptionsType.STAR_ITEM,
            new DisplayOptions(getBaseContext())
                .loadingDrawable(R.drawable.image_loading, true)
                .loadFailDrawable(R.drawable.image_load_fail, true)
        );
	}
}