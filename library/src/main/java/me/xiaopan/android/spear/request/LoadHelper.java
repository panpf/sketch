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

package me.xiaopan.android.spear.request;

import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;

import me.xiaopan.android.spear.Spear;
import me.xiaopan.android.spear.process.CutImageProcessor;
import me.xiaopan.android.spear.process.ImageProcessor;
import me.xiaopan.android.spear.util.FailureCause;
import me.xiaopan.android.spear.util.ImageSize;
import me.xiaopan.android.spear.util.Scheme;

/**
 * LoadHelper
 */
public class LoadHelper {
    private static final String LOG_TAG = LoadRequest.class.getSimpleName();

    private Spear spear;
    private String uri;

    private long diskCacheTimeout;
    private boolean enableDiskCache = true;

    private ImageSize maxsize;
    private ImageSize resize;
    private ImageProcessor imageProcessor;
    private ImageView.ScaleType scaleType;

    private LoadListener loadListener;
    private ProgressListener progressListener;

    /**
     * 创建加载请求生成器
     * @param spear Spear
     * @param uri 支持以下6种类型
     * <blockquote>“http://site.com/image.png“  // from Web
     * <br>“https://site.com/image.png“ // from Web
     * <br>“file:///mnt/sdcard/image.png“ // from SD card
     * <br>“content://media/external/audio/albumart/13“ // from content provider
     * <br>“assets://image.png“ // from assets
     * <br>“drawable://" + R.drawable.image // from drawables
     * </blockquote>
     */
    public LoadHelper(Spear spear, String uri) {
        this.spear = spear;
        this.uri = uri;
        DisplayMetrics displayMetrics = spear.getContext().getResources().getDisplayMetrics();
        this.maxsize = new ImageSize((int) (displayMetrics.widthPixels*1.5f), (int) (displayMetrics.heightPixels*1.5f));
    }

    /**
     * 关闭硬盘缓存
     * @return Builder
     */
    public LoadHelper disableDiskCache() {
        this.enableDiskCache = false;
        return this;
    }

    /**
     * 设置磁盘缓存超时时间
     * @param diskCacheTimeout 磁盘缓存超时时间，单位毫秒，小于等于0表示永久有效
     * @return Builder
     */
    public LoadHelper diskCacheTimeout(long diskCacheTimeout) {
        this.diskCacheTimeout = diskCacheTimeout;
        return this;
    }

    /**
     * 设置最大尺寸，在解码的时候会使用此Size来计算inSimpleSize
     * @param maxsize 最大尺寸
     * @return Builder
     */
    public LoadHelper maxsize(ImageSize maxsize){
        this.maxsize = maxsize;
        return this;
    }

    /**
     * 设置最大尺寸，在解码的时候会使用此Size来计算inSimpleSize
     * @param width 宽
     * @param height 高
     * @return Builder
     */
    public LoadHelper maxsize(int width, int height){
        this.maxsize = new ImageSize(width, height);
        return this;
    }

    /**
     * 重新修改宽高，BitmapProcessor会根据此宽高和ScaleType创建一张新的图片
     * @param resize 新的尺寸
     * @return Helper
     */
    public LoadHelper resize(ImageSize resize){
        this.resize = resize;
        if(this.resize != null && imageProcessor == null){
            imageProcessor = new CutImageProcessor();
        }
        return this;
    }

    /**
     * 重新修改宽高，BitmapProcessor会根据此宽高和ScaleType创建一张新的图片
     * @param width 宽
     * @param height 高
     * @return Helper
     */
    public LoadHelper resize(int width, int height){
        this.resize = new ImageSize(width, height);
        if(imageProcessor == null){
            imageProcessor = new CutImageProcessor();
        }
        return this;
    }

    /**
     * 设置图片处理器，图片处理器会根据resize和ScaleType创建一张新的图片
     * @param processor 图片处理器
     * @return Helper
     */
    public LoadHelper processor(ImageProcessor processor){
        this.imageProcessor = processor;
        return this;
    }

    /**
     * 设置加载监听器
     * @param loadListener 加载监听器
     * @return Helper
     */
    public LoadHelper listener(LoadListener loadListener){
        this.loadListener = loadListener;
        return this;
    }

    /**
     * 设置ScaleType，BitmapProcessor会根据resize和ScaleType创建一张新的图片
     * @param scaleType ScaleType
     * @return Helper
     */
    public LoadHelper scaleType(ImageView.ScaleType scaleType){
        this.scaleType = scaleType;
        return this;
    }

    /**
     * 设置进度监听器
     * @param progressListener 进度监听器
     * @return Helper
     */
    public LoadHelper progressListener(ProgressListener progressListener){
        this.progressListener = progressListener;
        return this;
    }

    /**
     * 设置加载参数
     * @param options 加载参数
     * @return Helper
     */
    public LoadHelper options(LoadOptions options){
        if(options == null){
            return null;
        }

        this.enableDiskCache = options.isEnableDiskCache();
        this.diskCacheTimeout = options.getDiskCacheTimeout();

        this.maxsize = options.getMaxsize();
        this.resize = options.getResize();
        this.scaleType = options.getScaleType();
        this.imageProcessor = options.getImageProcessor();

        return this;
    }

    /**
     * 设置加载参数，你只需要提前将LoadOptions通过Spear.putOptions()方法存起来，然后在这里指定其名称即可
     * @param optionsName 参数名称
     * @return Helper
     */
    public LoadHelper options(Enum<?> optionsName){
        return options((LoadOptions) Spear.getOptions(optionsName));
    }

    /**
     * 执行请求
     * @return RequestFuture 你可以通过RequestFuture来查看请求的状态或者取消这个请求
     */
    public RequestFuture fire() {
        // 执行请求
        if(loadListener != null){
            loadListener.onStarted();
        }

        // 验证uri参数
        if(uri == null || "".equals(uri.trim())){
            if(spear.isDebugMode()){
                Log.e(Spear.LOG_TAG, LOG_TAG + "：" + "uri不能为null或空");
            }
            if(loadListener != null){
                loadListener.onFailed(FailureCause.URI_NULL_OR_EMPTY);
            }
            return null;
        }

        // 过滤掉不支持的URI协议类型
        Scheme scheme = Scheme.valueOfUri(uri);
        if(scheme == Scheme.UNKNOWN){
            if(spear.isDebugMode()){
                Log.e(Spear.LOG_TAG, LOG_TAG + "：" + "未知的协议类型" + " URI" + "=" + uri);
            }
            if(loadListener != null){
                loadListener.onFailed(FailureCause.URI_NO_SUPPORT);
            }
            return null;
        }

        // 创建请求
        LoadRequest request = new LoadRequest();

        request.uri = uri;
        request.name = uri;
        request.spear = spear;
        request.scheme = scheme;
        request.enableDiskCache = enableDiskCache;
        request.diskCacheTimeout = diskCacheTimeout;

        request.maxsize = maxsize;
        request.resize = resize;
        request.imageProcessor = imageProcessor;
        request.scaleType = scaleType;

        request.loadListener = loadListener;
        request.loadProgressListener = progressListener;

        spear.getRequestExecutor().execute(request);
        return new RequestFuture(request);
    }
}
