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

package me.xiaopan.sketchsample;

import android.app.Application;
import android.text.format.Formatter;
import android.util.Log;

import me.xiaopan.android.gohttp.GoHttp;
import me.xiaopan.sketch.DisplayOptions;
import me.xiaopan.sketch.ImageHolder;
import me.xiaopan.sketch.LoadOptions;
import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.display.TransitionImageDisplayer;
import me.xiaopan.sketch.process.CircleImageProcessor;
import me.xiaopan.sketch.process.GaussianBlurImageProcessor;
import me.xiaopan.sketchsample.util.Settings;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        GoHttp.with(getBaseContext()).setDebugMode(true);
        Sketch.setDebugMode(true);

        Sketch.putOptions(OptionsType.Rectangle,
                new DisplayOptions(getBaseContext())
                        .setLoadingImage(R.drawable.image_loading)
                        .setFailureImage(R.drawable.image_failure)
                        .setPauseDownloadImage(R.drawable.image_pause_download)
                        .setDecodeGifImage(false)
                        .setImageDisplayer(new TransitionImageDisplayer())
        );

        Sketch.putOptions(
                OptionsType.Detail,
                new DisplayOptions(getBaseContext())
                        .setFailureImage(R.drawable.image_failure)
                        .setPauseDownloadImage(R.drawable.image_pause_download)
                        .setImageDisplayer(new TransitionImageDisplayer())
        );

        Sketch.putOptions(
                OptionsType.Circular,
                new DisplayOptions(getBaseContext())
                        .setLoadingImage(new ImageHolder(R.drawable.image_loading, CircleImageProcessor.getInstance()))
                        .setFailureImage(new ImageHolder(R.drawable.image_failure, CircleImageProcessor.getInstance()))
                        .setPauseDownloadImage(new ImageHolder(R.drawable.image_pause_download, CircleImageProcessor.getInstance()))
                        .setDecodeGifImage(false)
                        .setImageDisplayer(new TransitionImageDisplayer())
                        .setImageProcessor(CircleImageProcessor.getInstance())
        );

        Sketch.putOptions(
                OptionsType.WindowBackground,
                new LoadOptions(getBaseContext())
                        .setImageProcessor(new GaussianBlurImageProcessor(true))
                        .setDecodeGifImage(false)
        );

        boolean isPauseDownload = Settings.with(getBaseContext()).isMobileNetworkPauseDownload();
        Sketch.with(getBaseContext()).getConfiguration().setMobileNetworkPauseDownload(isPauseDownload);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

        Log.w("Application", "Memory is very low, has automatic releasing Sketch in memory cache(" + Formatter.formatFileSize(getBaseContext(), Sketch.with(getBaseContext()).getConfiguration().getMemoryCache().getSize()) + ")");
        Sketch.with(getBaseContext()).getConfiguration().getMemoryCache().clear();
    }
}