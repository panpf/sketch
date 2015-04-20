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
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import me.xiaopan.spear.process.ImageProcessor;

/**
 * 显示选项
 */
public class LoadOptions extends DownloadOptions{
    private ScaleType scaleType; //图片缩放方式，在处理图片的时候会用到
    private ImageSize maxSize;	//解码最大图片尺寸，用于读取图片时计算inSampleSize
    private ImageSize resize;	// 处理尺寸，ImageProcessor会根据此尺寸来创建新的图片
    private ImageProcessor imageProcessor;	//图片处理器
    private boolean disableGifImage;

    public LoadOptions(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        setMaxSize((int) (displayMetrics.widthPixels * 1.5f), (int) (displayMetrics.heightPixels * 1.5f));
    }

    @Override
    public LoadOptions setEnableDiskCache(boolean isEnableDiskCache) {
        super.setEnableDiskCache(isEnableDiskCache);
        return this;
    }

    /**
     * 获取最大尺寸
     * @return 最大尺寸
     */
    public ImageSize getMaxSize() {
        return maxSize;
    }

    /**
     * 设置最大尺寸，在解码的时候会使用此Size来计算inSimpleSize
     * @param maxSize 最大尺寸
     * @return LoadOptions
     */
    public LoadOptions setMaxSize(ImageSize maxSize){
        this.maxSize = maxSize;
        return this;
    }

    /**
     * 设置最大尺寸，在解码的时候会使用此Size来计算inSimpleSize
     * @param width 宽
     * @param height 高
     * @return LoadOptions
     */
    public LoadOptions setMaxSize(int width, int height){
        this.maxSize = new ImageSize(width, height);
        return this;
    }

    /**
     * 获取新尺寸
     * @return 新尺寸
     */
    public ImageSize getResize() {
        return resize;
    }

    /**
     * 裁剪图片，ImageProcessor会根据此宽高和ScaleType裁剪图片
     * @param resize 新的尺寸
     * @return LoadOptions
     */
    public LoadOptions setResize(ImageSize resize){
        this.resize = resize;
        return this;
    }

    /**
     * 裁剪图片，ImageProcessor会根据此宽高和ScaleType裁剪图片
     * @param width 宽
     * @param height 高
     * @return LoadOptions
     */
    public LoadOptions setResize(int width, int height){
        this.resize = new ImageSize(width, height);
        return this;
    }

    /**
     * 获取图片处理器
     * @return 图片处理器
     */
    public ImageProcessor getImageProcessor() {
        return imageProcessor;
    }

    /**
     * 设置图片处理器，图片处理器会根据resize和ScaleType创建一张新的图片
     * @param processor 图片处理器
     * @return LoadOptions
     */
    public LoadOptions setImageProcessor(ImageProcessor processor){
        this.imageProcessor = processor;
        return this;
    }

    /**
     * 获取缩放类型
     * @return 缩放类型
     */
    public ScaleType getScaleType() {
        return scaleType;
    }

    /**
     * 设置ScaleType，ImageProcessor会根据resize和ScaleType创建一张新的图片
     * @param scaleType ScaleType
     * @return LoadOptions
     */
    public LoadOptions setScaleType(ImageView.ScaleType scaleType){
        this.scaleType = scaleType;
        return this;
    }

    /**
     * 是否禁止处理GIF图片
     * @return true：是
     */
    public boolean isDisableGifImage() {
        return disableGifImage;
    }

    /**
     * 设置是否禁止处理GIF图片
     * @param disableGifImage true：是
     */
    public LoadOptions setDisableGifImage(boolean disableGifImage) {
        this.disableGifImage = disableGifImage;
        return this;
    }

    @Override
    public LoadOptions setHandleLevel(HandleLevel handleLevel) {
        super.setHandleLevel(handleLevel);
        return this;
    }
}
