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

import me.xiaopan.sketch.cache.BitmapPool;
import me.xiaopan.sketch.cache.DiskCache;
import me.xiaopan.sketch.cache.LruBitmapPool;
import me.xiaopan.sketch.cache.LruDiskCache;
import me.xiaopan.sketch.cache.LruMemoryCache;
import me.xiaopan.sketch.cache.MemoryCache;
import me.xiaopan.sketch.cache.MemorySizeCalculator;
import me.xiaopan.sketch.decode.DefaultImageDecoder;
import me.xiaopan.sketch.decode.ImageDecoder;
import me.xiaopan.sketch.display.DefaultImageDisplayer;
import me.xiaopan.sketch.display.ImageDisplayer;
import me.xiaopan.sketch.feature.HelperFactory;
import me.xiaopan.sketch.feature.ImagePreprocessor;
import me.xiaopan.sketch.feature.ImageSizeCalculator;
import me.xiaopan.sketch.feature.MobileNetworkGlobalPauseDownloadController;
import me.xiaopan.sketch.feature.ResizeCalculator;
import me.xiaopan.sketch.http.HttpClientStack;
import me.xiaopan.sketch.http.HttpStack;
import me.xiaopan.sketch.http.HurlStack;
import me.xiaopan.sketch.process.ImageProcessor;
import me.xiaopan.sketch.process.ResizeImageProcessor;
import me.xiaopan.sketch.request.FreeRideManager;
import me.xiaopan.sketch.request.RequestExecutor;
import me.xiaopan.sketch.request.RequestFactory;

/**
 * Sketch唯一配置类
 */
public final class Configuration {
    protected String logName = "Configuration";
    private Context context;
    private DiskCache diskCache;
    private BitmapPool bitmapPool;
    private MemoryCache memoryCache;

    private HttpStack httpStack;
    private ImageDecoder imageDecoder;
    private ImageDisplayer defaultImageDisplayer;
    private ImageProcessor resizeImageProcessor;
    private FreeRideManager freeRideManager;
    private RequestExecutor requestExecutor;
    private ResizeCalculator resizeCalculator;
    private ImagePreprocessor imagePreprocessor;
    private ImageSizeCalculator imageSizeCalculator;

    private HelperFactory helperFactory;
    private RequestFactory requestFactory;

    private SketchMonitor monitor;

    private boolean globalPauseLoad;   // 全局暂停加载新图片，开启后将只从内存缓存中找寻图片，只影响display请求
    private boolean globalPauseDownload;   // 全局暂停下载新图片，开启后将不再从网络下载新图片，只影响display请求
    private boolean globalLowQualityImage; // 全局使用低质量的图片
    private boolean globalInPreferQualityOverSpeed;   // false:全局解码时优先考虑速度；true:全局解码时优先考虑质量
    private MobileNetworkGlobalPauseDownloadController mobileNetworkGlobalPauseDownloadController;

    Configuration(Context context) {
        context = context.getApplicationContext();
        this.context = context;

        MemorySizeCalculator memorySizeCalculator = new MemorySizeCalculator(context);

        this.diskCache = new LruDiskCache(context, this, 1, DiskCache.DISK_CACHE_MAX_SIZE);
        this.bitmapPool = new LruBitmapPool(context, memorySizeCalculator.getBitmapPoolSize());
        this.memoryCache = new LruMemoryCache(context, memorySizeCalculator.getMemoryCacheSize());

        this.httpStack = Build.VERSION.SDK_INT >= 9 ? new HurlStack() : new HttpClientStack();
        this.imageDecoder = new DefaultImageDecoder();
        this.freeRideManager = new FreeRideManager();
        this.requestExecutor = new RequestExecutor();
        this.resizeCalculator = new ResizeCalculator();
        this.imagePreprocessor = new ImagePreprocessor();
        this.imageSizeCalculator = new ImageSizeCalculator();
        this.resizeImageProcessor = new ResizeImageProcessor();
        this.defaultImageDisplayer = new DefaultImageDisplayer();

        this.helperFactory = new HelperFactory();
        this.requestFactory = new RequestFactory();

        this.monitor = new SketchMonitor(context);

        if (LogType.BASE.isEnabled()) {
            SLog.d(LogType.BASE, getInfo());
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            context.getApplicationContext().registerComponentCallbacks(new MemoryChangedListener(this));
        }
    }

    /**
     * 获取Context
     *
     * @return Context
     */
    public Context getContext() {
        return context;
    }

    /**
     * 获取请求执行器
     *
     * @return RequestExecutor
     */
    public RequestExecutor getRequestExecutor() {
        return requestExecutor;
    }

    /**
     * 设置请求执行器
     *
     * @return Configuration. Convenient chain calls
     */
    @SuppressWarnings("unused")
    public Configuration setRequestExecutor(RequestExecutor newRequestExecutor) {
        if (newRequestExecutor != null) {
            RequestExecutor oldRequestExecutor = requestExecutor;
            requestExecutor = newRequestExecutor;
            if (oldRequestExecutor != null) {
                oldRequestExecutor.shutdown();
            }
            if (LogType.BASE.isEnabled()) {
                SLog.d(LogType.BASE, logName, "setRequestExecutor. %s", requestExecutor.getIdentifier());
            }
        }
        return this;
    }

    /**
     * 获取磁盘缓存器
     *
     * @return DiskCache
     */
    public DiskCache getDiskCache() {
        return diskCache;
    }

    /**
     * 设置磁盘缓存器
     *
     * @return Configuration. Convenient chain calls
     */
    @SuppressWarnings("unused")
    public Configuration setDiskCache(DiskCache newDiskCache) {
        if (newDiskCache != null) {
            DiskCache oldDiskCache = diskCache;
            diskCache = newDiskCache;
            if (oldDiskCache != null) {
                oldDiskCache.close();
            }
            if (LogType.BASE.isEnabled()) {
                SLog.d(LogType.BASE, logName, "setDiskCache. %s", diskCache.getIdentifier());
            }
        }
        return this;
    }

    /**
     * 获取Bitmap缓存器
     *
     * @return BitmapPool
     */
    @SuppressWarnings("unused")
    public BitmapPool getBitmapPool() {
        return bitmapPool;
    }

    /**
     * 设置Bitmap缓存器
     *
     * @return Configuration. Convenient chain calls
     */
    @SuppressWarnings("unused")
    public Configuration setBitmapPool(BitmapPool newBitmapPool) {
        if (newBitmapPool != null) {
            BitmapPool oldBitmapPool = this.bitmapPool;
            this.bitmapPool = newBitmapPool;
            if (oldBitmapPool != null) {
                oldBitmapPool.close();
            }
            if (LogType.BASE.isEnabled()) {
                SLog.d(LogType.BASE, logName, "setBitmapPool. %s", bitmapPool.getIdentifier());
            }
        }
        return this;
    }

    /**
     * 获取内存缓存器
     *
     * @return MemoryCache
     */
    public MemoryCache getMemoryCache() {
        return memoryCache;
    }

    /**
     * 设置内存缓存器
     *
     * @return Configuration. Convenient chain calls
     */
    @SuppressWarnings("unused")
    public Configuration setMemoryCache(MemoryCache memoryCache) {
        if (memoryCache != null) {
            MemoryCache oldMemoryCache = this.memoryCache;
            this.memoryCache = memoryCache;
            if (oldMemoryCache != null) {
                oldMemoryCache.close();
            }
            if (LogType.BASE.isEnabled()) {
                SLog.d(LogType.BASE, logName, "setMemoryCache. %s", memoryCache.getIdentifier());
            }
        }
        return this;
    }

    /**
     * 获取图片解码器
     *
     * @return ImageDecoder
     */
    public ImageDecoder getImageDecoder() {
        return imageDecoder;
    }

    /**
     * 设置图片解码器
     *
     * @return Configuration. Convenient chain calls
     */
    @SuppressWarnings("unused")
    public Configuration setImageDecoder(ImageDecoder imageDecoder) {
        if (imageDecoder != null) {
            this.imageDecoder = imageDecoder;
            if (LogType.BASE.isEnabled()) {
                SLog.d(LogType.BASE, logName, "setImageDecoder. %s", imageDecoder.getIdentifier());
            }
        }
        return this;
    }

    /**
     * 获取图片下载器
     *
     * @return HttpStack
     */
    public HttpStack getHttpStack() {
        return httpStack;
    }

    /**
     * 设置图片下载器
     *
     * @return Configuration. Convenient chain calls
     */
    @SuppressWarnings("unused")
    public Configuration setHttpStack(HttpStack httpStack) {
        if (httpStack != null) {
            this.httpStack = httpStack;
            if (LogType.BASE.isEnabled()) {
                SLog.d(LogType.BASE, logName, "setHttpStack. %s", httpStack.getIdentifier());
            }
        }
        return this;
    }

    /**
     * 获取图片尺寸计算器
     *
     * @return ImageSizeCalculator
     */
    public ImageSizeCalculator getImageSizeCalculator() {
        return imageSizeCalculator;
    }

    /**
     * 获取图片尺寸计算器
     *
     * @return Configuration. Convenient chain calls
     */
    @SuppressWarnings("unused")
    public Configuration setImageSizeCalculator(ImageSizeCalculator imageSizeCalculator) {
        if (imageSizeCalculator != null) {
            this.imageSizeCalculator = imageSizeCalculator;
            if (LogType.BASE.isEnabled()) {
                SLog.d(LogType.BASE, logName, "setImageSizeCalculator. %s", imageSizeCalculator.getIdentifier());
            }
        }
        return this;
    }

    /**
     * 获取默认的图片显示器
     *
     * @return ImageDisplayer
     */
    public ImageDisplayer getDefaultImageDisplayer() {
        return defaultImageDisplayer;
    }

    /**
     * 设置默认的图片处理器
     *
     * @return Configuration. Convenient chain calls
     */
    @SuppressWarnings("unused")
    public Configuration setDefaultImageDisplayer(ImageDisplayer defaultImageDisplayer) {
        if (defaultImageDisplayer != null) {
            this.defaultImageDisplayer = defaultImageDisplayer;
            if (LogType.BASE.isEnabled()) {
                SLog.d(LogType.BASE, logName, "setDefaultImageDisplayer. %s", defaultImageDisplayer.getIdentifier());
            }
        }
        return this;
    }

    /**
     * 获取Resize图片处理器
     *
     * @return ImageProcessor
     */
    public ImageProcessor getResizeImageProcessor() {
        return resizeImageProcessor;
    }

    /**
     * 设置Resize图片处理器
     *
     * @return Configuration. Convenient chain calls
     */
    @SuppressWarnings("unused")
    public Configuration setResizeImageProcessor(ImageProcessor resizeImageProcessor) {
        if (resizeImageProcessor != null) {
            this.resizeImageProcessor = resizeImageProcessor;
            if (LogType.BASE.isEnabled()) {
                SLog.d(LogType.BASE, logName, "setResizeImageProcessor. %s", resizeImageProcessor.getIdentifier());
            }
        }
        return this;
    }

    /**
     * 获取协助器工厂
     *
     * @return HelperFactory
     */
    public HelperFactory getHelperFactory() {
        return helperFactory;
    }

    /**
     * 设置协助器工厂
     *
     * @return Configuration. Convenient chain calls
     */
    @SuppressWarnings("unused")
    public Configuration setHelperFactory(HelperFactory helperFactory) {
        if (helperFactory != null) {
            this.helperFactory = helperFactory;
            if (LogType.BASE.isEnabled()) {
                SLog.d(LogType.BASE, logName, "setHelperFactory. %s", helperFactory.getIdentifier());
            }
        }
        return this;
    }

    /**
     * 获取请求工厂
     *
     * @return RequestFactory
     */
    public RequestFactory getRequestFactory() {
        return requestFactory;
    }

    /**
     * 设置请求工厂
     *
     * @return Configuration. Convenient chain calls
     */
    @SuppressWarnings("unused")
    public Configuration setRequestFactory(RequestFactory requestFactory) {
        if (requestFactory != null) {
            this.requestFactory = requestFactory;
            if (LogType.BASE.isEnabled()) {
                SLog.d(LogType.BASE, logName, "setRequestFactory. %s", requestFactory.getIdentifier());
            }
        }
        return this;
    }

    /**
     * 获取Resize计算器
     *
     * @return ResizeCalculator
     */
    public ResizeCalculator getResizeCalculator() {
        return resizeCalculator;
    }

    /**
     * 设置Resize计算器
     *
     * @return Configuration. Convenient chain calls
     */
    @SuppressWarnings("unused")
    public Configuration setResizeCalculator(ResizeCalculator resizeCalculator) {
        if (resizeCalculator != null) {
            this.resizeCalculator = resizeCalculator;
            if (LogType.BASE.isEnabled()) {
                SLog.d(LogType.BASE, logName, "setResizeCalculator. %s", resizeCalculator.getIdentifier());
            }
        }
        return this;
    }

    /**
     * 全局暂停加载新图片？开启后将只从内存缓存中找寻图片，只影响display请求
     */
    public boolean isGlobalPauseLoad() {
        return globalPauseLoad;
    }

    /**
     * 设置全局暂停加载新图片，开启后将只从内存缓存中找寻图片，只影响display请求
     *
     * @return Configuration. Convenient chain calls
     */
    public Configuration setGlobalPauseLoad(boolean globalPauseLoad) {
        if (this.globalPauseLoad != globalPauseLoad) {
            this.globalPauseLoad = globalPauseLoad;
            if (LogType.BASE.isEnabled()) {
                SLog.d(LogType.BASE, logName, "setGlobalPauseLoad. %s", globalPauseLoad);
            }
        }
        return this;
    }

    /**
     * 全局暂停下载图片？开启后将不再从网络下载图片，只影响display请求和load请求
     */
    public boolean isGlobalPauseDownload() {
        return globalPauseDownload;
    }

    /**
     * 设置全局暂停下载图片，开启后将不再从网络下载图片，只影响display请求和load请求
     *
     * @return Configuration. Convenient chain calls
     */
    public Configuration setGlobalPauseDownload(boolean globalPauseDownload) {
        if (this.globalPauseDownload != globalPauseDownload) {
            this.globalPauseDownload = globalPauseDownload;
            if (LogType.BASE.isEnabled()) {
                SLog.d(LogType.BASE, logName, "setGlobalPauseDownload. %s", globalPauseDownload);
            }
        }
        return this;
    }

    /**
     * 移动网络下全局暂停下载？只影响display请求和load请求
     */
    @SuppressWarnings("unused")
    public boolean isMobileNetworkGlobalPauseDownload() {
        return mobileNetworkGlobalPauseDownloadController != null && mobileNetworkGlobalPauseDownloadController.isOpened();
    }

    /**
     * 设置开启移动网络下暂停下载的功能，只影响display请求和load请求
     *
     * @return Configuration. Convenient chain calls
     */
    public Configuration setMobileNetworkGlobalPauseDownload(boolean mobileNetworkGlobalPauseDownload) {
        if (isMobileNetworkGlobalPauseDownload() != mobileNetworkGlobalPauseDownload) {
            if (mobileNetworkGlobalPauseDownload) {
                if (this.mobileNetworkGlobalPauseDownloadController == null) {
                    this.mobileNetworkGlobalPauseDownloadController = new MobileNetworkGlobalPauseDownloadController(context);
                }
                this.mobileNetworkGlobalPauseDownloadController.setOpened(true);
            } else {
                if (this.mobileNetworkGlobalPauseDownloadController != null) {
                    this.mobileNetworkGlobalPauseDownloadController.setOpened(false);
                }
            }

            if (LogType.BASE.isEnabled()) {
                SLog.d(LogType.BASE, logName, "setMobileNetworkGlobalPauseDownload. %s", isMobileNetworkGlobalPauseDownload());
            }
        }
        return this;
    }

    /**
     * 全局使用低质量的图片？
     */
    public boolean isGlobalLowQualityImage() {
        return globalLowQualityImage;
    }

    /**
     * 设置全局使用低质量的图片
     *
     * @return Configuration. Convenient chain calls
     */
    public Configuration setGlobalLowQualityImage(boolean globalLowQualityImage) {
        if (this.globalLowQualityImage != globalLowQualityImage) {
            this.globalLowQualityImage = globalLowQualityImage;
            if (LogType.BASE.isEnabled()) {
                SLog.d(LogType.BASE, logName, "setGlobalLowQualityImage. %s", globalLowQualityImage);
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
     * @return Configuration. Convenient chain calls
     */
    public Configuration setGlobalInPreferQualityOverSpeed(boolean globalInPreferQualityOverSpeed) {
        if (this.globalInPreferQualityOverSpeed != globalInPreferQualityOverSpeed) {
            this.globalInPreferQualityOverSpeed = globalInPreferQualityOverSpeed;
            if (LogType.BASE.isEnabled()) {
                SLog.d(LogType.BASE, logName, "setGlobalInPreferQualityOverSpeed. %s", globalInPreferQualityOverSpeed);
            }
        }
        return this;
    }

    /**
     * 获取图片预处理器
     *
     * @return ImagePreprocessor
     */
    public ImagePreprocessor getImagePreprocessor() {
        return imagePreprocessor;
    }

    /**
     * 设置图片预处理器
     *
     * @return Configuration. Convenient chain calls
     */
    public Configuration setImagePreprocessor(ImagePreprocessor imagePreprocessor) {
        if (imagePreprocessor != null) {
            this.imagePreprocessor = imagePreprocessor;
            if (LogType.BASE.isEnabled()) {
                SLog.d(LogType.BASE, logName, "setImagePreprocessor. %s", imagePreprocessor.getIdentifier());
            }
        }
        return this;
    }

    /**
     * 获取监控器
     *
     * @return SketchMonitor
     */
    @SuppressWarnings("unused")
    public SketchMonitor getMonitor() {
        return monitor;
    }

    /**
     * 设置监控器
     *
     * @return Configuration. Convenient chain calls
     */
    @SuppressWarnings("unused")
    public Configuration setMonitor(SketchMonitor monitor) {
        if (monitor != null) {
            this.monitor = monitor;
            if (LogType.BASE.isEnabled()) {
                SLog.d(LogType.BASE, logName, "setMonitor. %s", monitor.getIdentifier());
            }
        }
        return this;
    }

    /**
     * 获取顺风车管理器
     *
     * @return FreeRideManager
     */
    public FreeRideManager getFreeRideManager() {
        return freeRideManager;
    }

    public String getInfo() {
        return logName + ": " +
                "\n" + "diskCache：" + diskCache.getIdentifier() +
                "\n" + "bitmapPool：" + bitmapPool.getIdentifier() +
                "\n" + "memoryCache：" + memoryCache.getIdentifier() +
                "\n" + "imageDecoder：" + imageDecoder.getIdentifier() +
                "\n" + "helperFactory：" + helperFactory.getIdentifier() +
                "\n" + "defaultImageDisplayer：" + defaultImageDisplayer.getIdentifier() +
                "\n" + "resizeImageProcessor：" + resizeImageProcessor.getIdentifier() +
                "\n" + "requestFactory：" + requestFactory.getIdentifier() +
                "\n" + "httpStack：" + httpStack.getIdentifier() +
                "\n" + "requestExecutor：" + requestExecutor.getIdentifier() +
                "\n" + "imageSizeCalculator：" + imageSizeCalculator.getIdentifier() +
                "\n" + "resizeCalculator：" + resizeCalculator.getIdentifier() +
                "\n" + "imagePreprocessor：" + imagePreprocessor.getIdentifier() +
                "\n" + "errorCallback：" + monitor.getIdentifier() +
                "\n" + "globalPauseLoad：" + globalPauseLoad +
                "\n" + "globalPauseDownload：" + globalPauseDownload +
                "\n" + "globalLowQualityImage：" + globalLowQualityImage +
                "\n" + "globalInPreferQualityOverSpeed：" + globalInPreferQualityOverSpeed +
                "\n" + "mobileNetworkGlobalPauseDownload：" + isMobileNetworkGlobalPauseDownload();
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private static class MemoryChangedListener implements ComponentCallbacks2 {
        private Configuration configuration;

        public MemoryChangedListener(Configuration configuration) {
            this.configuration = configuration;
        }

        @Override
        public void onTrimMemory(int level) {
            Sketch.with(configuration.getContext()).onTrimMemory(level);
        }

        @Override
        public void onConfigurationChanged(android.content.res.Configuration newConfig) {

        }

        @Override
        public void onLowMemory() {
            Sketch.with(configuration.getContext()).onLowMemory();
        }
    }
}
