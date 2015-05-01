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
import android.text.format.Formatter;
import android.util.Log;
import android.widget.ImageView;

import me.xiaopan.android.gohttp.GoHttp;
import me.xiaopan.spear.DisplayOptions;
import me.xiaopan.spear.LoadOptions;
import me.xiaopan.spear.Spear;
import me.xiaopan.spear.display.ColorTransitionImageDisplayer;
import me.xiaopan.spear.display.TransitionImageDisplayer;
import me.xiaopan.spear.process.CircleImageProcessor;
import me.xiaopan.spear.process.GaussianBlurImageProcessor;
import me.xiaopan.spear.sample.util.Settings;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        GoHttp.with(getBaseContext()).setDebugMode(true);
        Spear.setDebugMode(true);
        Spear.putOptions(
                OptionsType.Rectangle_1,
                new DisplayOptions(getBaseContext())
                        .setLoadingDrawable(R.drawable.image_loading2)
                        .setLoadFailDrawable(R.drawable.image_load_fail2)
                        .setPauseDownloadDrawable(R.drawable.image_click2)
                        .setDecodeGifImage(false)
                        .setImageDisplayer(new TransitionImageDisplayer())
        );

        Spear.putOptions(
                OptionsType.Rectangle_0_75,
                new DisplayOptions(getBaseContext())
                        .setLoadingDrawable(R.drawable.image_loading2)
                        .setLoadFailDrawable(R.drawable.image_load_fail2)
                        .setPauseDownloadDrawable(R.drawable.image_click2)
                        .setImageDisplayer(new TransitionImageDisplayer())
        );

        Spear.putOptions(
                OptionsType.Rectangle_1_56,
                new DisplayOptions(getBaseContext())
                        .setLoadingDrawable(R.drawable.image_loading2)
                        .setLoadFailDrawable(R.drawable.image_load_fail2)
                        .setPauseDownloadDrawable(R.drawable.image_click2)
                        .setImageDisplayer(new TransitionImageDisplayer())
        );

        Spear.putOptions(
                OptionsType.Rectangle_3_2,
                new DisplayOptions(getBaseContext())
                        .setLoadingDrawable(R.drawable.image_loading2)
                        .setLoadFailDrawable(R.drawable.image_load_fail2)
                        .setPauseDownloadDrawable(R.drawable.image_click2)
                        .setImageDisplayer(new TransitionImageDisplayer())
        );

        Spear.putOptions(
                OptionsType.Detail,
                new DisplayOptions(getBaseContext())
                        .setLoadFailDrawable(R.drawable.image_load_fail2)
                        .setPauseDownloadDrawable(R.drawable.image_click2)
                        .setImageDisplayer(new ColorTransitionImageDisplayer(Color.TRANSPARENT))
        );

        Spear.putOptions(
                OptionsType.Circular,
                new DisplayOptions(getBaseContext())
                        .setLoadingDrawable(R.drawable.image_loading2)
                        .setLoadFailDrawable(R.drawable.image_load_fail2)
                        .setPauseDownloadDrawable(R.drawable.image_click2)
                        .setImageDisplayer(new TransitionImageDisplayer())
                        .setImageProcessor(new CircleImageProcessor())
        );

        Spear.putOptions(
                OptionsType.WindowBackground,
                new LoadOptions(getBaseContext())
                        .setScaleType(ImageView.ScaleType.CENTER_CROP)
                        .setImageProcessor(new GaussianBlurImageProcessor(true))
                        .setDecodeGifImage(false)
        );

        Spear.putOptions(
                OptionsType.Staggered,
                new DisplayOptions(getBaseContext())
                        .setDecodeGifImage(false)
                        .setImageDisplayer(new ColorTransitionImageDisplayer(Color.TRANSPARENT))
        );

        boolean isPauseDownload = Settings.with(getBaseContext()).isMobileNetworkPauseDownload();
        Spear.with(getBaseContext()).getConfiguration().setMobileNetworkPauseDownload(isPauseDownload);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

        Log.w("Application", "Memory is very low, has automatic releasing Spear in memory cache(" + Formatter.formatFileSize(getBaseContext(), Spear.with(getBaseContext()).getConfiguration().getMemoryCache().getSize()) + ")");
        Spear.with(getBaseContext()).getConfiguration().getMemoryCache().clear();
    }
}