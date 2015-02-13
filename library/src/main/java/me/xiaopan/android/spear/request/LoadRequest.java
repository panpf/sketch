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

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import me.xiaopan.android.spear.Spear;
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
    private byte[] imageData;

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
     * 设置运行状态
     * @param runStatus 运行状态
     */
    public void setRunStatus(RunStatus runStatus) {
        this.runStatus = runStatus;
    }

    /**
     * 获取图片数据
     * @return 图片数据
     */
    public byte[] getImageData() {
        return imageData;
    }

    /**
     * 设置图片数据
     * @param imageData 图片数据
     */
    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
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
        if(getImageScheme() == ImageScheme.HTTP || getImageScheme() == ImageScheme.HTTPS){
            setCacheFile(isEnableDiskCache()?getSpear().getConfiguration().getDiskCache().createCacheFile(this):null);

            // 如果不需要缓存或缓存文件不存在就从网络下载
            if(getCacheFile() == null || !getCacheFile().exists()){
                setDownloadListener(new LoadJoinDownloadListener(requestExecutor.getLocalTaskExecutor(), this));
                setRunStatus(RunStatus.DOWNLOAD);
                requestExecutor.getNetTaskExecutor().execute(this);
                if(Spear.isDebugMode()) Log.d(Spear.TAG, NAME + "：" + "LOAD - 网络" + "；" + getName());
                return;
            }
        }

        setRunStatus(RunStatus.LOAD);
        setImageFrom(LoadListener.ImageFrom.LOCAL);
        requestExecutor.getLocalTaskExecutor().execute(this);
        if(Spear.isDebugMode()) Log.d(Spear.TAG, NAME + "：" + "LOAD - 本地" + "；" + getName());
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
        Bitmap bitmap = getSpear().getConfiguration().getImageDecoder().decode(this);

        if(isCanceled()){
            if(getLoadListener() != null){
                getLoadListener().onCanceled();
            }
            if(bitmap != null && !bitmap.isRecycled()){
                bitmap.recycle();
            }
            return;
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
            if(bitmap != null && !bitmap.isRecycled()){
                bitmap.recycle();
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
