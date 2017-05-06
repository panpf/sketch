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
import me.xiaopan.sketch.feature.ImageOrientationCorrector;
import me.xiaopan.sketch.feature.ImagePreprocessor;
import me.xiaopan.sketch.feature.ImageSizeCalculator;
import me.xiaopan.sketch.feature.MobileNetworkGlobalPauseDownloadController;
import me.xiaopan.sketch.feature.ProcessedImageCache;
import me.xiaopan.sketch.feature.ResizeCalculator;
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
    private ProcessedImageCache processedImageCache;

    private HttpStack httpStack;
    private ImageDecoder imageDecoder;
    private ImageDisplayer defaultImageDisplayer;
    private ImageProcessor resizeImageProcessor;
    private FreeRideManager freeRideManager;
    private RequestExecutor requestExecutor;
    private ResizeCalculator resizeCalculator;
    private ImagePreprocessor imagePreprocessor;
    private ImageSizeCalculator imageSizeCalculator;
    private ImageOrientationCorrector imageOrientationCorrector;

    private HelperFactory helperFactory;
    private RequestFactory requestFactory;

    private SketchMonitor monitor;

    // TODO: 2017/4/15 搞一个通用的属性拦截器，把这些放到属性拦截器里
    private boolean globalPauseLoad;   // 全局暂停加载新图片，开启后将只从内存缓存中找寻图片，只影响display请求
    private boolean globalPauseDownload;   // 全局暂停下载新图片，开启后将不再从网络下载新图片，只影响display请求
    private boolean globalLowQualityImage; // 全局使用低质量的图片
    private boolean globalInPreferQualityOverSpeed;   // false:全局解码时优先考虑速度；true:全局解码时优先考虑质量
    private MobileNetworkGlobalPauseDownloadController mobileNetworkGlobalPauseDownloadController;

    Configuration(Context context) {
        context = context.getApplicationContext();
        this.context = context;

        MemorySizeCalculator memorySizeCalculator = new MemorySizeCalculator(context);

        // 由于默认的缓存文件名称从URLEncoder加密变成了MD5所以这里要升级一下版本号，好清除旧的缓存
        this.diskCache = new LruDiskCache(context, this, 2, DiskCache.DISK_CACHE_MAX_SIZE);
        this.bitmapPool = new LruBitmapPool(context, memorySizeCalculator.getBitmapPoolSize());
        this.memoryCache = new LruMemoryCache(context, memorySizeCalculator.getMemoryCacheSize());

        this.httpStack = new HurlStack();
        this.imageDecoder = new DefaultImageDecoder();
        this.freeRideManager = new FreeRideManager();
        this.requestExecutor = new RequestExecutor();
        this.resizeCalculator = new ResizeCalculator();
        this.imagePreprocessor = new ImagePreprocessor();
        this.imageSizeCalculator = new ImageSizeCalculator();
        this.processedImageCache = new ProcessedImageCache();
        this.resizeImageProcessor = new ResizeImageProcessor();
        this.defaultImageDisplayer = new DefaultImageDisplayer();
        this.imageOrientationCorrector = new ImageOrientationCorrector();

        this.helperFactory = new HelperFactory();
        this.requestFactory = new RequestFactory();

        this.monitor = new SketchMonitor(context);

        if (SLogType.BASE.isEnabled()) {
            SLog.d(SLogType.BASE, getInfo());
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            context.getApplicationContext().registerComponentCallbacks(new MemoryChangedListener(context));
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
            if (SLogType.BASE.isEnabled()) {
                SLog.d(SLogType.BASE, logName, "setRequestExecutor. %s", requestExecutor.getKey());
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
            if (SLogType.BASE.isEnabled()) {
                SLog.d(SLogType.BASE, logName, "setDiskCache. %s", diskCache.getKey());
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
            if (SLogType.BASE.isEnabled()) {
                SLog.d(SLogType.BASE, logName, "setBitmapPool. %s", bitmapPool.getKey());
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
            if (SLogType.BASE.isEnabled()) {
                SLog.d(SLogType.BASE, logName, "setMemoryCache. %s", memoryCache.getKey());
            }
        }
        return this;
    }

    /**
     * 获取再处理图片缓存器
     *
     * @return ProcessedImageCache
     */
    @SuppressWarnings("unused")
    public ProcessedImageCache getProcessedImageCache() {
        return processedImageCache;
    }

    /**
     * 设置再处理图片缓存器
     *
     * @return Configuration. Convenient chain calls
     */
    @SuppressWarnings("unused")
    public Configuration setProcessedImageCache(ProcessedImageCache processedImageCache) {
        this.processedImageCache = processedImageCache;
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
            if (SLogType.BASE.isEnabled()) {
                SLog.d(SLogType.BASE, logName, "setImageDecoder. %s", imageDecoder.getKey());
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
            if (SLogType.BASE.isEnabled()) {
                SLog.d(SLogType.BASE, logName, "setHttpStack. %s", httpStack.getKey());
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
            if (SLogType.BASE.isEnabled()) {
                SLog.d(SLogType.BASE, logName, "setImageSizeCalculator. %s", imageSizeCalculator.getKey());
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
            if (SLogType.BASE.isEnabled()) {
                SLog.d(SLogType.BASE, logName, "setDefaultImageDisplayer. %s", defaultImageDisplayer.getKey());
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
            if (SLogType.BASE.isEnabled()) {
                SLog.d(SLogType.BASE, logName, "setResizeImageProcessor. %s", resizeImageProcessor.getKey());
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
            if (SLogType.BASE.isEnabled()) {
                SLog.d(SLogType.BASE, logName, "setHelperFactory. %s", helperFactory.getKey());
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
            if (SLogType.BASE.isEnabled()) {
                SLog.d(SLogType.BASE, logName, "setRequestFactory. %s", requestFactory.getKey());
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
            if (SLogType.BASE.isEnabled()) {
                SLog.d(SLogType.BASE, logName, "setResizeCalculator. %s", resizeCalculator.getKey());
            }
        }
        return this;
    }

    /**
     * 获取图片方向纠正器
     *
     * @return ImageOrientationCorrector
     */
    public ImageOrientationCorrector getImageOrientationCorrector() {
        return imageOrientationCorrector;
    }

    /**
     * 设置图片方向纠正器
     *
     * @return Configuration. Convenient chain calls
     */
    @SuppressWarnings("unused")
    public Configuration setImageOrientationCorrector(ImageOrientationCorrector imageOrientationCorrector) {
        if (imageOrientationCorrector != null) {
            this.imageOrientationCorrector = imageOrientationCorrector;
            if (SLogType.BASE.isEnabled()) {
                SLog.d(SLogType.BASE, logName, "setImageOrientationCorrector. %s", imageOrientationCorrector.getKey());
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
            if (SLogType.BASE.isEnabled()) {
                SLog.d(SLogType.BASE, logName, "setGlobalPauseLoad. %s", globalPauseLoad);
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
            if (SLogType.BASE.isEnabled()) {
                SLog.d(SLogType.BASE, logName, "setGlobalPauseDownload. %s", globalPauseDownload);
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

            if (SLogType.BASE.isEnabled()) {
                SLog.d(SLogType.BASE, logName, "setMobileNetworkGlobalPauseDownload. %s", isMobileNetworkGlobalPauseDownload());
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
            if (SLogType.BASE.isEnabled()) {
                SLog.d(SLogType.BASE, logName, "setGlobalLowQualityImage. %s", globalLowQualityImage);
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
            if (SLogType.BASE.isEnabled()) {
                SLog.d(SLogType.BASE, logName, "setGlobalInPreferQualityOverSpeed. %s", globalInPreferQualityOverSpeed);
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
            if (SLogType.BASE.isEnabled()) {
                SLog.d(SLogType.BASE, logName, "setImagePreprocessor. %s", imagePreprocessor.getKey());
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
            if (SLogType.BASE.isEnabled()) {
                SLog.d(SLogType.BASE, logName, "setMonitor. %s", monitor.getKey());
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
                "\n" + "diskCache：" + diskCache.getKey() +
                "\n" + "bitmapPool：" + bitmapPool.getKey() +
                "\n" + "memoryCache：" + memoryCache.getKey() +
                "\n" + "processedImageCache：" + processedImageCache.getKey() +
                "\n" + "imageDecoder：" + imageDecoder.getKey() +
                "\n" + "helperFactory：" + helperFactory.getKey() +
                "\n" + "defaultImageDisplayer：" + defaultImageDisplayer.getKey() +
                "\n" + "resizeImageProcessor：" + resizeImageProcessor.getKey() +
                "\n" + "requestFactory：" + requestFactory.getKey() +
                "\n" + "httpStack：" + httpStack.getKey() +
                "\n" + "requestExecutor：" + requestExecutor.getKey() +
                "\n" + "imageSizeCalculator：" + imageSizeCalculator.getKey() +
                "\n" + "resizeCalculator：" + resizeCalculator.getKey() +
                "\n" + "imagePreprocessor：" + imagePreprocessor.getKey() +
                "\n" + "errorCallback：" + monitor.getKey() +
                "\n" + "globalPauseLoad：" + globalPauseLoad +
                "\n" + "globalPauseDownload：" + globalPauseDownload +
                "\n" + "globalLowQualityImage：" + globalLowQualityImage +
                "\n" + "globalInPreferQualityOverSpeed：" + globalInPreferQualityOverSpeed +
                "\n" + "mobileNetworkGlobalPauseDownload：" + isMobileNetworkGlobalPauseDownload();
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
