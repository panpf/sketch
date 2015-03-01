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

import java.io.File;

import me.xiaopan.android.spear.Spear;
import me.xiaopan.android.spear.download.ImageDownloader;
import me.xiaopan.android.spear.process.ImageProcessor;
import me.xiaopan.android.spear.util.ImageScheme;
import me.xiaopan.android.spear.util.ImageSize;

/**
 * 加载请求
 */
public class LoadRequest extends DownloadRequest{
    private static final String NAME = "LoadRequest";

    /* 加载请求用到的属性 */
    protected ImageSize resize;	// 裁剪尺寸，ImageProcessor会根据此尺寸和scaleType来裁剪图片
    protected ImageSize maxsize;	// 最大尺寸，用于读取图片时计算inSampleSize
    private LoadListener loadListener;	// 监听器
    protected ImageProcessor imageProcessor;	// 图片处理器
    protected ImageView.ScaleType scaleType; // 图片缩放方式，ImageProcessor会根据resize和scaleType来创建新的图片

    /* 辅助加载的属性 */
    private ImageFrom imageFrom;
    private byte[] imageData;
    private Bitmap bitmap;

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
    public void setImageFrom(ImageFrom imageFrom) {
        this.imageFrom = imageFrom;
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
        if(runStatus == RunStatus.LOAD){
            executeLoad();
        }else{
            super.run();
        }
    }

    @Override
    public void dispatch() {
        if(imageScheme == ImageScheme.HTTP || imageScheme == ImageScheme.HTTPS){
            this.cacheFile = enableDiskCache?spear.getConfiguration().getDiskCache().createCacheFile(this):null;

            // 如果不需要缓存或缓存文件不存在就从网络下载
            if(cacheFile == null || !cacheFile.exists()){
                runDownload();
                if(Spear.isDebugMode()){
                    Log.d(Spear.TAG, NAME + "：" + "LOAD - 网络" + "；" + name);
                }
                return;
            }
        }

        setImageFrom(ImageFrom.LOCAL);
        runLoad();
        if(Spear.isDebugMode()) Log.d(Spear.TAG, NAME + "：" + "LOAD - 本地" + "；" + name);
    }

    /**
     * 执行加载
     */
    public void executeLoad(){
        if(isCanceled()){
            if(Spear.isDebugMode()){
                Log.w(Spear.TAG, NAME + "：" + "已取消加载（加载刚开始）" + "；" + name);
            }
            return;
        }

        toLoadingStatus();

        // 解码
        Bitmap bitmap = spear.getConfiguration().getImageDecoder().decode(this);

        if(isCanceled()){
            if(bitmap != null && !bitmap.isRecycled()){
                bitmap.recycle();
            }
            if(Spear.isDebugMode()){
                Log.w(Spear.TAG, NAME + "：" + "已取消加载（解码完成后）" + "；" + name);
            }
            return;
        }

        //处理
        if(bitmap != null && !bitmap.isRecycled()){
            ImageProcessor imageProcessor = getImageProcessor();
            if(imageProcessor == null && getResize() != null){
                imageProcessor = spear.getConfiguration().getDefaultCutImageProcessor();
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
            if(bitmap != null && !bitmap.isRecycled()){
                bitmap.recycle();
            }
            if(Spear.isDebugMode()){
                Log.w(Spear.TAG, NAME + "：" + "已取消加载（图片处理后）" + "；" + name);
            }
            return;
        }

        if(bitmap != null && !bitmap.isRecycled()){
            handleLoadCompleted(bitmap, imageFrom);
        }else{
            toFailedStatus(FailureCause.DECODE_FAIL);
        }
    }

    @Override
    public void toCompletedStatus() {
        this.status = Status.COMPLETED;
        if(loadListener != null){
            loadListener.onCompleted(bitmap, imageFrom);
        }
    }

    @Override
    public void toFailedStatus(FailureCause failureCause) {
        this.status = Status.FAILED;
        if(loadListener != null){
            loadListener.onFailed(failureCause);
        }
    }

    @Override
    public void toCanceledStatus() {
        this.status = Status.CANCELED;
        if(loadListener != null){
            loadListener.onCanceled();
        }
    }

    public void handleLoadCompleted(Bitmap bitmap, ImageFrom imageFrom){
        this.bitmap = bitmap;
        this.imageFrom = imageFrom;
        toCompletedStatus();
    }

    @Override
    public void handleDownloadCompleted(ImageDownloader.DownloadResult downloadResult) {
        this.imageFrom = downloadResult.isFromNetwork()?ImageFrom.NETWORK:ImageFrom.DISK_CACHE;

        if(downloadResult.getResult().getClass().isAssignableFrom(File.class)){
            this.cacheFile = (File) downloadResult.getResult();
        }else{
            this.imageData = (byte[]) downloadResult.getResult();
        }

        this.runStatus = LoadRequest.RunStatus.LOAD;
        spear.getConfiguration().getRequestExecutor().getLocalRequestExecutor().execute(this);
    }
}
