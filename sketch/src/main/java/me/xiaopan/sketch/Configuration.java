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
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import me.xiaopan.sketch.cache.DiskCache;
import me.xiaopan.sketch.cache.LruDiskCache;
import me.xiaopan.sketch.cache.LruMemoryCache;
import me.xiaopan.sketch.cache.MemoryCache;
import me.xiaopan.sketch.decode.DefaultImageDecoder;
import me.xiaopan.sketch.decode.ImageDecoder;
import me.xiaopan.sketch.display.DefaultImageDisplayer;
import me.xiaopan.sketch.display.ImageDisplayer;
import me.xiaopan.sketch.download.HttpClientImageDownloader;
import me.xiaopan.sketch.download.HttpUrlConnectionImageDownloader;
import me.xiaopan.sketch.download.ImageDownloader;
import me.xiaopan.sketch.execute.DefaultRequestExecutor;
import me.xiaopan.sketch.execute.RequestExecutor;
import me.xiaopan.sketch.process.DefaultImageProcessor;
import me.xiaopan.sketch.process.ImageProcessor;
import me.xiaopan.sketch.util.MobileNetworkPauseDownloadManager;

public class Configuration {
    private static final String NAME = "Configuration";

    private Context context;	//上下文
    private Handler handler;    // 异步线程回调用
    private DiskCache diskCache;    // 磁盘缓存器
    private MemoryCache memoryCache;	//图片缓存器
    private MemoryCache placeholderImageMemoryCache;    // 占位图内存缓存器
    private ImageDecoder imageDecoder;	//图片解码器
    private HelperFactory helperFactory;    // 协助器工厂
    private ImageDisplayer defaultImageDisplayer;   // 默认的图片显示器，当DisplayRequest中没有指定显示器的时候就会用到
    private ImageProcessor defaultCutImageProcessor;    // 默认的图片裁剪处理器
    private RequestFactory requestFactory;  // 请求工厂
    private ImageDownloader imageDownloader;	//图片下载器
    private RequestExecutor requestExecutor;	//请求执行器
    private ResizeCalculator resizeCalculator;  // resize计算器
    private ImageSizeCalculator imageSizeCalculator; // 图片尺寸计算器

    private boolean pauseLoad;   // 暂停加载新图片，开启后将只从内存缓存中找寻图片，只影响display请求
    private boolean cacheInDisk = true;
    private boolean cacheInMemory = true;
    private boolean pauseDownload;   // 暂停下载新图片，开启后将不再从网络下载新图片，只影响display请求
    private boolean decodeGifImage = true; // 是否解码GIF图
    private boolean lowQualityImage; // 是否返回低质量的图片
    private MobileNetworkPauseDownloadManager mobileNetworkPauseDownloadManager;

    public Configuration(Context tempContext){
        this.context = tempContext.getApplicationContext();
        this.diskCache = new LruDiskCache(context);
        this.memoryCache = new LruMemoryCache(context, (int) (Runtime.getRuntime().maxMemory()/8));
        this.imageDecoder = new DefaultImageDecoder();
        this.helperFactory = new DefaultHelperFactory();
        this.requestFactory = new DefaultRequestFactory();
        if (Build.VERSION.SDK_INT >= 9) {
            this.imageDownloader = new HttpUrlConnectionImageDownloader();
        }else{
            this.imageDownloader = new HttpClientImageDownloader();
        }
        this.requestExecutor = new DefaultRequestExecutor.Builder().build();
        this.resizeCalculator = new DefaultResizeCalculator();
        this.imageSizeCalculator = new DefaultImageSizeCalculator();
        this.defaultImageDisplayer = new DefaultImageDisplayer();
        this.defaultCutImageProcessor = new DefaultImageProcessor();
        this.placeholderImageMemoryCache = new LruMemoryCache(context, (int) (Runtime.getRuntime().maxMemory()/16));
        this.handler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if(msg.obj instanceof DownloadRequest){
                    ((DownloadRequest) msg.obj).invokeInMainThread(msg);
                    return true;
                }else{
                    return false;
                }
            }
        });
        if(Sketch.isDebugMode()){
            Log.i(Sketch.TAG, getInfo());
        }
    }

    /**
     * 获取上下文
     * @return 上下文
     */
    public Context getContext() {
        return context;
    }

    /**
     * 获取Handler
     * @return Handler
     */
    public Handler getHandler() {
        return handler;
    }

    /**
     * 获取请求执行器
     * @return 请求执行器
     */
    public RequestExecutor getRequestExecutor() {
        return requestExecutor;
    }

    /**
     * 设置请求执行器
     * @param requestExecutor 请求执行器
     */
    public Configuration setRequestExecutor(RequestExecutor requestExecutor) {
        if(requestExecutor != null){
            this.requestExecutor = requestExecutor;
            if(Sketch.isDebugMode()){
                StringBuilder builder = new StringBuilder();
                builder.append(NAME).append(": ").append("set").append(" - ");
                builder.append("requestExecutor").append(" (");
                requestExecutor.appendIdentifier(builder);
                builder.append(")");
                Log.i(Sketch.TAG, builder.toString());
            }
        }
        return this;
    }

    /**
     * 获取磁盘缓存器
     * @return 磁盘缓存器
     */
    public DiskCache getDiskCache() {
        return diskCache;
    }

    /**
     * 设置磁盘缓存器
     * @param diskCache 磁盘缓存器
     */
    public Configuration setDiskCache(DiskCache diskCache) {
        if(diskCache != null){
            this.diskCache = diskCache;
            if(Sketch.isDebugMode()){
                StringBuilder builder = new StringBuilder();
                builder.append(NAME).append(": ").append("set").append(" - ");
                builder.append("diskCache").append(" (");
                diskCache.appendIdentifier(builder);
                builder.append(")");
                Log.i(Sketch.TAG, builder.toString());
            }
        }
        return this;
    }

    /**
     * 获取内存缓存器
     * @return 内存缓存器
     */
    public MemoryCache getMemoryCache() {
        return memoryCache;
    }

    /**
     * 设置内存缓存器
     * @param memoryCache 内存缓存器
     */
    public Configuration setMemoryCache(MemoryCache memoryCache) {
        if(memoryCache != null){
            MemoryCache oldMemoryCache = this.memoryCache;
            this.memoryCache = memoryCache;
            if(oldMemoryCache != null){
                oldMemoryCache.clear();
            }
            if(Sketch.isDebugMode()){
                StringBuilder builder = new StringBuilder();
                builder.append(NAME).append(": ").append("set").append(" - ");
                builder.append("memoryCache").append(" (");
                memoryCache.appendIdentifier(builder);
                builder.append(")");
                Log.i(Sketch.TAG, builder.toString());
            }
        }
        return this;
    }

    /**
     * 获取占位图内存缓存器
     * @return 占位图内存缓存器
     */
    public MemoryCache getPlaceholderImageMemoryCache() {
        return placeholderImageMemoryCache;
    }

    /**
     * 设置占位图内存缓存器
     * @param placeholderImageMemoryCache 占位图内存缓存器
     */
    public Configuration setPlaceholderImageMemoryCache(MemoryCache placeholderImageMemoryCache) {
        if(placeholderImageMemoryCache != null){
            MemoryCache oldMemoryCache = this.placeholderImageMemoryCache;
            this.placeholderImageMemoryCache = placeholderImageMemoryCache;
            if(oldMemoryCache != null){
                oldMemoryCache.clear();
            }
            if(Sketch.isDebugMode()){
                StringBuilder builder = new StringBuilder();
                builder.append(NAME).append(": ").append("set").append(" - ");
                builder.append("placeholderImageMemoryCache").append(" (");
                placeholderImageMemoryCache.appendIdentifier(builder);
                builder.append(")");
                Log.i(Sketch.TAG, builder.toString());
            }
        }
        return this;
    }

    /**
     * 获取图片解码器
     * @return 图片解码器
     */
    public ImageDecoder getImageDecoder() {
        return imageDecoder;
    }

    /**
     * 设置图片解码器
     * @param imageDecoder 图片解码器
     */
    public Configuration setImageDecoder(ImageDecoder imageDecoder) {
        if(imageDecoder != null){
            this.imageDecoder = imageDecoder;
            if(Sketch.isDebugMode()){
                StringBuilder builder = new StringBuilder();
                builder.append(NAME).append(": ").append("set").append(" - ");
                builder.append("imageDecoder").append(" (");
                imageDecoder.appendIdentifier(builder);
                builder.append(")");
                Log.i(Sketch.TAG, builder.toString());
            }
        }
        return this;
    }

    /**
     * 获取图片下载器
     */
    public ImageDownloader getImageDownloader() {
        return imageDownloader;
    }

    /**
     * 设置图片下载器
     * @param imageDownloader 图片下载器
     */
    public Configuration setImageDownloader(ImageDownloader imageDownloader) {
        if(imageDownloader != null){
            this.imageDownloader = imageDownloader;
            if(Sketch.isDebugMode()){
                StringBuilder builder = new StringBuilder();
                builder.append(NAME).append(": ").append("set").append(" - ");
                builder.append("imageDownloader").append(" (");
                imageDownloader.appendIdentifier(builder);
                builder.append(")");
                Log.i(Sketch.TAG, builder.toString());
            }
        }
        return this;
    }

    /**
     * 获取图片尺寸计算器
     * @return 图片尺寸计算器
     */
    public ImageSizeCalculator getImageSizeCalculator() {
        return imageSizeCalculator;
    }

    /**
     * 获取图片尺寸计算器
     * @param imageSizeCalculator 图片尺寸计算器
     */
    public Configuration setImageSizeCalculator(ImageSizeCalculator imageSizeCalculator) {
        if(imageSizeCalculator != null){
            this.imageSizeCalculator = imageSizeCalculator;
            if(Sketch.isDebugMode()){
                StringBuilder builder = new StringBuilder();
                builder.append(NAME).append(": ").append("set").append(" - ");
                builder.append("imageSizeCalculator").append(" (");
                imageSizeCalculator.appendIdentifier(builder);
                builder.append(")");
                Log.i(Sketch.TAG, builder.toString());
            }
        }
        return this;
    }

    /**
     * 获取默认的图片显示器
     * @return 默认的图片显示器
     */
    public ImageDisplayer getDefaultImageDisplayer() {
        return defaultImageDisplayer;
    }

    /**
     * 设置默认的图片处理器
     * @param defaultImageDisplayer 默认的图片处理器
     */
    public Configuration setDefaultImageDisplayer(ImageDisplayer defaultImageDisplayer) {
        if(defaultImageDisplayer != null){
            this.defaultImageDisplayer = defaultImageDisplayer;
            if(Sketch.isDebugMode()){
                StringBuilder builder = new StringBuilder();
                builder.append(NAME).append(": ").append("set").append(" - ");
                builder.append("defaultImageDisplayer").append(" (");
                defaultImageDisplayer.appendIdentifier(builder);
                builder.append(")");
                Log.i(Sketch.TAG, builder.toString());
            }
        }
        return this;
    }

    /**
     * 获取默认的图片裁剪处理器
     * @return 默认的图片裁剪处理器
     */
    public ImageProcessor getDefaultCutImageProcessor() {
        return defaultCutImageProcessor;
    }

    /**
     * 默认的默认的图片裁剪处理器
     * @param defaultCutImageProcessor 默认的图片裁剪处理器
     */
    public Configuration setDefaultCutImageProcessor(ImageProcessor defaultCutImageProcessor) {
        if(defaultCutImageProcessor != null){
            this.defaultCutImageProcessor = defaultCutImageProcessor;
            if(Sketch.isDebugMode()){
                StringBuilder builder = new StringBuilder();
                builder.append(NAME).append(": ").append("set").append(" - ");
                builder.append("defaultCutImageProcessor").append(" (");
                defaultCutImageProcessor.appendIdentifier(builder);
                builder.append(")");
                Log.i(Sketch.TAG, builder.toString());
            }
        }
        return this;
    }

    /**
     * 获取协助器工厂
     * @return 协助器工厂
     */
    public HelperFactory getHelperFactory() {
        return helperFactory;
    }

    /**
     * 设置协助器工厂
     * @param helperFactory 协助器工厂
     */
    public Configuration setHelperFactory(HelperFactory helperFactory) {
        if(helperFactory != null){
            this.helperFactory = helperFactory;
            if(Sketch.isDebugMode()){
                StringBuilder builder = new StringBuilder();
                builder.append(NAME).append(": ").append("set").append(" - ");
                builder.append("helperFactory").append(" (");
                helperFactory.appendIdentifier(builder);
                builder.append(")");
                Log.i(Sketch.TAG, builder.toString());
            }
        }
        return this;
    }

    /**
     * 获取请求工厂
     * @return 请求工厂
     */
    public RequestFactory getRequestFactory() {
        return requestFactory;
    }

    /**
     * 设置请求工厂
     * @param requestFactory 请求工厂
     */
    public Configuration setRequestFactory(RequestFactory requestFactory) {
        if(requestFactory != null){
            this.requestFactory = requestFactory;
            if(Sketch.isDebugMode()){
                StringBuilder builder = new StringBuilder();
                builder.append(NAME).append(": ").append("set").append(" - ");
                builder.append("requestFactory").append(" (");
                requestFactory.appendIdentifier(builder);
                builder.append(")");
                Log.i(Sketch.TAG, builder.toString());
            }
        }
        return this;
    }

    /**
     * 获取Resize计算器
     * @return ResizeCalculator
     */
    public ResizeCalculator getResizeCalculator() {
        return resizeCalculator;
    }

    /**
     * 设置Resize计算器
     * @param resizeCalculator ResizeCalculator
     */
    public Configuration setResizeCalculator(ResizeCalculator resizeCalculator) {
        if(resizeCalculator != null){
            this.resizeCalculator = resizeCalculator;
            if(Sketch.isDebugMode()){
                StringBuilder builder = new StringBuilder();
                builder.append(NAME).append(": ").append("set").append(" - ");
                builder.append("resizeCalculator").append(" (");
                resizeCalculator.appendIdentifier(builder);
                builder.append(")");
                Log.i(Sketch.TAG, builder.toString());
            }
        }
        return this;
    }

    /**
     * 设置是否暂停加载新图片，开启后将只从内存缓存中找寻图片，只影响display请求
     * @param pauseLoad 是否暂停加载新图片，开启后将只从内存缓存中找寻图片，只影响display请求
     */
    public Configuration setPauseLoad(boolean pauseLoad) {
        if(this.pauseLoad != pauseLoad){
            this.pauseLoad = pauseLoad;
            if(Sketch.isDebugMode()){
                StringBuilder builder = new StringBuilder();
                builder.append(NAME).append(": ").append("set").append(" - ");
                builder.append("pauseLoad").append(" (");
                builder.append(pauseLoad);
                builder.append(")");
                Log.i(Sketch.TAG, builder.toString());
            }
        }
        return this;
    }

    /**
     * 是否暂停加载新图片，开启后将只从内存缓存中找寻图片，只影响display请求
     * @return 是否暂停加载新图片，开启后将只从内存缓存中找寻图片，只影响display请求
     */
    public boolean isPauseLoad() {
        return pauseLoad;
    }

    /**
     * 是否暂停下载图片，开启后将不再从网络下载图片，只影响display请求
     * @return 暂停下载图片，开启后将不再从网络下载图片，只影响display请求
     */
    public boolean isPauseDownload() {
        return pauseDownload;
    }

    /**
     * 设置暂停下载图片，开启后将不再从网络下载图片，只影响display请求
     * @param pauseDownload 暂停下载图片，开启后将不再从网络下载图片，只影响display请求
     */
    public Configuration setPauseDownload(boolean pauseDownload) {
        if(this.pauseDownload != pauseDownload){
            this.pauseDownload = pauseDownload;
            if(Sketch.isDebugMode()){
                StringBuilder builder = new StringBuilder();
                builder.append(NAME).append(": ").append("set").append(" - ");
                builder.append("pauseDownload").append(" (");
                builder.append(pauseDownload);
                builder.append(")");
                Log.i(Sketch.TAG, builder.toString());
            }
        }
        return this;
    }

    /**
     * 设置是否开启移动网络下暂停下载的功能
     * @param mobileNetworkPauseDownload 是否开启移动网络下暂停下载的功能
     */
    public Configuration setMobileNetworkPauseDownload(boolean mobileNetworkPauseDownload){
        if(mobileNetworkPauseDownload){
            if(mobileNetworkPauseDownloadManager == null){
                mobileNetworkPauseDownloadManager = new MobileNetworkPauseDownloadManager(context);
            }
            mobileNetworkPauseDownloadManager.setPauseDownload(true);
        }else{
            if(mobileNetworkPauseDownloadManager != null){
                mobileNetworkPauseDownloadManager.setPauseDownload(false);
            }
        }
        return this;
    }

    /**
     * 是否解码GIF图
     * @return true：解码；false：不解码
     */
    public boolean isDecodeGifImage() {
        return decodeGifImage;
    }

    /**
     * 设置是否解码GIF图
     * @param decodeGifImage true：解码；false：不解码
     */
    public Configuration setDecodeGifImage(boolean decodeGifImage) {
        if(this.decodeGifImage != decodeGifImage){
            this.decodeGifImage = decodeGifImage;
            if(Sketch.isDebugMode()){
                StringBuilder builder = new StringBuilder();
                builder.append(NAME).append(": ").append("set").append(" - ");
                builder.append("decodeGifImage").append(" (");
                builder.append(decodeGifImage);
                builder.append(")");
                Log.i(Sketch.TAG, builder.toString());
            }
        }
        return this;
    }

    /**
     * 是否返回低质量的图片
     * @return true: 是
     */
    public boolean isLowQualityImage() {
        return lowQualityImage;
    }

    /**
     * 设置是否返回低质量的图片
     * @param lowQualityImage true:是
     */
    public Configuration setLowQualityImage(boolean lowQualityImage) {
        if(this.lowQualityImage != lowQualityImage){
            this.lowQualityImage = lowQualityImage;
            if(Sketch.isDebugMode()){
                StringBuilder builder = new StringBuilder();
                builder.append(NAME).append(": ").append("set").append(" - ");
                builder.append("lowQualityImage").append(" (");
                builder.append(lowQualityImage);
                builder.append(")");
                Log.i(Sketch.TAG, builder.toString());
            }
        }
        return this;
    }

    /**
     * 是否将图片缓存在本地
     * @return 是否将图片缓存在本地（默认是）
     */
    public boolean isCacheInDisk() {
        return cacheInDisk;
    }

    /**
     * 设置是否将图片缓存在本地
     * @param cacheInDisk 是否将图片缓存在本地（默认是）
     */
    public Configuration setCacheInDisk(boolean cacheInDisk) {
        if(this.cacheInDisk != cacheInDisk) {
            this.cacheInDisk = cacheInDisk;
            if (Sketch.isDebugMode()) {
                StringBuilder builder = new StringBuilder();
                builder.append(NAME).append(": ").append("set").append(" - ");
                builder.append("cacheInDisk").append(" (");
                builder.append(cacheInDisk);
                builder.append(")");
                Log.i(Sketch.TAG, builder.toString());
            }
        }
        return this;
    }

    /**
     * 是否将图片缓存在内存中
     * @return 是否将图片缓存在内存中（默认是）
     */
    public boolean isCacheInMemory() {
        return cacheInMemory;
    }

    /**
     * 设置是否将图片缓存在内存中
     * @param cacheInMemory 是否将图片缓存在内存中（默认是）
     */
    public Configuration setCacheInMemory(boolean cacheInMemory) {
        if(this.cacheInMemory != cacheInMemory) {
            this.cacheInMemory = cacheInMemory;
            if (Sketch.isDebugMode()) {
                StringBuilder builder = new StringBuilder();
                builder.append(NAME).append(": ").append("set").append(" - ");
                builder.append("cacheInMemory").append(" (");
                builder.append(cacheInMemory);
                builder.append(")");
                Log.i(Sketch.TAG, builder.toString());
            }
        }
        return this;
    }

    public String getInfo(){
        StringBuilder builder = new StringBuilder();
        builder.append(NAME).append(": ");

        if(diskCache != null){
            builder.append("diskCache");
            builder.append(" (");
            diskCache.appendIdentifier(builder);
            builder.append(")");
        }

        if(memoryCache != null){
            if(builder.length() > 0) builder.append("; ");
            builder.append("memoryCache");
            builder.append(" (");
            memoryCache.appendIdentifier(builder);
            builder.append(")");
        }

        if(placeholderImageMemoryCache != null){
            if(builder.length() > 0) builder.append("; ");
            builder.append("placeholderImageMemoryCache");
            builder.append(" (");
            placeholderImageMemoryCache.appendIdentifier(builder);
            builder.append(")");
        }

        if(imageDecoder != null){
            if(builder.length() > 0) builder.append("; ");
            builder.append("imageDecoder");
            builder.append(" (");
            imageDecoder.appendIdentifier(builder);
            builder.append(")");
        }

        if(helperFactory != null){
            if(builder.length() > 0) builder.append("; ");
            builder.append("helperFactory");
            builder.append(" (");
            helperFactory.appendIdentifier(builder);
            builder.append(")");
        }

        if(defaultImageDisplayer != null){
            if(builder.length() > 0) builder.append("; ");
            builder.append("defaultImageDisplayer");
            builder.append(" (");
            defaultImageDisplayer.appendIdentifier(builder);
            builder.append(")");
        }

        if(defaultCutImageProcessor != null){
            if(builder.length() > 0) builder.append("; ");
            builder.append("defaultCutImageProcessor");
            builder.append(" (");
            defaultCutImageProcessor.appendIdentifier(builder);
            builder.append(")");
        }

        if(requestFactory != null){
            if(builder.length() > 0) builder.append("; ");
            builder.append("requestFactory");
            builder.append(" (");
            requestFactory.appendIdentifier(builder);
            builder.append(")");
        }

        if(imageDownloader != null){
            if(builder.length() > 0) builder.append("; ");
            builder.append("imageDownloader");
            builder.append(" (");
            imageDownloader.appendIdentifier(builder);
            builder.append(")");
        }

        if(requestExecutor != null){
            if(builder.length() > 0) builder.append("; ");
            builder.append("requestExecutor");
            builder.append(" (");
            requestExecutor.appendIdentifier(builder);
            builder.append(")");
        }

        if(imageSizeCalculator != null){
            if(builder.length() > 0) builder.append("; ");
            builder.append("imageSizeCalculator");
            builder.append(" (");
            imageSizeCalculator.appendIdentifier(builder);
            builder.append(")");
        }

        if(resizeCalculator != null){
            if(builder.length() > 0) builder.append("; ");
            builder.append("resizeCalculator");
            builder.append(" (");
            resizeCalculator.appendIdentifier(builder);
            builder.append(")");
        }

        if(builder.length() > 0) builder.append("; ");
        builder.append("pauseLoad");
        builder.append(" (");
        builder.append(pauseLoad);
        builder.append(")");

        builder.append("; ");
        builder.append("pauseDownload");
        builder.append(" (");
        builder.append(pauseDownload);
        builder.append(")");

        builder.append("; ");
        builder.append("decodeGifImage");
        builder.append(" (");
        builder.append(decodeGifImage);
        builder.append(")");

        builder.append("; ");
        builder.append("lowQualityImage");
        builder.append(" (");
        builder.append(lowQualityImage);
        builder.append(")");

        builder.append("; ");
        builder.append("cacheInMemory");
        builder.append(" (");
        builder.append(cacheInMemory);
        builder.append(")");

        builder.append("; ");
        builder.append("cacheInDisk");
        builder.append(" (");
        builder.append(cacheInDisk);
        builder.append(")");

        return builder.toString();
    }
}
