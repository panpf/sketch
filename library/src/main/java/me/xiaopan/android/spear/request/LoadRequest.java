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

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;

import me.xiaopan.android.spear.Spear;
import me.xiaopan.android.spear.decode.AssetsDecodeListener;
import me.xiaopan.android.spear.decode.CacheFileDecodeListener;
import me.xiaopan.android.spear.decode.ContentDecodeListener;
import me.xiaopan.android.spear.decode.DrawableDecodeListener;
import me.xiaopan.android.spear.decode.FileDecodeListener;
import me.xiaopan.android.spear.decode.ImageDecoder;
import me.xiaopan.android.spear.execute.RequestExecutor;
import me.xiaopan.android.spear.process.ImageProcessor;
import me.xiaopan.android.spear.util.ImageScheme;
import me.xiaopan.android.spear.util.ImageSize;

/**
 * 加载请求
 */
public class LoadRequest extends DownloadRequest{
    private static final String NAME = "LoadRequest";

    /* 加载请求用到的属性 */
    private ImageSize resize;	// 裁剪尺寸，ImageProcessor会根据此尺寸和scaleType来裁剪图片
    private ImageSize maxsize;	// 最大尺寸，用于读取图片时计算inSampleSize
    private LoadListener loadListener;	// 监听器
    private ImageProcessor imageProcessor;	// 图片处理器
    private ImageView.ScaleType scaleType; // 图片缩放方式，ImageProcessor会根据resize和scaleType来创建新的图片

    /* 辅助加载的属性 */
    private RunStatus runStatus;
    private LoadListener.ImageFrom imageFrom;
    private ImageDecoder.DecodeListener onDecodeListener;

    /**
     * 获取裁剪尺寸，ImageProcessor会根据此尺寸和scaleType来创建新的图片
     */
    public ImageSize getResize() {
        return resize;
    }

    /**
     * 设置裁剪尺寸，ImageProcessor会根据此尺寸和scaleType来创建新的图片
     */
    public void setResize(ImageSize resize) {
        this.resize = resize;
    }

    /**
     * 获取最大尺寸，用于读取图片时计算inSampleSize
     */
    public ImageSize getMaxsize() {
        return maxsize;
    }

    /**
     * 设置最大尺寸，用于读取图片时计算inSampleSize
     */
    public void setMaxsize(ImageSize maxsize) {
        this.maxsize = maxsize;
    }

    /**
     * 获取缩放类型
     */
    public ImageView.ScaleType getScaleType() {
        return scaleType;
    }

    /**
     * 设置缩放类型
     */
    public void setScaleType(ImageView.ScaleType scaleType) {
        this.scaleType = scaleType;
    }

    /**
     * 获取图片处理器
     */
    public ImageProcessor getImageProcessor() {
        return imageProcessor;
    }

    /**
     * 设置图片处理器
     */
    public void setImageProcessor(ImageProcessor imageProcessor) {
        this.imageProcessor = imageProcessor;
    }

    /**
     * 获取加载监听器
     */
    public LoadListener getLoadListener() {
        return loadListener;
    }

    /**
     * 设置加载监听器
     */
    public LoadRequest setLoadListener(LoadListener loadListener) {
        this.loadListener = loadListener;
        return this;
    }


    /**
     * 设置结果来自哪里
     * @param imageFrom 结果来自哪里
     */
    public void setImageFrom(LoadListener.ImageFrom imageFrom) {
        this.imageFrom = imageFrom;
    }

    /**
     * 设置解码监听器
     * @param onDecodeListener 解码监听器
     */
    public void setOnDecodeListener(ImageDecoder.DecodeListener onDecodeListener) {
        this.onDecodeListener = onDecodeListener;
    }

    /**
     * 设置运行状态
     * @param runStatus 运行状态
     */
    public void setRunStatus(RunStatus runStatus) {
        this.runStatus = runStatus;
    }

    @Override
    public void run() {
        if(runStatus == null){
            new IllegalStateException("runStatus 参数为null，无法执行").printStackTrace();
            return;
        }

        switch(runStatus){
            case LOAD:
                executeLoad();
                break;
            case DOWNLOAD:
                executeDownload();
                break;
            default:
                new IllegalStateException(runStatus.name()+" 属于未知的类型，没法搞").printStackTrace();
                break;
        }
    }

    @Override
    public void dispatch(RequestExecutor requestExecutor) {
        switch(getImageScheme()){
            case HTTP:
            case HTTPS :
                // 要先创建缓存文件
                if(isEnableDiskCache()){
                    setCacheFile(getSpear().getConfiguration().getDiskCache().createCacheFile(this));
                }

                // 如果缓存文件存在就从本地读取
                File cacheFile = getCacheFile();
                if(cacheFile != null && cacheFile.exists()){
                    setRunStatus(LoadRequest.RunStatus.LOAD);
                    setImageFrom(LoadListener.ImageFrom.LOCAL);
                    setOnDecodeListener(new CacheFileDecodeListener(cacheFile, this));
                    requestExecutor.getLocalTaskExecutor().execute(this);
                    if(Spear.isDebugMode()) Log.d(Spear.LOG_TAG, NAME + "：" + "LOAD - HTTP - 本地" + "；" + getName());
                    break;
                }

                // 从网络下载
                setDownloadListener(new LoadJoinDownloadListener(requestExecutor.getLocalTaskExecutor(), this));
                setRunStatus(LoadRequest.RunStatus.DOWNLOAD);
                requestExecutor.getNetTaskExecutor().execute(this);
                if(Spear.isDebugMode()) Log.d(Spear.LOG_TAG, NAME + "：" + "LOAD - HTTP - 网络" + "；" + getName());
                break;
            case FILE :
                setRunStatus(LoadRequest.RunStatus.LOAD);
                setImageFrom(LoadListener.ImageFrom.LOCAL);
                setOnDecodeListener(new FileDecodeListener(new File(getUri()), this));
                requestExecutor.getLocalTaskExecutor().execute(this);
                if(Spear.isDebugMode()) Log.d(Spear.LOG_TAG, NAME + "：" + "LOAD - FILE" + "；" + getName());
                break;
            case ASSETS :
                setRunStatus(LoadRequest.RunStatus.LOAD);
                setImageFrom(LoadListener.ImageFrom.LOCAL);
                setOnDecodeListener(new AssetsDecodeListener(ImageScheme.ASSETS.crop(getUri()), this));
                requestExecutor.getLocalTaskExecutor().execute(this);
                if(Spear.isDebugMode()) Log.d(Spear.LOG_TAG, NAME + "：" + "LOAD - ASSETS" + "；" + getName());
                break;
            case CONTENT :
                setRunStatus(LoadRequest.RunStatus.LOAD);
                setImageFrom(LoadListener.ImageFrom.LOCAL);
                setOnDecodeListener(new ContentDecodeListener(getUri(), this));
                requestExecutor.getLocalTaskExecutor().execute(this);
                if(Spear.isDebugMode()) Log.d(Spear.LOG_TAG, NAME + "：" + "LOAD - CONTENT" + "；" + getName());
                break;
            case DRAWABLE :
                setRunStatus(LoadRequest.RunStatus.LOAD);
                setImageFrom(LoadListener.ImageFrom.LOCAL);
                setOnDecodeListener(new DrawableDecodeListener(ImageScheme.DRAWABLE.crop(getUri()), this));
                requestExecutor.getLocalTaskExecutor().execute(this);
                if(Spear.isDebugMode()) Log.d(Spear.LOG_TAG, NAME + "：" + "LOAD - DRAWABLE" + "；" + getName());
                break;
            default:
                if(Spear.isDebugMode()) Log.e(Spear.LOG_TAG, NAME + "：" + "LOAD - 未知的协议格式" + "：" + getUri());
                break;
        }
    }

    /**
     * 执行加载
     */
    public void executeLoad(){
        if(isCanceled()){
            if(getLoadListener() != null){
                getLoadListener().onCanceled();
            }
            return;
        }

        setStatus(Request.Status.LOADING);

        // 解码
        Bitmap bitmap = null;
        if(getUri().endsWith(".apk")){
            PackageManager packageManager = getSpear().getConfiguration().getContext().getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageArchiveInfo(getUri(), PackageManager.GET_CONFIGURATIONS);
            if(packageInfo != null){
                packageInfo.applicationInfo.sourceDir = getUri();
                packageInfo.applicationInfo.publicSourceDir = getUri();
                Drawable drawable = packageInfo.applicationInfo.loadIcon(packageManager);
                if(drawable != null && drawable instanceof BitmapDrawable){
                    bitmap = ((BitmapDrawable) drawable).getBitmap();
                }
            }
        }else{
            try{
                bitmap = getSpear().getConfiguration().getImageDecoder().decode(getSpear(), getMaxsize(), onDecodeListener);
            }catch(IOException e1){
                e1.printStackTrace();
            }
        }

        //处理
        if(bitmap != null && !bitmap.isRecycled()){
            ImageProcessor imageProcessor = getImageProcessor();
            if(imageProcessor == null && getResize() != null){
                imageProcessor = getSpear().getConfiguration().getDefaultCutImageProcessor();
            }
            if(imageProcessor != null){
                Bitmap newBitmap = imageProcessor.process(bitmap, getResize(), getScaleType());
                if(newBitmap != bitmap){
                    bitmap.recycle();
                    bitmap = newBitmap;
                }
            }
        }

        if(isCanceled()){
            if(getLoadListener() != null){
                getLoadListener().onCanceled();
            }
            return;
        }

        if(bitmap != null && !bitmap.isRecycled()){
            if(!(this instanceof DisplayRequest)){
                setStatus(Request.Status.COMPLETED);
            }
            if(getLoadListener() != null){
                getLoadListener().onCompleted(bitmap, imageFrom);
            }
        }else{
            if(!(this instanceof DisplayRequest)){
                setStatus(Request.Status.FAILED);
            }
            if(getLoadListener() != null){
                getLoadListener().onFailed(null);
            }
        }
    }

    public enum RunStatus{
        LOAD,
        DOWNLOAD,
    }
}
