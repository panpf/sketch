/*
 * Copyright (C) 2013 Peng fei Pan <sky@panpf.me>
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

import android.app.Application;
import android.content.ComponentCallbacks2;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import androidx.annotation.DrawableRes;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.ImageView;

import me.panpf.sketch.request.CancelCause;
import me.panpf.sketch.request.DisplayHelper;
import me.panpf.sketch.request.DisplayRequest;
import me.panpf.sketch.request.DownloadHelper;
import me.panpf.sketch.request.DownloadListener;
import me.panpf.sketch.request.LoadHelper;
import me.panpf.sketch.request.LoadListener;
import me.panpf.sketch.uri.AssetUriModel;
import me.panpf.sketch.uri.DrawableUriModel;
import me.panpf.sketch.util.SketchUtils;

/**
 * {@link Sketch} 是一个功能强大且全面的图片加载器，可以从网络或者本地加载图片，支持 gif、手势缩放以及分块显示超大图
 * <ul>
 * <li>{@link #display(String, SketchView)}：显示图片到 {@link SketchImageView} 上</li>
 * <li>{@link #load(String, LoadListener)}：加载图片到内存中</li>
 * <li>{@link #download(String, DownloadListener)}：下载图片到磁盘上</li>
 * </ul>
 */
public class Sketch {
    public static final String META_DATA_KEY_INITIALIZER = "SKETCH_INITIALIZER";

    private static volatile Sketch instance;

    private Configuration configuration;

    private Sketch(@NonNull Context context) {
        this.configuration = new Configuration(context);
    }

    /**
     * 获取 {@link Sketch} 实例
     *
     * @param context 用于初始化 {@link Sketch}
     * @return {@link Sketch}
     */
    @NonNull
    public static Sketch with(@NonNull Context context) {
        if (instance == null) {
            synchronized (Sketch.class) {
                if (instance == null) {
                    Sketch newInstance = new Sketch(context);
                    SLog.i(null, "Version %s %s(%d) -> %s",
                            BuildConfig.BUILD_TYPE, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE, newInstance.configuration.toString());

                    Initializer initializer = SketchUtils.findInitializer(context);
                    if (initializer != null) {
                        initializer.onInitialize(context.getApplicationContext(), newInstance.configuration);
                    }
                    instance = newInstance;
                }
            }
        }
        return instance;
    }

    /**
     * 取消请求
     *
     * @param sketchView 会通过 {@link SketchView} 的 {@link Drawable} 找到正在执行的请求，然后取消它
     * @return true：当前 {@link SketchView} 有正在执行的任务并且取消成功；false：当前 {@link SketchView} 没有正在执行的任务
     */
    @SuppressWarnings("unused")
    public static boolean cancel(@NonNull SketchView sketchView) {
        final DisplayRequest displayRequest = SketchUtils.findDisplayRequest(sketchView);
        if (displayRequest != null && !displayRequest.isFinished()) {
            displayRequest.cancel(CancelCause.BE_CANCELLED);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取配置对象
     *
     * @return {@link Configuration}
     */
    @NonNull
    public Configuration getConfiguration() {
        return configuration;
    }

    /**
     * 根据指定的 uri 下载图片
     *
     * @param uri      图片 uri，只支持 http:// 和 https://
     * @param listener 监听下载过程
     * @return {@link DownloadHelper} 你可以继续通过 {@link DownloadHelper} 设置参数，最后调用其 {@link DownloadHelper#commit()} 方法提交
     */
    @NonNull
    @SuppressWarnings("unused")
    public DownloadHelper download(@NonNull String uri, @Nullable DownloadListener listener) {
        return configuration.getHelperFactory().getDownloadHelper(this, uri, listener);
    }

    /**
     * 根据指定的 uri 加载图片到内存中
     *
     * @param uri      图片 uri，支持全部的 uri 类型，请参考 <a href="https://github.com/panpf/sketch/blob/master/docs/wiki/uri.md">https://github.com/panpf/sketch/blob/master/docs/wiki/uri.md</a>
     * @param listener 监听下载过程
     * @return {@link LoadHelper} 你可以继续通过 {@link LoadHelper} 设置参数，最后调用其 {@link LoadHelper#commit()} 方法提交
     */
    @NonNull
    public LoadHelper load(@NonNull String uri, @Nullable LoadListener listener) {
        return configuration.getHelperFactory().getLoadHelper(this, uri, listener);
    }

    /**
     * 加载 assets 资源图片
     *
     * @param assetFileName assets 文件夹下的图片文件的名称
     * @param listener      监听加载过程
     * @return {@link LoadHelper} 你可以继续通过 {@link LoadHelper} 设置参数，最后调用其 {@link LoadHelper#commit()} 方法提交
     */
    @NonNull
    @SuppressWarnings("unused")
    public LoadHelper loadFromAsset(@NonNull String assetFileName, @Nullable LoadListener listener) {
        String uri = AssetUriModel.makeUri(assetFileName);
        return configuration.getHelperFactory().getLoadHelper(this, uri, listener);
    }

    /**
     * 加载 drawable 资源图片
     *
     * @param drawableResId drawable 资源 id
     * @param listener      监听加载过程
     * @return {@link LoadHelper} 你可以继续通过 {@link LoadHelper} 设置参数，最后调用其 {@link LoadHelper#commit()} 方法提交
     */
    @NonNull
    @SuppressWarnings("unused")
    public LoadHelper loadFromResource(@DrawableRes int drawableResId, @Nullable LoadListener listener) {
        String uri = DrawableUriModel.makeUri(drawableResId);
        return configuration.getHelperFactory().getLoadHelper(this, uri, listener);
    }

    /**
     * 加载来自 {@link ContentProvider} 的图片
     *
     * @param uri      来自 {@link ContentProvider} 的图片 uri，例如：content://、file://，使用 {@link ContentResolver#openInputStream(Uri)} api 读取图片
     * @param listener 监听加载过程
     * @return {@link LoadHelper} 你可以继续通过 {@link LoadHelper} 设置参数，最后调用其 {@link LoadHelper#commit()} 方法提交
     */
    @NonNull
    @SuppressWarnings("unused")
    public LoadHelper loadFromContent(@NonNull String uri, @Nullable LoadListener listener) {
        return configuration.getHelperFactory().getLoadHelper(this, uri, listener);
    }

    /**
     * 根据指定的 uri 显示图片
     *
     * @param uri        图片 uri，支持全部的 uri 类型，请参考 <a href="https://github.com/panpf/sketch/blob/master/docs/wiki/uri.md">https://github.com/panpf/sketch/blob/master/docs/wiki/uri.md</a>
     * @param sketchView {@link Sketch} 对 {@link ImageView} 的规范接口，默认实现是 {@link SketchImageView}
     * @return {@link DisplayHelper} 你可以继续通过 {@link DisplayHelper} 设置参数，最后调用其 {@link DisplayHelper#commit()} 方法提交
     */
    @NonNull
    public DisplayHelper display(@NonNull String uri, @NonNull SketchView sketchView) {
        return configuration.getHelperFactory().getDisplayHelper(this, uri, sketchView);
    }

    /**
     * 显示 assets 资源图片
     *
     * @param assetFileName assets 文件夹下的图片文件的名称
     * @param sketchView    {@link Sketch} 对 {@link ImageView} 的规范接口，默认实现是 {@link SketchImageView}
     * @return {@link DisplayHelper} 你可以继续通过 {@link DisplayHelper} 设置参数，最后调用其 {@link DisplayHelper#commit()} 方法提交
     */
    @NonNull
    public DisplayHelper displayFromAsset(@NonNull String assetFileName, @NonNull SketchView sketchView) {
        String uri = AssetUriModel.makeUri(assetFileName);
        return configuration.getHelperFactory().getDisplayHelper(this, uri, sketchView);
    }

    /**
     * 显示 drawable 资源图片
     *
     * @param drawableResId drawable 资源 id
     * @param sketchView    {@link Sketch} 对 {@link ImageView} 的规范接口，默认实现是 {@link SketchImageView}
     * @return {@link DisplayHelper} 你可以继续通过 {@link DisplayHelper} 设置参数，最后调用其 {@link DisplayHelper#commit()} 方法提交
     */
    @NonNull
    public DisplayHelper displayFromResource(@DrawableRes int drawableResId, @NonNull SketchView sketchView) {
        String uri = DrawableUriModel.makeUri(drawableResId);
        return configuration.getHelperFactory().getDisplayHelper(this, uri, sketchView);
    }

    /**
     * 显示来自 {@link ContentProvider} 的图片
     *
     * @param uri        来自 {@link ContentProvider} 的图片 uri，例如：content://、file://，使用 {@link ContentResolver#openInputStream(Uri)} api 读取图片
     * @param sketchView {@link Sketch} 对 {@link ImageView} 的规范接口，默认实现是 {@link SketchImageView}
     * @return {@link DisplayHelper} 你可以继续通过 {@link DisplayHelper} 设置参数，最后调用其 {@link DisplayHelper#commit()} 方法提交
     */
    @NonNull
    public DisplayHelper displayFromContent(@NonNull String uri, @NonNull SketchView sketchView) {
        return configuration.getHelperFactory().getDisplayHelper(this, uri, sketchView);
    }

    /**
     * 修整内存缓存，4.0 以下你需要重写 {@link Application#onTrimMemory(int)} 方法，然后调用这个方法
     *
     * @param level 修剪级别，对应 APP 的不同状态，对应 {@link ComponentCallbacks2} 里的常量
     */
    @Keep
    public void onTrimMemory(int level) {
        SLog.w(null, "Trim of memory, level= %s", SketchUtils.getTrimLevelName(level));

        configuration.getMemoryCache().trimMemory(level);
        configuration.getBitmapPool().trimMemory(level);
    }

    /**
     * 当内存低时直接清空全部内存缓存，4.0 以下你需要重写 {@link Application#onLowMemory} 方法，然后调用这个方法
     */
    @Keep
    public void onLowMemory() {
        SLog.w(null, "Memory is very low, clean memory cache and bitmap pool");

        configuration.getMemoryCache().clear();
        configuration.getBitmapPool().clear();
    }
}