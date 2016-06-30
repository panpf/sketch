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

import android.content.Context;
import android.os.Build;
import android.util.Log;

import me.xiaopan.sketch.cache.DiskCache;
import me.xiaopan.sketch.cache.LruDiskCache;
import me.xiaopan.sketch.cache.LruMemoryCache;
import me.xiaopan.sketch.cache.MemoryCache;
import me.xiaopan.sketch.decode.DefaultImageDecoder;
import me.xiaopan.sketch.decode.ImageDecoder;
import me.xiaopan.sketch.display.DefaultImageDisplayer;
import me.xiaopan.sketch.display.ImageDisplayer;
import me.xiaopan.sketch.feature.ErrorCallback;
import me.xiaopan.sketch.feature.HelperFactory;
import me.xiaopan.sketch.feature.ImagePreprocessor;
import me.xiaopan.sketch.feature.ImageSizeCalculator;
import me.xiaopan.sketch.feature.MobileNetworkGlobalPauseDownload;
import me.xiaopan.sketch.feature.RequestFactory;
import me.xiaopan.sketch.feature.ResizeCalculator;
import me.xiaopan.sketch.http.HttpClientStack;
import me.xiaopan.sketch.http.HttpStack;
import me.xiaopan.sketch.http.HurlStack;
import me.xiaopan.sketch.process.ImageProcessor;
import me.xiaopan.sketch.process.ResizeImageProcessor;
import me.xiaopan.sketch.request.RequestExecutor;
import me.xiaopan.sketch.util.SketchUtils;

public class Configuration {
    protected String logName = "Configuration";

    private Context context;    // 上下文
    private DiskCache diskCache;    // 磁盘缓存
    private HttpStack httpStack;    // 网络
    private MemoryCache memoryCache;    //图片内存缓存
    private MemoryCache placeholderImageMemoryCache;    // 占位图内存缓存器
    private ImageDecoder imageDecoder;    //图片解码器
    private HelperFactory helperFactory;    // 协助器工厂
    private ErrorCallback errorCallback;    // 错误回调
    private ImageDisplayer defaultImageDisplayer;   // 默认的图片显示器，当DisplayRequest中没有指定显示器的时候就会用到
    private ImageProcessor resizeImageProcessor;    // Resize图片处理器
    private RequestFactory requestFactory;  // 请求工厂
    private RequestExecutor requestExecutor;    //请求执行器
    private ResizeCalculator resizeCalculator;  // resize计算器
    private ImagePreprocessor imagePreprocessor;    // 本地图片预处理器
    private ImageSizeCalculator imageSizeCalculator; // 图片尺寸计算器

    private boolean globalPauseLoad;   // 全局暂停加载新图片，开启后将只从内存缓存中找寻图片，只影响display请求
    private boolean globalPauseDownload;   // 全局暂停下载新图片，开启后将不再从网络下载新图片，只影响display请求
    private boolean globalLowQualityImage; // 全局使用低质量的图片
    private boolean globalDisableCacheInDisk;   // 全局禁用磁盘缓存
    private boolean globalDisableCacheInMemory; // 全局禁用内存缓存
    private boolean globalInPreferQualityOverSpeed;   // false:解码时优先考虑速度;true:解码时优先考虑质量 (默认false)
    private MobileNetworkGlobalPauseDownload mobileNetworkGlobalPauseDownload;

    public Configuration(Context tempContext) {
        this.context = tempContext.getApplicationContext();

        // LruDiskCache需要依赖所以先创建
        this.errorCallback = new ErrorCallback(context);

        this.httpStack = Build.VERSION.SDK_INT >= 9 ? new HurlStack() : new HttpClientStack();
        this.diskCache = new LruDiskCache(context, this, 1, DiskCache.DISK_CACHE_MAX_SIZE);
        this.memoryCache = LruMemoryCache.create(context);
        this.imageDecoder = new DefaultImageDecoder();
        this.helperFactory = new HelperFactory();
        this.requestFactory = new RequestFactory();
        this.requestExecutor = new RequestExecutor();
        this.resizeCalculator = new ResizeCalculator();
        this.imagePreprocessor = new ImagePreprocessor();
        this.imageSizeCalculator = new ImageSizeCalculator();
        this.defaultImageDisplayer = new DefaultImageDisplayer();
        this.resizeImageProcessor = new ResizeImageProcessor();
        this.placeholderImageMemoryCache = LruMemoryCache.createPlaceholder(context);

        if (Sketch.isDebugMode()) {
            Log.i(Sketch.TAG, getInfo());
        }
    }

    /**
     * 获取上下文
     */
    public Context getContext() {
        return context;
    }

    /**
     * 获取请求执行器
     */
    public RequestExecutor getRequestExecutor() {
        return requestExecutor;
    }

    /**
     * 设置请求执行器
     */
    @SuppressWarnings("unused")
    public Configuration setRequestExecutor(RequestExecutor newRequestExecutor) {
        if (newRequestExecutor != null) {
            RequestExecutor oldRequestExecutor = requestExecutor;
            requestExecutor = newRequestExecutor;
            if (oldRequestExecutor != null) {
                oldRequestExecutor.shutdown();
            }
            if (Sketch.isDebugMode()) {
                Log.i(Sketch.TAG, SketchUtils.concat(logName, ": ",
                        "set", " - ", "requestExecutor", " (", requestExecutor.getIdentifier(), ")"));
            }
        }
        return this;
    }

    /**
     * 获取磁盘缓存器
     */
    public DiskCache getDiskCache() {
        return diskCache;
    }

    /**
     * 设置磁盘缓存器
     */
    @SuppressWarnings("unused")
    public Configuration setDiskCache(DiskCache newDiskCache) {
        if (newDiskCache != null) {
            DiskCache oldDiskCache = diskCache;
            diskCache = newDiskCache;
            if (oldDiskCache != null) {
                oldDiskCache.close();
            }
            if (Sketch.isDebugMode()) {
                Log.i(Sketch.TAG, SketchUtils.concat(logName, ": ",
                        "set", " - ", "diskCache", " (", diskCache.getIdentifier(), ")"));
            }
        }
        return this;
    }

    /**
     * 获取内存缓存器
     */
    public MemoryCache getMemoryCache() {
        return memoryCache;
    }

    /**
     * 设置内存缓存器
     */
    @SuppressWarnings("unused")
    public Configuration setMemoryCache(MemoryCache memoryCache) {
        if (memoryCache != null) {
            MemoryCache oldMemoryCache = this.memoryCache;
            this.memoryCache = memoryCache;
            if (oldMemoryCache != null) {
                oldMemoryCache.close();
            }
            if (Sketch.isDebugMode()) {
                Log.i(Sketch.TAG, SketchUtils.concat(logName, ": ",
                        "set", " - ", "memoryCache", " (", memoryCache.getIdentifier(), ")"));
            }
        }
        return this;
    }

    /**
     * 获取占位图内存缓存器
     */
    public MemoryCache getPlaceholderImageMemoryCache() {
        return placeholderImageMemoryCache;
    }

    /**
     * 设置占位图内存缓存器
     */
    @SuppressWarnings("unused")
    public Configuration setPlaceholderImageMemoryCache(MemoryCache newPlaceholderImageMemoryCache) {
        if (newPlaceholderImageMemoryCache != null) {
            MemoryCache oldPlaceholderImageMemoryCache = placeholderImageMemoryCache;
            placeholderImageMemoryCache = newPlaceholderImageMemoryCache;
            if (oldPlaceholderImageMemoryCache != null) {
                oldPlaceholderImageMemoryCache.close();
            }
            if (Sketch.isDebugMode()) {
                Log.i(Sketch.TAG, SketchUtils.concat(logName, ": ",
                        "set", " - ", "placeholderImageMemoryCache", " (", placeholderImageMemoryCache.getIdentifier(), ")"));
            }
        }
        return this;
    }

    /**
     * 获取图片解码器
     */
    public ImageDecoder getImageDecoder() {
        return imageDecoder;
    }

    /**
     * 设置图片解码器
     */
    @SuppressWarnings("unused")
    public Configuration setImageDecoder(ImageDecoder imageDecoder) {
        if (imageDecoder != null) {
            this.imageDecoder = imageDecoder;
            if (Sketch.isDebugMode()) {
                Log.i(Sketch.TAG, SketchUtils.concat(logName, ": ",
                        "set", " - ", "imageDecoder", " (", imageDecoder.getIdentifier(), ")"));
            }
        }
        return this;
    }

    /**
     * 获取图片下载器
     */
    public HttpStack getHttpStack() {
        return httpStack;
    }

    /**
     * 设置图片下载器
     */
    @SuppressWarnings("unused")
    public Configuration setHttpStack(HttpStack httpStack) {
        if (httpStack != null) {
            this.httpStack = httpStack;
            if (Sketch.isDebugMode()) {
                Log.i(Sketch.TAG, SketchUtils.concat(logName, ": ",
                        "set", " - ", "httpStack", " (", httpStack.getIdentifier(), ")"));
            }
        }
        return this;
    }

    /**
     * 获取图片尺寸计算器
     */
    public ImageSizeCalculator getImageSizeCalculator() {
        return imageSizeCalculator;
    }

    /**
     * 获取图片尺寸计算器
     */
    @SuppressWarnings("unused")
    public Configuration setImageSizeCalculator(ImageSizeCalculator imageSizeCalculator) {
        if (imageSizeCalculator != null) {
            this.imageSizeCalculator = imageSizeCalculator;
            if (Sketch.isDebugMode()) {
                Log.i(Sketch.TAG, SketchUtils.concat(logName, ": ",
                        "set", " - ", "imageSizeCalculator", " (", imageSizeCalculator.getIdentifier(), ")"));
            }
        }
        return this;
    }

    /**
     * 获取默认的图片显示器
     */
    public ImageDisplayer getDefaultImageDisplayer() {
        return defaultImageDisplayer;
    }

    /**
     * 设置默认的图片处理器
     */
    @SuppressWarnings("unused")
    public Configuration setDefaultImageDisplayer(ImageDisplayer defaultImageDisplayer) {
        if (defaultImageDisplayer != null) {
            this.defaultImageDisplayer = defaultImageDisplayer;
            if (Sketch.isDebugMode()) {
                Log.i(Sketch.TAG, SketchUtils.concat(logName, ": ",
                        "set", " - ", "defaultImageDisplayer", " (", defaultImageDisplayer.getIdentifier(), ")"));
            }
        }
        return this;
    }

    /**
     * 获取Resize图片处理器
     */
    public ImageProcessor getResizeImageProcessor() {
        return resizeImageProcessor;
    }

    /**
     * 设置Resize图片处理器
     */
    @SuppressWarnings("unused")
    public Configuration setResizeImageProcessor(ImageProcessor resizeImageProcessor) {
        if (resizeImageProcessor != null) {
            this.resizeImageProcessor = resizeImageProcessor;
            if (Sketch.isDebugMode()) {
                Log.i(Sketch.TAG, SketchUtils.concat(logName, ": ",
                        "set", " - ", "resizeImageProcessor", " (", resizeImageProcessor.getIdentifier(), ")"));
            }
        }
        return this;
    }

    /**
     * 获取协助器工厂
     */
    public HelperFactory getHelperFactory() {
        return helperFactory;
    }

    /**
     * 设置协助器工厂
     */
    @SuppressWarnings("unused")
    public Configuration setHelperFactory(HelperFactory helperFactory) {
        if (helperFactory != null) {
            this.helperFactory = helperFactory;
            if (Sketch.isDebugMode()) {
                Log.i(Sketch.TAG, SketchUtils.concat(logName, ": ",
                        "set", " - ", "helperFactory", " (", helperFactory.getIdentifier(), ")"));
            }
        }
        return this;
    }

    /**
     * 获取请求工厂
     */
    public RequestFactory getRequestFactory() {
        return requestFactory;
    }

    /**
     * 设置请求工厂
     */
    @SuppressWarnings("unused")
    public Configuration setRequestFactory(RequestFactory requestFactory) {
        if (requestFactory != null) {
            this.requestFactory = requestFactory;
            if (Sketch.isDebugMode()) {
                Log.i(Sketch.TAG, SketchUtils.concat(logName, ": ",
                        "set", " - ", "requestFactory", " (", requestFactory.getIdentifier(), ")"));
            }
        }
        return this;
    }

    /**
     * 获取Resize计算器
     */
    public ResizeCalculator getResizeCalculator() {
        return resizeCalculator;
    }

    /**
     * 设置Resize计算器
     */
    @SuppressWarnings("unused")
    public Configuration setResizeCalculator(ResizeCalculator resizeCalculator) {
        if (resizeCalculator != null) {
            this.resizeCalculator = resizeCalculator;
            if (Sketch.isDebugMode()) {
                Log.i(Sketch.TAG, SketchUtils.concat(logName, ": ",
                        "set", " - ", "resizeCalculator", " (", resizeCalculator.getIdentifier(), ")"));
            }
        }
        return this;
    }

    /**
     * 是否全局暂停加载新图片，开启后将只从内存缓存中找寻图片，只影响display请求
     */
    public boolean isGlobalPauseLoad() {
        return globalPauseLoad;
    }

    /**
     * 设置全局暂停加载新图片，开启后将只从内存缓存中找寻图片，只影响display请求
     */
    public Configuration setGlobalPauseLoad(boolean globalPauseLoad) {
        if (this.globalPauseLoad != globalPauseLoad) {
            this.globalPauseLoad = globalPauseLoad;
            if (Sketch.isDebugMode()) {
                Log.i(Sketch.TAG, SketchUtils.concat(logName, ": ",
                        "set", " - ", "globalPauseLoad", " (", globalPauseLoad, ")"));
            }
        }
        return this;
    }

    /**
     * 是否全局暂停下载图片，开启后将不再从网络下载图片，只影响display请求和load请求
     */
    public boolean isGlobalPauseDownload() {
        return globalPauseDownload;
    }

    /**
     * 设置全局暂停下载图片，开启后将不再从网络下载图片，只影响display请求和load请求
     */
    public Configuration setGlobalPauseDownload(boolean globalPauseDownload) {
        if (this.globalPauseDownload != globalPauseDownload) {
            this.globalPauseDownload = globalPauseDownload;
            if (Sketch.isDebugMode()) {
                Log.i(Sketch.TAG, SketchUtils.concat(logName, ": ",
                        "set", " - ", "globalPauseDownload", " (", globalPauseDownload, ")"));
            }
        }
        return this;
    }

    /**
     * 是否移动网络下全局暂停下载，只影响display请求和load请求
     */
    @SuppressWarnings("unused")
    public boolean isMobileNetworkGlobalPauseDownload() {
        return mobileNetworkGlobalPauseDownload != null && mobileNetworkGlobalPauseDownload.isOpened();
    }

    /**
     * 设置是否开启移动网络下暂停下载的功能，只影响display请求和load请求
     */
    public Configuration setMobileNetworkGlobalPauseDownload(boolean mobileNetworkGlobalPauseDownload) {
        if(isMobileNetworkGlobalPauseDownload() != mobileNetworkGlobalPauseDownload){
            if (mobileNetworkGlobalPauseDownload) {
                if (this.mobileNetworkGlobalPauseDownload == null) {
                    this.mobileNetworkGlobalPauseDownload = new MobileNetworkGlobalPauseDownload(context);
                }
                this.mobileNetworkGlobalPauseDownload.setOpened(true);
            } else {
                if (this.mobileNetworkGlobalPauseDownload != null) {
                    this.mobileNetworkGlobalPauseDownload.setOpened(false);
                }
            }

            if (Sketch.isDebugMode()) {
                Log.i(Sketch.TAG, SketchUtils.concat(logName, ": ",
                        "set", " - ", "mobileNetworkGlobalPauseDownload", " (", isMobileNetworkGlobalPauseDownload(), ")"));
            }
        }
        return this;
    }

    /**
     * 是否全局使用低质量的图片
     */
    public boolean isGlobalLowQualityImage() {
        return globalLowQualityImage;
    }

    /**
     * 设置全局使用低质量的图片
     */
    public Configuration setGlobalLowQualityImage(boolean globalLowQualityImage) {
        if (this.globalLowQualityImage != globalLowQualityImage) {
            this.globalLowQualityImage = globalLowQualityImage;
            if (Sketch.isDebugMode()) {
                Log.i(Sketch.TAG, SketchUtils.concat(logName, ": ",
                        "set", " - ", "globalLowQualityImage", " (", globalLowQualityImage, ")"));
            }
        }
        return this;
    }

    /**
     * 全局解码时优先考虑速度还是质量 (默认优先考虑速度)
     *
     * @return true：质量；false：速度
     */
    public boolean isGlobalInPreferQualityOverSpeed() {
        return globalInPreferQualityOverSpeed;
    }

    /**
     * 设置全局解码时优先考虑速度还是质量 (默认优先考虑速度)
     *
     * @param globalInPreferQualityOverSpeed true：质量；false：速度
     */
    public Configuration setGlobalInPreferQualityOverSpeed(boolean globalInPreferQualityOverSpeed) {
        if (this.globalInPreferQualityOverSpeed != globalInPreferQualityOverSpeed) {
            this.globalInPreferQualityOverSpeed = globalInPreferQualityOverSpeed;
            if (Sketch.isDebugMode()) {
                Log.i(Sketch.TAG, SketchUtils.concat(logName, ": ",
                        "set", " - ", "globalInPreferQualityOverSpeed", " (", globalInPreferQualityOverSpeed, ")"));
            }
        }
        return this;
    }

    /**
     * 是否全局禁用磁盘缓存
     */
    public boolean isGlobalDisableCacheInDisk() {
        return globalDisableCacheInDisk;
    }

    /**
     * 设置是否全局禁用磁盘缓存
     */
    public Configuration setGlobalDisableCacheInDisk(boolean globalDisableCacheInDisk) {
        if (this.globalDisableCacheInDisk != globalDisableCacheInDisk) {
            this.globalDisableCacheInDisk = globalDisableCacheInDisk;
            if (Sketch.isDebugMode()) {
                Log.i(Sketch.TAG, SketchUtils.concat(logName, ": ",
                        "set", " - ", "globalDisableCacheInDisk", " (", globalDisableCacheInDisk, ")"));
            }
        }
        return this;
    }

    /**
     * 是否全局禁用内存缓存
     */
    public boolean isGlobalDisableCacheInMemory() {
        return globalDisableCacheInMemory;
    }

    /**
     * 设置是否全局禁用内存缓存
     */
    public Configuration setGlobalDisableCacheInMemory(boolean globalDisableCacheInMemory) {
        if (this.globalDisableCacheInMemory != globalDisableCacheInMemory) {
            this.globalDisableCacheInMemory = globalDisableCacheInMemory;
            if (Sketch.isDebugMode()) {
                Log.i(Sketch.TAG, SketchUtils.concat(logName, ": ",
                        "set", " - ", "globalDisableCacheInMemory", " (", globalDisableCacheInMemory, ")"));
            }
        }
        return this;
    }

    /**
     * 获取图片预处理器
     */
    public ImagePreprocessor getImagePreprocessor() {
        return imagePreprocessor;
    }

    /**
     * 设置图片预处理器
     */
    public Configuration setImagePreprocessor(ImagePreprocessor imagePreprocessor) {
        if (imagePreprocessor != null) {
            this.imagePreprocessor = imagePreprocessor;
            if (Sketch.isDebugMode()) {
                Log.i(Sketch.TAG, SketchUtils.concat(logName, ": ",
                        "set", " - ", "imagePreprocessor", " (", imagePreprocessor.getIdentifier(), ")"));
            }
        }
        return this;
    }

    /**
     * 获取错误回调
     */
    @SuppressWarnings("unused")
    public ErrorCallback getErrorCallback() {
        return errorCallback;
    }

    /**
     * 设置错误回调
     */
    @SuppressWarnings("unused")
    public void setErrorCallback(ErrorCallback errorCallback) {
        if (errorCallback != null) {
            this.errorCallback = errorCallback;
            if (Sketch.isDebugMode()) {
                Log.i(Sketch.TAG, SketchUtils.concat(logName, ": ",
                        "set", " - ", "errorCallback", " (", errorCallback.getIdentifier(), ")"));
            }
        }
    }

    public String getInfo() {
        StringBuilder builder = new StringBuilder();
        builder.append(logName).append(": ");

        if (diskCache != null) {
            if (builder.length() > 0) builder.append("\n");
            builder.append("diskCache");
            builder.append("：");
            diskCache.appendIdentifier(builder);
        }

        if (memoryCache != null) {
            if (builder.length() > 0) builder.append("\n");
            builder.append("memoryCache");
            builder.append("：");
            memoryCache.appendIdentifier(builder);
        }

        if (placeholderImageMemoryCache != null) {
            if (builder.length() > 0) builder.append("\n");
            builder.append("placeholderImageMemoryCache");
            builder.append("：");
            placeholderImageMemoryCache.appendIdentifier(builder);
        }

        if (imageDecoder != null) {
            if (builder.length() > 0) builder.append("\n");
            builder.append("imageDecoder");
            builder.append("：");
            imageDecoder.appendIdentifier(builder);
        }

        if (helperFactory != null) {
            if (builder.length() > 0) builder.append("\n");
            builder.append("helperFactory");
            builder.append("：");
            helperFactory.appendIdentifier(builder);
        }

        if (defaultImageDisplayer != null) {
            if (builder.length() > 0) builder.append("\n");
            builder.append("defaultImageDisplayer");
            builder.append("：");
            defaultImageDisplayer.appendIdentifier(builder);
        }

        if (resizeImageProcessor != null) {
            if (builder.length() > 0) builder.append("\n");
            builder.append("resizeImageProcessor");
            builder.append("：");
            resizeImageProcessor.appendIdentifier(builder);
        }

        if (requestFactory != null) {
            if (builder.length() > 0) builder.append("\n");
            builder.append("requestFactory");
            builder.append("：");
            requestFactory.appendIdentifier(builder);
        }

        if (httpStack != null) {
            if (builder.length() > 0) builder.append("\n");
            builder.append("httpStack");
            builder.append("：");
            httpStack.appendIdentifier(builder);
        }

        if (requestExecutor != null) {
            if (builder.length() > 0) builder.append("\n");
            builder.append("requestExecutor");
            builder.append("：");
            requestExecutor.appendIdentifier(builder);
        }

        if (imageSizeCalculator != null) {
            if (builder.length() > 0) builder.append("\n");
            builder.append("imageSizeCalculator");
            builder.append("：");
            imageSizeCalculator.appendIdentifier(builder);
        }

        if (resizeCalculator != null) {
            if (builder.length() > 0) builder.append("\n");
            builder.append("resizeCalculator");
            builder.append("：");
            resizeCalculator.appendIdentifier(builder);
        }

        if (imagePreprocessor != null) {
            if (builder.length() > 0) builder.append("\n");
            builder.append("imagePreprocessor");
            builder.append("：");
            imagePreprocessor.appendIdentifier(builder);
        }

        if (errorCallback != null) {
            if (builder.length() > 0) builder.append("\n");
            builder.append("errorCallback");
            builder.append("：");
            errorCallback.appendIdentifier(builder);
        }

        if (builder.length() > 0) builder.append("\n");
        builder.append("globalPauseLoad");
        builder.append("：");
        builder.append(globalPauseLoad);

        if (builder.length() > 0) builder.append("\n");
        builder.append("globalPauseDownload");
        builder.append("：");
        builder.append(globalPauseDownload);

        if (builder.length() > 0) builder.append("\n");
        builder.append("globalLowQualityImage");
        builder.append("：");
        builder.append(globalLowQualityImage);

        if (builder.length() > 0) builder.append("\n");
        builder.append("globalInPreferQualityOverSpeed");
        builder.append("：");
        builder.append(globalInPreferQualityOverSpeed);

        if (builder.length() > 0) builder.append("\n");
        builder.append("globalDisableCacheInMemory");
        builder.append("：");
        builder.append(globalDisableCacheInMemory);

        if (builder.length() > 0) builder.append("\n");
        builder.append("globalDisableCacheInDisk");
        builder.append("：");
        builder.append(globalDisableCacheInDisk);

        if (builder.length() > 0) builder.append("\n");
        builder.append("mobileNetworkGlobalPauseDownload");
        builder.append("：");
        builder.append(isMobileNetworkGlobalPauseDownload());

        return builder.toString();
    }
}
