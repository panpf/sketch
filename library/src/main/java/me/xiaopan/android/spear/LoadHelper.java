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

package me.xiaopan.android.spear;

import android.util.Log;
import android.widget.ImageView;

import me.xiaopan.android.spear.process.ImageProcessor;
import me.xiaopan.android.spear.request.DownloadRequest;
import me.xiaopan.android.spear.request.FailCause;
import me.xiaopan.android.spear.request.Level;
import me.xiaopan.android.spear.request.LoadListener;
import me.xiaopan.android.spear.request.LoadRequest;
import me.xiaopan.android.spear.request.ProgressListener;
import me.xiaopan.android.spear.request.Request;
import me.xiaopan.android.spear.request.UriScheme;
import me.xiaopan.android.spear.util.ImageSize;

/**
 * LoadHelper
 */
public class LoadHelper {
    private static final String NAME = "DownloadHelper";

    protected Spear spear;
    protected String uri;
    protected String name;
    protected boolean enableDiskCache = DownloadRequest.DEFAULT_ENABLE_DISK_CACHE;
    protected ProgressListener progressListener;

    protected Level level = Level.NET;
    protected ImageSize maxsize;
    protected ImageSize resize;
    protected ImageProcessor imageProcessor;
    protected ImageView.ScaleType scaleType;
    protected LoadListener loadListener;

    /**
     * 创建加载请求生成器
     * @param spear Spear
     * @param uri 支持以下6种类型
     * <blockquote>“http://site.com/image.png“  // from Web
     * <br>“https://site.com/image.png“ // from Web
     * <br>“/mnt/sdcard/image.png“ // from SD card
     * <br>“content://media/external/audio/albumart/13“ // from content provider
     * <br>“assets://image.png“ // from assets
     * <br>“drawable://" + R.drawable.image // from drawables
     * </blockquote>
     */
    public LoadHelper(Spear spear, String uri) {
        this.spear = spear;
        this.uri = uri;
        this.maxsize = spear.getConfiguration().getImageSizeCalculator().getDefaultImageMaxsize(spear.getConfiguration().getContext());
    }

    /**
     * 设置名称，用于在log总区分请求
     * @param name 名称
     * @return LoadHelper
     */
    public LoadHelper name(String name){
        this.name = name;
        return this;
    }

    /**
     * 关闭硬盘缓存
     * @return LoadHelper
     */
    public LoadHelper disableDiskCache() {
        this.enableDiskCache = false;
        return this;
    }

    /**
     * 设置最大尺寸，在解码的时候会使用此Size来计算inSimpleSize
     * @param maxsize 最大尺寸
     * @return LoadHelper
     */
    public LoadHelper maxsize(ImageSize maxsize){
        this.maxsize = maxsize;
        return this;
    }

    /**
     * 设置最大尺寸，在解码的时候会使用此Size来计算inSimpleSize
     * @param width 宽
     * @param height 高
     * @return LoadHelper
     */
    public LoadHelper maxsize(int width, int height){
        this.maxsize = new ImageSize(width, height);
        return this;
    }

    /**
     * 裁剪图片，ImageProcessor会根据此宽高和ScaleType裁剪图片
     * @param resize 新的尺寸
     * @return LoadHelper
     */
    public LoadHelper resize(ImageSize resize){
        this.resize = resize;
        return this;
    }

    /**
     * 裁剪图片，ImageProcessor会根据此宽高和ScaleType裁剪图片
     * @param width 宽
     * @param height 高
     * @return LoadHelper
     */
    public LoadHelper resize(int width, int height){
        this.resize = new ImageSize(width, height);
        return this;
    }

    /**
     * 设置图片处理器，图片处理器会根据resize和ScaleType创建一张新的图片
     * @param processor 图片处理器
     * @return LoadHelper
     */
    public LoadHelper processor(ImageProcessor processor){
        this.imageProcessor = processor;
        return this;
    }

    /**
     * 设置加载监听器
     * @param loadListener 加载监听器
     * @return LoadHelper
     */
    public LoadHelper listener(LoadListener loadListener){
        this.loadListener = loadListener;
        return this;
    }

    /**
     * 设置ScaleType，ImageProcessor会根据resize和ScaleType创建一张新的图片
     * @param scaleType ScaleType
     * @return LoadHelper
     */
    public LoadHelper scaleType(ImageView.ScaleType scaleType){
        this.scaleType = scaleType;
        return this;
    }

    /**
     * 设置进度监听器
     * @param progressListener 进度监听器
     * @return LoadHelper
     */
    public LoadHelper progressListener(ProgressListener progressListener){
        this.progressListener = progressListener;
        return this;
    }

    /**
     * 设置加载级别
     * @param level 加载级别
     * @return LoadHelper
     */
    public LoadHelper level(Level level){
        if(level != null){
            this.level = level;
        }
        return this;
    }

    /**
     * 设置加载参数
     * @param options 加载参数
     * @return LoadHelper
     */
    public LoadHelper options(LoadOptions options){
        if(options == null){
            return this;
        }

        if(options.isEnableDiskCache() != DownloadRequest.DEFAULT_ENABLE_DISK_CACHE){
            this.enableDiskCache = options.isEnableDiskCache();
        }
        if(this.maxsize == null){
            this.maxsize = options.getMaxsize();
        }
        if(this.resize == null){
            this.resize = options.getResize();
        }
        if(this.scaleType == null){
            this.scaleType = options.getScaleType();
        }
        if(this.imageProcessor == null){
            this.imageProcessor = options.getImageProcessor();
        }

        return this;
    }

    /**
     * 设置加载参数，你只需要提前将LoadOptions通过Spear.putOptions()方法存起来，然后在这里指定其名称即可
     * @param optionsName 参数名称
     * @return LoadHelper
     */
    public LoadHelper options(Enum<?> optionsName){
        return options((LoadOptions) Spear.getOptions(optionsName));
    }

    /**
     * 执行请求
     * @return Request 你可以通过Request来查看请求的状态或者取消这个请求
     */
    public Request fire() {
        // 执行请求
        if(loadListener != null){
            loadListener.onStarted();
        }

        // 验证uri参数
        if(uri == null || "".equals(uri.trim())){
            if(Spear.isDebugMode()){
                Log.e(Spear.TAG, NAME + " - " + "uri不能为null或空");
            }
            if(loadListener != null){
                loadListener.onFailed(FailCause.URI_NULL_OR_EMPTY);
            }
            return null;
        }

        // 过滤掉不支持的URI协议类型
        UriScheme uriScheme = UriScheme.valueOfUri(uri);
        if(uriScheme == null){
            if(Spear.isDebugMode()){
                Log.e(Spear.TAG, NAME + " - " + "未知的协议类型" + " URI" + "=" + uri);
            }
            if(loadListener != null){
                loadListener.onFailed(FailCause.URI_NO_SUPPORT);
            }
            return null;
        }

        // 创建请求
        LoadRequest request = new LoadRequest(spear, uri, uriScheme);

        request.setName(name != null ? name : uri);
        request.setProgressListener(progressListener);
        request.setLevel(level);

        request.setEnableDiskCache(enableDiskCache);

        request.setMaxsize(maxsize);
        request.setResize(resize);
        request.setImageProcessor(imageProcessor);
        request.setScaleType(scaleType);
        request.setLoadListener(loadListener);

        request.runDispatch();

        return request;
    }
}
