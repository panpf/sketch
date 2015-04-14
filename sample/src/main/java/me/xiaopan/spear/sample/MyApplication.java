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

package me.xiaopan.spear.sample;

import android.app.Application;
import android.graphics.Color;
import android.util.Log;

import me.xiaopan.android.gohttp.GoHttp;
import me.xiaopan.spear.DisplayOptions;
import me.xiaopan.spear.Spear;
import me.xiaopan.spear.display.ColorTransitionImageDisplayer;
import me.xiaopan.spear.display.TransitionImageDisplayer;
import me.xiaopan.spear.process.CircleImageProcessor;
import me.xiaopan.spear.process.ReflectionImageProcessor;
import me.xiaopan.spear.sample.util.Settings;

public class MyApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

        GoHttp.with(getBaseContext()).setDebugMode(true);
        Spear.setDebugMode(true);
        Spear.putOptions(
                DisplayOptionsType.Rectangle_1,
                new DisplayOptions(getBaseContext())
                        .loadingDrawable(R.drawable.image_loading2)
                        .loadFailDrawable(R.drawable.image_load_fail2)
                        .pauseDownloadDrawable(R.drawable.image_click2)
                        .displayer(new TransitionImageDisplayer())
        );

        Spear.putOptions(
                DisplayOptionsType.Rectangle_0_75,
                new DisplayOptions(getBaseContext())
                        .loadingDrawable(R.drawable.image_loading2)
                        .loadFailDrawable(R.drawable.image_load_fail2)
                        .pauseDownloadDrawable(R.drawable.image_click2)
                        .displayer(new TransitionImageDisplayer())
        );

        Spear.putOptions(
                DisplayOptionsType.Rectangle_1_56,
                new DisplayOptions(getBaseContext())
                        .loadingDrawable(R.drawable.image_loading2)
                        .loadFailDrawable(R.drawable.image_load_fail2)
                        .pauseDownloadDrawable(R.drawable.image_click2)
                        .displayer(new TransitionImageDisplayer())
        );

        Spear.putOptions(
                DisplayOptionsType.Rectangle_3_2,
                new DisplayOptions(getBaseContext())
                        .loadingDrawable(R.drawable.image_loading2)
                        .loadFailDrawable(R.drawable.image_load_fail2)
                        .pauseDownloadDrawable(R.drawable.image_click2)
                        .displayer(new TransitionImageDisplayer())
        );

        Spear.putOptions(
            DisplayOptionsType.Detail,
            new DisplayOptions(getBaseContext())
                .loadFailDrawable(R.drawable.image_load_fail2)
                .pauseDownloadDrawable(R.drawable.image_click2)
                .loadGifDrawable()
                .displayer(new ColorTransitionImageDisplayer(Color.BLACK))
                .processor(new ReflectionImageProcessor())
        );

        Spear.putOptions(
                DisplayOptionsType.Circular,
                new DisplayOptions(getBaseContext())
                        .loadingDrawable(R.drawable.image_loading2)
                        .loadFailDrawable(R.drawable.image_load_fail2)
                        .pauseDownloadDrawable(R.drawable.image_click2)
                        .displayer(new TransitionImageDisplayer())
                        .processor(new CircleImageProcessor())
        );

        boolean isPauseDownload = Settings.with(getBaseContext()).isMobileNetworkPauseDownload();
        Spear.with(getBaseContext()).getConfiguration().setMobileNetworkPauseDownload(isPauseDownload);

            Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                    @Override
                    public void uncaughtException(Thread thread, Throwable ex) {
                            ex.printStackTrace();
                    }
            });
	}

        @Override
        public void onLowMemory() {
                super.onLowMemory();

                Log.w("Application", "lowMemory");
                Spear.with(getBaseContext()).getConfiguration().getMemoryCache().clear();
        }

        @Override
        public void onTrimMemory(int level) {
                super.onTrimMemory(level);
                Log.w("Application", "trimMemory");
        }
}