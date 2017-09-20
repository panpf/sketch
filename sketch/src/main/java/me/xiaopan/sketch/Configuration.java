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

import android.annotation.TargetApi;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;

import me.xiaopan.sketch.cache.BitmapPool;
import me.xiaopan.sketch.cache.DiskCache;
import me.xiaopan.sketch.cache.LruBitmapPool;
import me.xiaopan.sketch.cache.LruDiskCache;
import me.xiaopan.sketch.cache.LruMemoryCache;
import me.xiaopan.sketch.cache.MemoryCache;
import me.xiaopan.sketch.cache.MemorySizeCalculator;
import me.xiaopan.sketch.decode.ImageDecoder;
import me.xiaopan.sketch.decode.ImageOrientationCorrector;
import me.xiaopan.sketch.decode.ImageSizeCalculator;
import me.xiaopan.sketch.decode.ProcessedImageCache;
import me.xiaopan.sketch.decode.ResizeCalculator;
import me.xiaopan.sketch.display.DefaultImageDisplayer;
import me.xiaopan.sketch.display.ImageDisplayer;
import me.xiaopan.sketch.http.HttpStack;
import me.xiaopan.sketch.http.HurlStack;
import me.xiaopan.sketch.http.ImageDownloader;
import me.xiaopan.sketch.optionsfilter.OptionsFilterRegistry;
import me.xiaopan.sketch.process.ImageProcessor;
import me.xiaopan.sketch.process.ResizeImageProcessor;
import me.xiaopan.sketch.request.FreeRideManager;
import me.xiaopan.sketch.request.HelperFactory;
import me.xiaopan.sketch.request.RequestExecutor;
import me.xiaopan.sketch.request.RequestFactory;
import me.xiaopan.sketch.uri.UriModelRegistry;

/**
 * Sketch 唯一配置类
 */
public final class Configuration {
    private static final String NAME = "Configuration";

    private Context context;

    private UriModelRegistry uriModelRegistry;
    private OptionsFilterRegistry optionsFilterRegistry;

    private DiskCache diskCache;
    private BitmapPool bitmapPool;
    private MemoryCache memoryCache;
    private ProcessedImageCache processedImageCache;

    private HttpStack httpStack;
    private ImageDecoder decoder;
    private ImageDownloader downloader;
    private ImageOrientationCorrector orientationCorrector;

    private ImageDisplayer defaultDisplayer;
    private ImageProcessor resizeProcessor;
    private ResizeCalculator resizeCalculator;
    private ImageSizeCalculator sizeCalculator;

    private RequestExecutor executor;
    private FreeRideManager freeRideManager;
    private HelperFactory helperFactory;
    private RequestFactory requestFactory;
    private ErrorTracker errorTracker;

    Configuration(@NonNull Context context) {
        context = context.getApplicationContext();
        this.context = context;

        this.uriModelRegistry = new UriModelRegistry();
        this.optionsFilterRegistry = new OptionsFilterRegistry();

        // 由于默认的缓存文件名称从 URLEncoder 加密变成了 MD5 所以这里要升级一下版本号，好清除旧的缓存
        this.diskCache = new LruDiskCache(context, this, 2, DiskCache.DISK_CACHE_MAX_SIZE);
        MemorySizeCalculator memorySizeCalculator = new MemorySizeCalculator(context);
        this.bitmapPool = new LruBitmapPool(context, memorySizeCalculator.getBitmapPoolSize());
        this.memoryCache = new LruMemoryCache(context, memorySizeCalculator.getMemoryCacheSize());

        this.decoder = new ImageDecoder();
        this.executor = new RequestExecutor();
        this.httpStack = new HurlStack();
        this.downloader = new ImageDownloader();
        this.sizeCalculator = new ImageSizeCalculator();
        this.freeRideManager = new FreeRideManager();
        this.resizeProcessor = new ResizeImageProcessor();
        this.resizeCalculator = new ResizeCalculator();
        this.defaultDisplayer = new DefaultImageDisplayer();
        this.processedImageCache = new ProcessedImageCache();
        this.orientationCorrector = new ImageOrientationCorrector();

        this.helperFactory = new HelperFactory();
        this.requestFactory = new RequestFactory();
        this.errorTracker = new ErrorTracker(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            context.getApplicationContext().registerComponentCallbacks(new MemoryChangedListener(context));
        }
    }

    /**
     * 获取 Context
     *
     * @return Context
     */
    @NonNull
    public Context getContext() {
        return context;
    }

    /**
     * 获取 UriModel 管理器
     *
     * @return UriModelRegistry
     */
    @NonNull
    public UriModelRegistry getUriModelRegistry() {
        return uriModelRegistry;
    }

    /**
     * 获取 OptionsFilter 管理器
     *
     * @return OptionsFilterRegistry
     */
    public OptionsFilterRegistry getOptionsFilterRegistry() {
        return optionsFilterRegistry;
    }

    /**
     * 获取磁盘缓存器
     *
     * @return DiskCache
     */
    @NonNull
    public DiskCache getDiskCache() {
        return diskCache;
    }

    /**
     * 设置磁盘缓存器
     *
     * @return Configuration. Convenient chain calls
     */
    @NonNull
    @SuppressWarnings("unused")
    public Configuration setDiskCache(@NonNull DiskCache newDiskCache) {
        //noinspection ConstantConditions
        if (newDiskCache != null) {
            DiskCache oldDiskCache = diskCache;
            diskCache = newDiskCache;
            if (oldDiskCache != null) {
                oldDiskCache.close();
            }
            SLog.w(NAME, "diskCache=%s", diskCache.getKey());
        }
        return this;
    }

    /**
     * 获取 Bitmap 缓存器
     *
     * @return BitmapPool
     */
    @NonNull
    @SuppressWarnings("unused")
    public BitmapPool getBitmapPool() {
        return bitmapPool;
    }

    /**
     * 设置 Bitmap 缓存器
     *
     * @return Configuration. Convenient chain calls
     */
    @NonNull
    @SuppressWarnings("unused")
    public Configuration setBitmapPool(@NonNull BitmapPool newBitmapPool) {
        //noinspection ConstantConditions
        if (newBitmapPool != null) {
            BitmapPool oldBitmapPool = this.bitmapPool;
            this.bitmapPool = newBitmapPool;
            if (oldBitmapPool != null) {
                oldBitmapPool.close();
            }
            SLog.w(NAME, "bitmapPool = %s", bitmapPool.getKey());
        }
        return this;
    }

    /**
     * 获取内存缓存器
     *
     * @return MemoryCache
     */
    @NonNull
    public MemoryCache getMemoryCache() {
        return memoryCache;
    }

    /**
     * 设置内存缓存器
     *
     * @return Configuration. Convenient chain calls
     */
    @NonNull
    @SuppressWarnings("unused")
    public Configuration setMemoryCache(@NonNull MemoryCache memoryCache) {
        //noinspection ConstantConditions
        if (memoryCache != null) {
            MemoryCache oldMemoryCache = this.memoryCache;
            this.memoryCache = memoryCache;
            if (oldMemoryCache != null) {
                oldMemoryCache.close();
            }
            SLog.w(NAME, "memoryCache=", memoryCache.getKey());
        }
        return this;
    }

    /**
     * 获取再处理图片缓存器
     *
     * @return ProcessedImageCache
     */
    @NonNull
    @SuppressWarnings("unused")
    public ProcessedImageCache getProcessedImageCache() {
        return processedImageCache;
    }

    /**
     * 设置再处理图片缓存器
     *
     * @return Configuration. Convenient chain calls
     */
    @NonNull
    @SuppressWarnings("unused")
    public Configuration setProcessedImageCache(@NonNull ProcessedImageCache processedImageCache) {
        //noinspection ConstantConditions
        if (processedImageCache != null) {
            this.processedImageCache = processedImageCache;
            SLog.w(NAME, "processedCache=", processedImageCache.getKey());
        }
        return this;
    }


    /**
     * 获取图片下载器
     *
     * @return HttpStack
     */
    @NonNull
    @SuppressWarnings("unused")
    public HttpStack getHttpStack() {
        return httpStack;
    }

    /**
     * 设置图片下载器
     *
     * @return Configuration. Convenient chain calls
     */
    @NonNull
    @SuppressWarnings("unused")
    public Configuration setHttpStack(@NonNull HttpStack httpStack) {
        //noinspection ConstantConditions
        if (httpStack != null) {
            this.httpStack = httpStack;
            SLog.w(NAME, "httpStack=", httpStack.getKey());
        }
        return this;
    }

    /**
     * 获取图片解码器
     *
     * @return ImageDecoder
     */
    @NonNull
    public ImageDecoder getDecoder() {
        return decoder;
    }

    /**
     * 设置图片解码器
     *
     * @return Configuration. Convenient chain calls
     */
    @NonNull
    @SuppressWarnings("unused")
    public Configuration setDecoder(@NonNull ImageDecoder decoder) {
        //noinspection ConstantConditions
        if (decoder != null) {
            this.decoder = decoder;
            SLog.w(NAME, "decoder=%s", decoder.getKey());
        }
        return this;
    }

    /**
     * 获取图片下载器
     *
     * @return ImageDownloader
     */
    @NonNull
    public ImageDownloader getDownloader() {
        return downloader;
    }

    /**
     * 设置图片下载器
     *
     * @param downloader 设置图片下载器
     * @return Configuration. Convenient chain calls
     */
    @NonNull
    @SuppressWarnings("unused")
    public Configuration setDownloader(@NonNull ImageDownloader downloader) {
        //noinspection ConstantConditions
        if (downloader != null) {
            this.downloader = downloader;
            SLog.w(NAME, "downloader=%s", downloader.getKey());
        }
        return this;
    }

    /**
     * 获取图片方向纠正器
     *
     * @return ImageOrientationCorrector
     */
    @NonNull
    public ImageOrientationCorrector getOrientationCorrector() {
        return orientationCorrector;
    }

    /**
     * 设置图片方向纠正器
     *
     * @return Configuration. Convenient chain calls
     */
    @NonNull
    @SuppressWarnings("unused")
    public Configuration setOrientationCorrector(@NonNull ImageOrientationCorrector orientationCorrector) {
        //noinspection ConstantConditions
        if (orientationCorrector != null) {
            this.orientationCorrector = orientationCorrector;
            SLog.w(NAME, "orientationCorrector=%s", orientationCorrector.getKey());
        }
        return this;
    }


    /**
     * 获取默认的图片显示器
     *
     * @return ImageDisplayer
     */
    @NonNull
    public ImageDisplayer getDefaultDisplayer() {
        return defaultDisplayer;
    }

    /**
     * 设置默认的图片显示器
     *
     * @return Configuration. Convenient chain calls
     */
    @NonNull
    @SuppressWarnings("unused")
    public Configuration setDefaultDisplayer(@NonNull ImageDisplayer defaultDisplayer) {
        //noinspection ConstantConditions
        if (defaultDisplayer != null) {
            this.defaultDisplayer = defaultDisplayer;
            SLog.w(NAME, "defaultDisplayer=%s", defaultDisplayer.getKey());
        }
        return this;
    }

    /**
     * 获取 Resize 图片处理器
     *
     * @return ImageProcessor
     */
    @NonNull
    public ImageProcessor getResizeProcessor() {
        return resizeProcessor;
    }

    /**
     * 设置Resize图片处理器
     *
     * @return Configuration. Convenient chain calls
     */
    @NonNull
    @SuppressWarnings("unused")
    public Configuration setResizeProcessor(@NonNull ImageProcessor resizeProcessor) {
        //noinspection ConstantConditions
        if (resizeProcessor != null) {
            this.resizeProcessor = resizeProcessor;
            SLog.w(NAME, "resizeProcessor=%s", resizeProcessor.getKey());
        }
        return this;
    }

    /**
     * 获取 Resize 计算器
     *
     * @return ResizeCalculator
     */
    @NonNull
    public ResizeCalculator getResizeCalculator() {
        return resizeCalculator;
    }

    /**
     * 设置Resize计算器
     *
     * @return Configuration. Convenient chain calls
     */
    @NonNull
    @SuppressWarnings("unused")
    public Configuration setResizeCalculator(@NonNull ResizeCalculator resizeCalculator) {
        //noinspection ConstantConditions
        if (resizeCalculator != null) {
            this.resizeCalculator = resizeCalculator;
            SLog.w(NAME, "resizeCalculator=%s", resizeCalculator.getKey());
        }
        return this;
    }

    /**
     * 获取图片尺寸计算器
     *
     * @return ImageSizeCalculator
     */
    @NonNull
    public ImageSizeCalculator getSizeCalculator() {
        return sizeCalculator;
    }

    /**
     * 获取图片尺寸计算器
     *
     * @return Configuration. Convenient chain calls
     */
    @NonNull
    @SuppressWarnings("unused")
    public Configuration setSizeCalculator(@NonNull ImageSizeCalculator sizeCalculator) {
        //noinspection ConstantConditions
        if (sizeCalculator != null) {
            this.sizeCalculator = sizeCalculator;
            SLog.w(NAME, "sizeCalculator=%s", sizeCalculator.getKey());
        }
        return this;
    }


    /**
     * 获取顺风车管理器
     *
     * @return FreeRideManager
     */
    @NonNull
    public FreeRideManager getFreeRideManager() {
        return freeRideManager;
    }

    /**
     * 设置顺风车管理器
     *
     * @param freeRideManager 顺风车管理器
     * @return Configuration. Convenient chain calls
     */
    @NonNull
    @SuppressWarnings("unused")
    public Configuration setFreeRideManager(@NonNull FreeRideManager freeRideManager) {
        //noinspection ConstantConditions
        if (freeRideManager != null) {
            this.freeRideManager = freeRideManager;
            SLog.w(NAME, "freeRideManager=%s", freeRideManager.getKey());
        }
        return this;
    }

    /**
     * 获取请求执行器
     *
     * @return RequestExecutor
     */
    @NonNull
    public RequestExecutor getExecutor() {
        return executor;
    }

    /**
     * 设置请求执行器
     *
     * @return Configuration. Convenient chain calls
     */
    @NonNull
    @SuppressWarnings("unused")
    public Configuration setExecutor(@NonNull RequestExecutor newRequestExecutor) {
        //noinspection ConstantConditions
        if (newRequestExecutor != null) {
            RequestExecutor oldRequestExecutor = executor;
            executor = newRequestExecutor;
            if (oldRequestExecutor != null) {
                oldRequestExecutor.shutdown();
            }
            SLog.w(NAME, "executor=%s", executor.getKey());
        }
        return this;
    }

    /**
     * 获取协助器工厂
     *
     * @return HelperFactory
     */
    @NonNull
    public HelperFactory getHelperFactory() {
        return helperFactory;
    }

    /**
     * 设置协助器工厂
     *
     * @return Configuration. Convenient chain calls
     */
    @NonNull
    @SuppressWarnings("unused")
    public Configuration setHelperFactory(@NonNull HelperFactory helperFactory) {
        //noinspection ConstantConditions
        if (helperFactory != null) {
            this.helperFactory = helperFactory;
            SLog.w(NAME, "helperFactory=%s", helperFactory.getKey());
        }
        return this;
    }

    /**
     * 获取请求工厂
     *
     * @return RequestFactory
     */
    @NonNull
    public RequestFactory getRequestFactory() {
        return requestFactory;
    }

    /**
     * 设置请求工厂
     *
     * @return Configuration. Convenient chain calls
     */
    @NonNull
    @SuppressWarnings("unused")
    public Configuration setRequestFactory(@NonNull RequestFactory requestFactory) {
        //noinspection ConstantConditions
        if (requestFactory != null) {
            this.requestFactory = requestFactory;
            SLog.w(NAME, "requestFactory=%s", requestFactory.getKey());
        }
        return this;
    }

    /**
     * 获取错误跟踪器
     *
     * @return ErrorTracker
     */
    @NonNull
    @SuppressWarnings("unused")
    public ErrorTracker getErrorTracker() {
        return errorTracker;
    }

    /**
     * 设置错误跟踪器
     *
     * @return Configuration. Convenient chain calls
     */
    @NonNull
    @SuppressWarnings("unused")
    public Configuration setErrorTracker(@NonNull ErrorTracker errorTracker) {
        //noinspection ConstantConditions
        if (errorTracker != null) {
            this.errorTracker = errorTracker;
            SLog.w(NAME, "errorTracker=%s", errorTracker.getKey());
        }
        return this;
    }


    /**
     * 全局暂停下载图片？
     */
    @SuppressWarnings("unused")
    public boolean isPauseDownloadEnabled() {
        return optionsFilterRegistry.isPauseDownloadEnabled();
    }

    /**
     * 设置全局暂停下载图片，开启后将不再从网络下载图片，只影响 display 请求和 load 请求
     *
     * @return Configuration. Convenient chain calls
     */
    @NonNull
    public Configuration setPauseDownloadEnabled(boolean pauseDownloadEnabled) {
        if (optionsFilterRegistry.isPauseDownloadEnabled() != pauseDownloadEnabled) {
            optionsFilterRegistry.setPauseDownloadEnabled(pauseDownloadEnabled);
            SLog.w(NAME, "pauseDownload=%s", pauseDownloadEnabled);
        }
        return this;
    }

    /**
     * 全局暂停加载新图片？
     */
    public boolean isPauseLoadEnabled() {
        return optionsFilterRegistry.isPauseLoadEnabled();
    }

    /**
     * 设置全局暂停加载新图片，开启后将只从内存缓存中找寻图片，只影响 display 请求
     *
     * @return Configuration. Convenient chain calls
     */
    @NonNull
    public Configuration setPauseLoadEnabled(boolean pauseLoadEnabled) {
        if (optionsFilterRegistry.isPauseLoadEnabled() != pauseLoadEnabled) {
            optionsFilterRegistry.setPauseLoadEnabled(pauseLoadEnabled);
            SLog.w(NAME, "pauseLoad=%s", pauseLoadEnabled);
        }
        return this;
    }

    /**
     * 全局使用低质量的图片？
     */
    public boolean isLowQualityImageEnabled() {
        return optionsFilterRegistry.isLowQualityImageEnabled();
    }

    /**
     * 设置全局使用低质量图片
     *
     * @return Configuration. Convenient chain calls
     */
    @NonNull
    public Configuration setLowQualityImageEnabled(boolean lowQualityImageEnabled) {
        if (optionsFilterRegistry.isLowQualityImageEnabled() != lowQualityImageEnabled) {
            optionsFilterRegistry.setLowQualityImageEnabled(lowQualityImageEnabled);
            SLog.w(NAME, "lowQualityImage=%s", lowQualityImageEnabled);
        }
        return this;
    }

    /**
     * 全局解码时优先质量？
     *
     * @return true：质量；false：速度
     */
    @SuppressWarnings("unused")
    public boolean isInPreferQualityOverSpeedEnabled() {
        return optionsFilterRegistry.isInPreferQualityOverSpeedEnabled();
    }

    /**
     * 设置全局解码时优先考虑质量 (默认优先考虑速度)
     *
     * @param inPreferQualityOverSpeedEnabled true：质量；false：速度
     * @return Configuration. Convenient chain calls
     */
    @NonNull
    public Configuration setInPreferQualityOverSpeedEnabled(boolean inPreferQualityOverSpeedEnabled) {
        if (optionsFilterRegistry.isInPreferQualityOverSpeedEnabled() != inPreferQualityOverSpeedEnabled) {
            optionsFilterRegistry.setInPreferQualityOverSpeedEnabled(inPreferQualityOverSpeedEnabled);
            SLog.w(NAME, "inPreferQualityOverSpeed=%s", inPreferQualityOverSpeedEnabled);
        }
        return this;
    }

    /**
     * 全局移动数据下暂停下载？
     */
    @SuppressWarnings("unused")
    public boolean isMobileDataPauseDownloadEnabled() {
        return optionsFilterRegistry.isMobileDataPauseDownloadEnabled();
    }

    /**
     * 开启全局移动数据下暂停下载功能，只影响 display 请求和 load 请求
     *
     * @return Configuration. Convenient chain calls
     */
    @NonNull
    public Configuration setMobileDataPauseDownloadEnabled(boolean mobileDataPauseDownloadEnabled) {
        if (isMobileDataPauseDownloadEnabled() != mobileDataPauseDownloadEnabled) {
            optionsFilterRegistry.setMobileDataPauseDownloadEnabled(this, mobileDataPauseDownloadEnabled);
            SLog.w(NAME, "mobileDataPauseDownload=%s", isMobileDataPauseDownloadEnabled());
        }
        return this;
    }

    @NonNull
    public String getInfo() {
        return NAME + ": " +
                "\n" + "uriModelRegistry：" + uriModelRegistry.getKey() +
                "\n" + "optionsFilterRegistry：" + optionsFilterRegistry.getKey() +

                "\n" + "diskCache：" + diskCache.getKey() +
                "\n" + "bitmapPool：" + bitmapPool.getKey() +
                "\n" + "memoryCache：" + memoryCache.getKey() +
                "\n" + "processedImageCache：" + processedImageCache.getKey() +

                "\n" + "httpStack：" + httpStack.getKey() +
                "\n" + "decoder：" + decoder.getKey() +
                "\n" + "downloader：" + downloader.getKey() +
                "\n" + "orientationCorrector：" + orientationCorrector.getKey() +

                "\n" + "defaultDisplayer：" + defaultDisplayer.getKey() +
                "\n" + "resizeProcessor：" + resizeProcessor.getKey() +
                "\n" + "resizeCalculator：" + resizeCalculator.getKey() +
                "\n" + "sizeCalculator：" + sizeCalculator.getKey() +

                "\n" + "freeRideManager：" + freeRideManager.getKey() +
                "\n" + "executor：" + executor.getKey() +
                "\n" + "helperFactory：" + helperFactory.getKey() +
                "\n" + "requestFactory：" + requestFactory.getKey() +
                "\n" + "errorTracker：" + errorTracker.getKey() +

                "\n" + "pauseDownload：" + optionsFilterRegistry.isPauseDownloadEnabled() +
                "\n" + "pauseLoad：" + optionsFilterRegistry.isPauseLoadEnabled() +
                "\n" + "lowQualityImage：" + optionsFilterRegistry.isLowQualityImageEnabled() +
                "\n" + "inPreferQualityOverSpeed：" + optionsFilterRegistry.isInPreferQualityOverSpeedEnabled() +
                "\n" + "mobileDataPauseDownload：" + isMobileDataPauseDownloadEnabled();
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private static class MemoryChangedListener implements ComponentCallbacks2 {
        private Context context;

        public MemoryChangedListener(Context context) {
            this.context = context.getApplicationContext();
        }

        @Override
        public void onTrimMemory(int level) {
            Sketch.with(context).onTrimMemory(level);
        }

        @Override
        public void onConfigurationChanged(android.content.res.Configuration newConfig) {

        }

        @Override
        public void onLowMemory() {
            Sketch.with(context).onLowMemory();
        }
    }
}
