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

package me.xiaopan.sketch;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.os.Build;

import java.util.HashMap;
import java.util.Map;

import me.xiaopan.sketch.feature.ImagePreprocessor;
import me.xiaopan.sketch.request.CancelCause;
import me.xiaopan.sketch.request.DisplayHelper;
import me.xiaopan.sketch.request.DisplayOptions;
import me.xiaopan.sketch.request.DisplayParams;
import me.xiaopan.sketch.request.DisplayRequest;
import me.xiaopan.sketch.request.DownloadHelper;
import me.xiaopan.sketch.request.DownloadListener;
import me.xiaopan.sketch.request.DownloadOptions;
import me.xiaopan.sketch.request.ImageViewInterface;
import me.xiaopan.sketch.request.LoadHelper;
import me.xiaopan.sketch.request.LoadListener;
import me.xiaopan.sketch.request.LoadOptions;
import me.xiaopan.sketch.request.UriScheme;
import me.xiaopan.sketch.util.SketchUtils;

/**
 * Sketch是一个功能强大且全面的图片加载器，可以从网络或者本地加载图片，支持gif、手势缩放以及分块显示超大图
 * <ul>
 * <li>display()：显示图片到ImageView上</li>
 * <li>load()：加载图片到内存中</li>
 * <li>download()：下载图片到磁盘上</li>
 * </ul>
 */
public class Sketch {
    public static final String TAG = "Sketch";

    private static Sketch instance;
    private static Map<Enum<?>, Object> optionsMap;

    private Configuration configuration;

    private Sketch(Context context) {
        SLog.i(String.format("Version %s %s(%d)", BuildConfig.BUILD_TYPE, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE));
        this.configuration = new Configuration(context);
    }

    /**
     * 获取Sketch实例
     *
     * @param context 用于初始化Sketch
     * @return Sketch
     */
    public static Sketch with(Context context) {
        if (instance == null) {
            synchronized (Sketch.class) {
                if (instance == null) {
                    instance = new Sketch(context);
                }
            }
        }
        return instance;
    }

    /**
     * 获取配置
     *
     * @return Configuration
     */
    public Configuration getConfiguration() {
        return configuration;
    }


    /**
     * 下载图片
     *
     * @param uri      图片Uri，支持以下几种
     *                 <ul>
     *                 <li>http://site.com/image.png  // from Web</li>
     *                 <li>https://site.com/image.png // from Web</li>
     *                 </ul>
     * @param listener 监听下载过程
     * @return DownloadHelper 你可以继续通过DownloadHelper设置一下参数，最后调用其commit()方法提交即可
     */
    @SuppressWarnings("unused")
    public DownloadHelper download(String uri, DownloadListener listener) {
        return configuration.getHelperFactory().getDownloadHelper(this, uri).listener(listener);
    }


    /**
     * 根据URI加载图片
     *
     * @param uri      图片Uri，支持以下几种
     *                 <ul>
     *                 <li>http://site.com/image.png    // from Web</li>
     *                 <li>https://site.com/image.png   // from Web</li>
     *                 <li>file:///mnt/sdcard/image.png // from SD card</li>
     *                 <li>/mnt/sdcard/image.png    // from SD card</li>
     *                 <li>/mnt/sdcard/app.apk  // from SD card apk file</li>
     *                 <li>content://media/external/audio/albumart/13   // from content provider</li>
     *                 <li>asset://image.png    // from assets</li>
     *                 <li>"drawable://" + R.drawable.image // from drawables (only images, non-9patch)</li>
     *                 </ul>
     * @param listener 监听下载过程
     * @return LoadHelper 你可以继续通过LoadHelper设置一下参数，最后调用其commit()方法提交即可
     */
    public LoadHelper load(String uri, LoadListener listener) {
        return configuration.getHelperFactory().getLoadHelper(this, uri).listener(listener);
    }

    /**
     * 加载Asset中的图片
     *
     * @param fileName asset中图片文件的名称
     * @param listener 监听加载过程
     * @return LoadHelper 你可以继续通过LoadHelper设置一下参数，最后调用其commit()方法提交即可
     */
    @SuppressWarnings("unused")
    public LoadHelper loadFromAsset(String fileName, LoadListener listener) {
        String uri = UriScheme.ASSET.createUri(fileName);
        return configuration.getHelperFactory().getLoadHelper(this, uri).listener(listener);
    }

    /**
     * 加载资源中的图片
     *
     * @param drawableResId 图片资源的ID
     * @param listener      监听加载过程
     * @return LoadHelper 你可以继续通过LoadHelper设置一下参数，最后调用其commit()方法提交即可
     */
    @SuppressWarnings("unused")
    public LoadHelper loadFromResource(int drawableResId, LoadListener listener) {
        String uri = UriScheme.DRAWABLE.createUri(String.valueOf(drawableResId));
        return configuration.getHelperFactory().getLoadHelper(this, uri).listener(listener);
    }

    /**
     * 加载URI指向的图片
     *
     * @param uri      图片Uri，会通过ContentResolver().openInputStream(Uri)方法来读取图片
     * @param listener 监听加载过程
     * @return LoadHelper 你可以继续通过LoadHelper设置一下参数，最后调用其commit()方法提交即可
     */
    @SuppressWarnings("unused")
    public LoadHelper loadFromURI(Uri uri, LoadListener listener) {
        return configuration.getHelperFactory().getLoadHelper(this, uri.toString()).listener(listener);
    }

    /**
     * 加载已安装APP的图标
     *
     * @param packageName 已安装APP的包名
     * @param versionCode 已安装APP的版本号
     * @param listener    监听加载过程
     * @return LoadHelper 你可以继续通过LoadHelper设置一下参数，最后调用其commit()方法提交即可
     */
    @SuppressWarnings("unused")
    public LoadHelper loadInstalledAppIcon(String packageName, int versionCode, LoadListener listener) {
        String uri = ImagePreprocessor.createInstalledAppIconUri(packageName, versionCode);
        return configuration.getHelperFactory().getLoadHelper(this, uri).listener(listener);
    }


    /**
     * 显示图片
     *
     * @param uri 图片Uri，支持以下几种
     *                 <ul>
     *                 <li>http://site.com/image.png    // from Web</li>
     *                 <li>https://site.com/image.png   // from Web</li>
     *                 <li>file:///mnt/sdcard/image.png // from SD card</li>
     *                 <li>/mnt/sdcard/image.png    // from SD card</li>
     *                 <li>/mnt/sdcard/app.apk  // from SD card apk file</li>
     *                 <li>content://media/external/audio/albumart/13   // from content provider</li>
     *                 <li>asset://image.png    // from assets</li>
     *                 <li>"drawable://" + R.drawable.image // from drawables (only images, non-9patch)</li>
     *                 </ul>
     * @param imageViewInterface 默认实现是SketchImageView
     * @return DisplayHelper 你可以继续通过DisplayHelper设置一下参数，最后调用其commit()方法提交即可
     */
    public DisplayHelper display(String uri, ImageViewInterface imageViewInterface) {
        return configuration.getHelperFactory().getDisplayHelper(this, uri, imageViewInterface);
    }

    /**
     * 显示Asset中的图片
     *
     * @param fileName           asset中图片文件的名称
     * @param imageViewInterface 默认实现是SketchImageView
     * @return DisplayHelper 你可以继续通过DisplayHelper设置一下参数，最后调用其commit()方法提交即可
     */
    public DisplayHelper displayFromAsset(String fileName, ImageViewInterface imageViewInterface) {
        return configuration.getHelperFactory().getDisplayHelper(this, UriScheme.ASSET.createUri(fileName), imageViewInterface);
    }

    /**
     * 显示资源中的图片
     *
     * @param drawableResId      图片资源的ID
     * @param imageViewInterface 默认实现是SketchImageView
     * @return DisplayHelper 你可以继续通过DisplayHelper设置一下参数，最后调用其commit()方法提交即可
     */
    public DisplayHelper displayFromResource(int drawableResId, ImageViewInterface imageViewInterface) {
        return configuration.getHelperFactory().getDisplayHelper(this, UriScheme.DRAWABLE.createUri(String.valueOf(drawableResId)), imageViewInterface);
    }

    /**
     * 显示URI指向的图片
     *
     * @param uri                图片Uri，会通过ContentResolver().openInputStream(Uri)方法来读取图片
     * @param imageViewInterface 默认实现是SketchImageView
     * @return DisplayHelper 你可以继续通过DisplayHelper设置一下参数，最后调用其commit()方法提交即可
     */
    public DisplayHelper displayFromURI(Uri uri, ImageViewInterface imageViewInterface) {
        return configuration.getHelperFactory().getDisplayHelper(this, uri != null ? uri.toString() : null, imageViewInterface);
    }

    /**
     * 显示已安装APP的图标
     *
     * @param packageName        已安装APP的包名
     * @param versionCode        已安装APP的版本号
     * @param imageViewInterface 默认实现是SketchImageView
     * @return DisplayHelper 你可以继续通过DisplayHelper设置一下参数，最后调用其commit()方法提交即可
     */
    public DisplayHelper displayInstalledAppIcon(String packageName, int versionCode, ImageViewInterface imageViewInterface) {
        return configuration.getHelperFactory().getDisplayHelper(this, ImagePreprocessor.createInstalledAppIconUri(packageName, versionCode), imageViewInterface);
    }

    /**
     * 显示图片，主要用于配合SketchImageView兼容RecyclerView以及点击重试功能
     *
     * @param displayParams      显示参数缓存
     * @param imageViewInterface 默认实现是SketchImageView
     * @return DisplayHelper 你可以继续通过DisplayHelper设置一下参数，最后调用其commit()方法提交即可
     */
    public DisplayHelper display(DisplayParams displayParams, ImageViewInterface imageViewInterface) {
        return configuration.getHelperFactory().getDisplayHelper(this, displayParams, imageViewInterface);
    }

    /**
     * 修整内存缓存，4.0以下你需要重写Application的onTrimMemory(int)方法，然后调用这个方法
     *
     * @param level 修剪级别，对应APP的不同状态，对应ComponentCallbacks2里的常量
     * @see android.content.ComponentCallbacks2
     */
    public void onTrimMemory(int level) {
        // ICE_CREAM_SANDWICH以上版本已经自动注册了onTrimMemory监听，因此无需再在你的Application的onTrimMemory方法中调用此方法
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            StackTraceElement[] stackTraceElements = new Exception().getStackTrace();
            if (!SketchUtils.invokeIn(stackTraceElements, Application.class, "onTrimMemory")) {
                return;
            }
        }

        if (SLogType.BASE.isEnabled()) {
            SLog.w(SLogType.BASE, "Trim of memory, level= %s", SketchUtils.getTrimLevelName(level));
        }

        configuration.getMemoryCache().trimMemory(level);
        configuration.getBitmapPool().trimMemory(level);
    }

    /**
     * 当内存低时直接清空全部内存缓存，4.0以下你需要重写Application的onLowMemory方法，然后调用这个方法
     */
    public void onLowMemory() {
        // ICE_CREAM_SANDWICH以上版本已经自动注册了onLowMemory监听，因此无需再在你的Application的onLowMemory方法中调用此方法
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            StackTraceElement[] stackTraceElements = new Exception().getStackTrace();
            if (!SketchUtils.invokeIn(stackTraceElements, Application.class, "onLowMemory")) {
                return;
            }
        }

        if (SLogType.BASE.isEnabled()) {
            SLog.w(SLogType.BASE, "Memory is very low, clean memory cache and bitmap pool");
        }

        configuration.getMemoryCache().clear();
        configuration.getBitmapPool().clear();
    }


    /**
     * 取消请求
     *
     * @param imageViewInterface 会通过ImageViewInterface的Drawable找到正在执行的请求，然后取消它
     * @return true：当前ImageView有正在执行的任务并且取消成功；false：当前ImageView没有正在执行的任务
     */
    public static boolean cancel(ImageViewInterface imageViewInterface) {
        final DisplayRequest displayRequest = SketchUtils.findDisplayRequest(imageViewInterface);
        if (displayRequest != null && !displayRequest.isFinished()) {
            displayRequest.cancel(CancelCause.BE_CANCELLED);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 安装选项Map
     */
    private static void installOptionsMap() {
        if (optionsMap == null) {
            synchronized (Sketch.class) {
                if (optionsMap == null) {
                    optionsMap = new HashMap<Enum<?>, Object>();
                }
            }
        }
    }

    /**
     * 存入一个下载选项
     *
     * @param optionsName 选项名称
     * @param options     下载选项
     */
    @SuppressWarnings("unused")
    public static void putOptions(Enum<?> optionsName, DownloadOptions options) {
        installOptionsMap();
        optionsMap.put(optionsName, options);
    }

    /**
     * 存入一个加载选项
     *
     * @param optionsName 选项名称
     * @param options     加载选项
     */
    @SuppressWarnings("unused")
    public static void putOptions(Enum<?> optionsName, LoadOptions options) {
        installOptionsMap();
        optionsMap.put(optionsName, options);
    }

    /**
     * 存入一个显示选项
     *
     * @param optionsName 选项名称
     * @param options     显示选项
     */
    public static void putOptions(Enum<?> optionsName, DisplayOptions options) {
        installOptionsMap();
        optionsMap.put(optionsName, options);
    }

    /**
     * 获取之前存入的下载选项
     *
     * @param optionsName 选项名称
     */
    public static DownloadOptions getDownloadOptions(Enum<?> optionsName) {
        return optionsMap != null ? (DownloadOptions) optionsMap.get(optionsName) : null;
    }

    /**
     * 获取之前存入的加载选项
     *
     * @param optionsName 选项名称
     */
    public static LoadOptions getLoadOptions(Enum<?> optionsName) {
        return optionsMap != null ? (LoadOptions) optionsMap.get(optionsName) : null;
    }

    /**
     * 获取之前存入的显示选项
     *
     * @param optionsName 选项名称
     */
    public static DisplayOptions getDisplayOptions(Enum<?> optionsName) {
        return optionsMap != null ? (DisplayOptions) optionsMap.get(optionsName) : null;
    }
}