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

import me.xiaoapn.android.spear.sample.R;
import me.xiaopan.android.gohttp.GoHttp;
import me.xiaopan.android.spear.Spear;
import me.xiaopan.android.spear.display.ColorTransitionImageDisplayer;
import me.xiaopan.android.spear.display.TransitionImageDisplayer;
import me.xiaopan.android.spear.process.CircleImageProcessor;
import me.xiaopan.android.spear.process.ReflectionImageProcessor;
import me.xiaopan.android.spear.DisplayOptions;

public class MyApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

        GoHttp.with(getBaseContext()).setDebugMode(true);
        Spear.setDebugMode(true);
        Spear.putOptions(
            DisplayOptionsType.LOCAL_PHOTO_ALBUM_ITEM,
            new DisplayOptions(getBaseContext())
                .loadingDrawable(R.drawable.image_loading)
                .loadFailDrawable(R.drawable.image_load_fail)
                .displayer(new TransitionImageDisplayer())
        );

        Spear.putOptions(
            DisplayOptionsType.IMAGE_DETAIL_ITEM,
            new DisplayOptions(getBaseContext())
                .loadFailDrawable(R.drawable.image_load_fail)
                .displayer(new ColorTransitionImageDisplayer(Color.BLACK))
                .processor(new ReflectionImageProcessor())
        );

        Spear.putOptions(
            DisplayOptionsType.STAR_HOME_HEADER,
            new DisplayOptions(getBaseContext())
                .displayer(new TransitionImageDisplayer())
        );

        Spear.putOptions(
            DisplayOptionsType.STAR_HOME_ITEM,
            new DisplayOptions(getBaseContext())
                .loadingDrawable(R.drawable.image_loading)
                .loadFailDrawable(R.drawable.image_load_fail)
                .displayer(new TransitionImageDisplayer())
        );

        Spear.putOptions(
                DisplayOptionsType.STAR_HEAD_PORTRAIT,
                new DisplayOptions(getBaseContext())
                    .loadingDrawable(R.drawable.image_loading)
                    .loadFailDrawable(R.drawable.image_load_fail)
                    .displayer(new TransitionImageDisplayer())
                    .processor(new CircleImageProcessor())
        );

        Spear.putOptions(
                DisplayOptionsType.INDEX_CATEGORY_ONE,
                new DisplayOptions(getBaseContext())
                    .loadingDrawable(R.drawable.image_loading)
                    .loadFailDrawable(R.drawable.image_load_fail)
                    .displayer(new TransitionImageDisplayer())
        );

        Spear.putOptions(
                DisplayOptionsType.INDEX_CATEGORY_TWO,
                new DisplayOptions(getBaseContext())
                    .loadingDrawable(R.drawable.image_loading)
                    .loadFailDrawable(R.drawable.image_load_fail)
                    .displayer(new TransitionImageDisplayer())
        );

        Spear.putOptions(
                DisplayOptionsType.INDEX_CATEGORY_THREE,
                new DisplayOptions(getBaseContext())
                        .loadingDrawable(R.drawable.image_loading)
                        .loadFailDrawable(R.drawable.image_load_fail)
                        .displayer(new TransitionImageDisplayer())
        );

        Spear.putOptions(
                DisplayOptionsType.HOT_STAR_ONE,
                new DisplayOptions(getBaseContext())
                    .loadingDrawable(R.drawable.image_loading)
                    .loadFailDrawable(R.drawable.image_load_fail)
                    .displayer(new TransitionImageDisplayer())
        );

        Spear.putOptions(
                DisplayOptionsType.HOT_STAR_TWO,
                new DisplayOptions(getBaseContext())
                    .loadingDrawable(R.drawable.image_loading)
                    .loadFailDrawable(R.drawable.image_load_fail)
                    .displayer(new TransitionImageDisplayer())
        );

        Spear.putOptions(
                DisplayOptionsType.HOT_STAR_THREE,
                new DisplayOptions(getBaseContext())
                    .loadingDrawable(R.drawable.image_loading)
                    .loadFailDrawable(R.drawable.image_load_fail)
                    .displayer(new TransitionImageDisplayer())
        );

        Spear.putOptions(
                DisplayOptionsType.SEARCH_ITEM_GRID,
                new DisplayOptions(getBaseContext())
                        .loadingDrawable(R.drawable.image_loading)
                        .loadFailDrawable(R.drawable.image_load_fail)
                        .displayer(new TransitionImageDisplayer())
        );

        Spear.putOptions(
                DisplayOptionsType.SEARCH_ITEM_LINEAR,
                new DisplayOptions(getBaseContext())
                        .loadFailDrawable(R.drawable.image_load_fail)
                        .displayer(new TransitionImageDisplayer())
        );

        Spear.putOptions(
                DisplayOptionsType.SEARCH_ITEM_STAGGERED,
                new DisplayOptions(getBaseContext())
                        .loadFailDrawable(R.drawable.image_load_fail)
                        .displayer(new TransitionImageDisplayer())
        );
	}
}