/*
 * Copyright (C) 2019 Peng fei Pan <panpfpanpf@outlook.me>
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

package me.panpf.sketch;

import android.content.ContentProvider;
import android.content.Context;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import me.panpf.sketch.request.DisplayHelper;
import me.panpf.sketch.request.DownloadHelper;
import me.panpf.sketch.request.DownloadListener;
import me.panpf.sketch.request.LoadHelper;
import me.panpf.sketch.request.LoadListener;
import me.panpf.sketch.uri.AndroidResUriModel;
import me.panpf.sketch.uri.ApkIconUriModel;
import me.panpf.sketch.uri.AppIconUriModel;
import me.panpf.sketch.uri.AssetUriModel;
import me.panpf.sketch.uri.Base64UriModel;
import me.panpf.sketch.uri.Base64VariantUriModel;
import me.panpf.sketch.uri.ContentUriModel;
import me.panpf.sketch.uri.DrawableUriModel;
import me.panpf.sketch.uri.FileUriModel;
import me.panpf.sketch.uri.FileVariantUriModel;
import me.panpf.sketch.uri.HttpUriModel;
import me.panpf.sketch.uri.HttpsUriModel;
import me.panpf.sketch.util.SketchUtils;

@SuppressWarnings("WeakerAccess")
public class Sketch {
    public static final String META_DATA_KEY_INITIALIZER = "SKETCH_INITIALIZER";

    @Nullable
    private static volatile Sketch instance;
    @NonNull
    private Configuration configuration;

    private Sketch(@NonNull Context context) {
        this.configuration = new Configuration(context);
    }

    /**
     * Get a unique instance
     */
    @NonNull
    // todo 取消默认提供的单例，改成 builder 方式创建
    public static Sketch with(@NonNull Context context) {
        Sketch oldInstance = instance;
        if (oldInstance != null) return oldInstance;

        synchronized (Sketch.class) {
            oldInstance = instance;
            if (oldInstance != null) return oldInstance;

            Sketch newInstance = new Sketch(context);
            SLog.iff("Version %s %s(%d) -> %s",
                    BuildConfig.BUILD_TYPE, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE, newInstance.configuration.toString());

            Initializer initializer = SketchUtils.findInitializer(context);
            if (initializer != null) {
                initializer.onInitialize(context.getApplicationContext(), newInstance.configuration);
            }
            instance = newInstance;
            return newInstance;
        }
    }

    @NonNull
    public Configuration getConfiguration() {
        return configuration;
    }


    /**
     * Download image by uri, only supports http and https. Finally, the {@link DownloadHelper#commit()} method is called to submit
     */
    @NonNull
    public DownloadHelper download(@NonNull String uri, @Nullable DownloadListener listener) {
        return new DownloadHelper(this, uri, listener);
    }


    /**
     * Load image into memory by uri. Finally, the {@link LoadHelper#commit()} method is called to submit
     *
     * @see AndroidResUriModel
     * @see ApkIconUriModel
     * @see AppIconUriModel
     * @see AssetUriModel
     * @see Base64UriModel
     * @see Base64VariantUriModel
     * @see ContentUriModel
     * @see DrawableUriModel
     * @see FileUriModel
     * @see FileVariantUriModel
     * @see HttpUriModel
     * @see HttpsUriModel
     */
    // todo 支持异步和同步两种方式
    @NonNull
    public LoadHelper load(@NonNull String uri, @Nullable LoadListener listener) {
        return new LoadHelper(this, uri, listener);
    }

    /**
     * Load image into memory from asset resource. Finally, need the {@link LoadHelper#commit()} method is called to submit
     *
     * @see AssetUriModel
     */
    @NonNull
    public LoadHelper loadFromAsset(@NonNull String assetFileName, @Nullable LoadListener listener) {
        String uri = AssetUriModel.makeUri(assetFileName);
        return new LoadHelper(this, uri, listener);
    }

    /**
     * Load image into memory from drawable resource. Finally, need the {@link LoadHelper#commit()} method is called to submit
     *
     * @see DrawableUriModel
     */
    @NonNull
    public LoadHelper loadFromResource(@DrawableRes int drawableResId, @Nullable LoadListener listener) {
        String uri = DrawableUriModel.makeUri(drawableResId);
        return new LoadHelper(this, uri, listener);
    }

    /**
     * Load image into memory from {@link ContentProvider}. Finally, need the {@link LoadHelper#commit()} method is called to submit
     *
     * @see ContentUriModel
     */
    @NonNull
    public LoadHelper loadFromContent(@NonNull String uri, @Nullable LoadListener listener) {
        return new LoadHelper(this, uri, listener);
    }


    /**
     * Display image to {@link SketchView} by uri. Finally, need the {@link DisplayHelper#commit()} method is called to submit
     *
     * @see AndroidResUriModel
     * @see ApkIconUriModel
     * @see AppIconUriModel
     * @see AssetUriModel
     * @see Base64UriModel
     * @see Base64VariantUriModel
     * @see ContentUriModel
     * @see DrawableUriModel
     * @see FileUriModel
     * @see FileVariantUriModel
     * @see HttpUriModel
     * @see HttpsUriModel
     */
    @NonNull
    public DisplayHelper display(@NonNull String uri, @NonNull SketchView sketchView) {
        return new DisplayHelper(this, uri, sketchView);
    }

    /**
     * Display image to {@link SketchView} from asset resource. Finally, need the {@link DisplayHelper#commit()} method is called to submit
     *
     * @see AssetUriModel
     */
    @NonNull
    public DisplayHelper displayFromAsset(@NonNull String assetFileName, @NonNull SketchView sketchView) {
        String uri = AssetUriModel.makeUri(assetFileName);
        return new DisplayHelper(this, uri, sketchView);
    }

    /**
     * Display image to {@link SketchView} from drawable resource. Finally, need the {@link DisplayHelper#commit()} method is called to submit
     *
     * @see DrawableUriModel
     */
    @NonNull
    public DisplayHelper displayFromResource(@DrawableRes int drawableResId, @NonNull SketchView sketchView) {
        String uri = DrawableUriModel.makeUri(drawableResId);
        return new DisplayHelper(this, uri, sketchView);
    }

    /**
     * Display image to {@link SketchView} from asset {@link ContentProvider}. Finally, need the {@link DisplayHelper#commit()} method is called to submit
     *
     * @see ContentUriModel
     */
    @NonNull
    public DisplayHelper displayFromContent(@NonNull String uri, @NonNull SketchView sketchView) {
        return new DisplayHelper(this, uri, sketchView);
    }


    @Deprecated
    public void onTrimMemory(int level) {
        SLog.wf("Trim of memory, level= %s", SketchUtils.getTrimLevelName(level));

        configuration.getMemoryCache().trimMemory(level);
        configuration.getBitmapPool().trimMemory(level);
    }

    @Deprecated
    public void onLowMemory() {
        SLog.w("Memory is very low, clean memory cache and bitmap pool");

        configuration.getMemoryCache().clear();
        configuration.getBitmapPool().clear();
    }
}