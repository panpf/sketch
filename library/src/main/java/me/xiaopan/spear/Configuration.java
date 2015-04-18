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

package me.xiaopan.spear;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import me.xiaopan.spear.cache.DiskCache;
import me.xiaopan.spear.cache.LruDiskCache;
import me.xiaopan.spear.cache.LruMemoryCache;
import me.xiaopan.spear.cache.MemoryCache;
import me.xiaopan.spear.decode.DefaultImageDecoder;
import me.xiaopan.spear.decode.ImageDecoder;
import me.xiaopan.spear.display.DefaultImageDisplayer;
import me.xiaopan.spear.display.ImageDisplayer;
import me.xiaopan.spear.download.HttpUrlConnectionImageDownloader;
import me.xiaopan.spear.download.ImageDownloader;
import me.xiaopan.spear.execute.DefaultRequestExecutor;
import me.xiaopan.spear.execute.RequestExecutor;
import me.xiaopan.spear.process.CutImageProcessor;
import me.xiaopan.spear.process.ImageProcessor;
import me.xiaopan.spear.util.MobileNetworkPauseDownloadManager;

public class Configuration {
    private Context context;	//上下文
    private Handler handler;    // 异步线程回调用
    private DiskCache diskCache;    // 磁盘缓存器
    private MemoryCache memoryCache;	//图片缓存器
    private ImageDecoder imageDecoder;	//图片解码器
    private HelperFactory helperFactory;    // 协助器工厂
    private ImageDisplayer defaultImageDisplayer;   // 默认的图片显示器，当DisplayRequest中没有指定显示器的时候就会用到
    private ImageProcessor defaultCutImageProcessor;    // 默认的图片裁剪处理器
    private RequestFactory requestFactory;  // 请求工厂
    private ImageDownloader imageDownloader;	//图片下载器
    private RequestExecutor requestExecutor;	//请求执行器
    private ImageSizeCalculator imageSizeCalculator; // 图片尺寸计算器

    private boolean pauseLoad;   // 暂停加载新图片，开启后将只从内存缓存中找寻图片，只影响display请求
    private boolean pauseDownload;   // 暂停下载新图片，开启后将不再从网络下载新图片，只影响display请求
    private MobileNetworkPauseDownloadManager mobileNetworkPauseDownloadManager;

    public Configuration(Context context){
        this.context = context;
        this.diskCache = new LruDiskCache(context);
        this.memoryCache = new LruMemoryCache(context);
        this.imageDecoder = new DefaultImageDecoder();
        this.helperFactory = new HelperFactoryImpl();
        this.requestFactory = new RequestFactoryImpl();
        this.imageDownloader = new HttpUrlConnectionImageDownloader();
        this.requestExecutor = new DefaultRequestExecutor.Builder().build();
        this.imageSizeCalculator = new ImageSizeCalculatorImpl();
        this.defaultImageDisplayer = new DefaultImageDisplayer();
        this.defaultCutImageProcessor = new CutImageProcessor();
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
            this.memoryCache = memoryCache;
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
        }
        return this;
    }

    /**
     * 设置是否暂停加载新图片，开启后将只从内存缓存中找寻图片，只影响display请求
     * @param pauseLoad 是否暂停加载新图片，开启后将只从内存缓存中找寻图片，只影响display请求
     */
    public void setPauseLoad(boolean pauseLoad) {
        if(this.pauseLoad == pauseLoad){
            return;
        }
        this.pauseLoad = pauseLoad;
        if(Spear.isDebugMode()){
            if(this.pauseLoad){
                Log.w(Spear.TAG, "pauseLoad");
            }else{
                Log.d(Spear.TAG, "resumeLoad");
            }
        }
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
    public void setPauseDownload(boolean pauseDownload) {
        if(this.pauseDownload == pauseDownload){
            return;
        }
        this.pauseDownload = pauseDownload;
        if(Spear.isDebugMode()){
            if(this.pauseDownload){
                Log.w(Spear.TAG, "pauseDownload");
            }else{
                Log.d(Spear.TAG, "resumeDownload");
            }
        }
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
}
