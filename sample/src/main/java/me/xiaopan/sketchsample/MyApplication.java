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
import me.xiaopan.sketch.Sketch;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        GoHttp.with(getBaseContext()).setDebugMode(true);

        SketchManager sketchManager = new SketchManager(getBaseContext());
        sketchManager.initConfig();
        sketchManager.initDisplayOptions();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

        Log.w("Application", "Memory is very low, has automatic releasing Sketch in memory cache(" + Formatter.formatFileSize(getBaseContext(), Sketch.with(getBaseContext()).getConfiguration().getMemoryCache().getSize()) + ")");
        Sketch.with(getBaseContext()).getConfiguration().getMemoryCache().clear();
    }
}