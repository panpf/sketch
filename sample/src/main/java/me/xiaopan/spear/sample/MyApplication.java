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
import me.xiaopan.spear.ImageHolder;
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

        Spear.putOptions(OptionsType.Rectangle,
                new DisplayOptions(getBaseContext())
                        .setLoadingImage(R.drawable.image_loading)
                        .setFailureImage(R.drawable.image_failure)
                        .setPauseDownloadImage(R.drawable.image_pause_download)
                        .setDecodeGifImage(false)
                        .setImageDisplayer(new TransitionImageDisplayer())
        );

        Spear.putOptions(
                OptionsType.Detail,
                new DisplayOptions(getBaseContext())
                        .setFailureImage(R.drawable.image_failure)
                        .setPauseDownloadImage(R.drawable.image_pause_download)
                        .setImageDisplayer(new ColorTransitionImageDisplayer(Color.TRANSPARENT))
        );

        Spear.putOptions(
                OptionsType.Circular,
                new DisplayOptions(getBaseContext())
                        .setLoadingImage(new ImageHolder(R.drawable.image_loading, CircleImageProcessor.getInstance()))
                        .setFailureImage(new ImageHolder(R.drawable.image_failure, CircleImageProcessor.getInstance()))
                        .setPauseDownloadImage(new ImageHolder(R.drawable.image_pause_download, CircleImageProcessor.getInstance()))
                        .setDecodeGifImage(false)
                        .setImageDisplayer(new TransitionImageDisplayer())
                        .setImageProcessor(CircleImageProcessor.getInstance())
        );

        Spear.putOptions(
                OptionsType.WindowBackground,
                new LoadOptions(getBaseContext())
                        .setScaleType(ImageView.ScaleType.CENTER_CROP)
                        .setImageProcessor(new GaussianBlurImageProcessor(true))
                        .setDecodeGifImage(false)
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