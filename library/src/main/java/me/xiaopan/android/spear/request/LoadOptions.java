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

import android.content.Context;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import me.xiaopan.android.spear.process.CutImageProcessor;
import me.xiaopan.android.spear.process.ImageProcessor;
import me.xiaopan.android.spear.util.ImageSize;

/**
 * 显示选项
 */
public class LoadOptions extends DownloadOptions{
    private ScaleType scaleType; //图片缩放方式，在处理图片的时候会用到
    private ImageSize maxsize;	//解码最大图片尺寸，用于读取图片时计算inSampleSize
    private ImageSize resize;	// 处理尺寸，BitmapProcessor会根据此尺寸来创建新的图片
    private ImageProcessor imageProcessor;	//图片处理器

    public LoadOptions(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        maxsize((int) (displayMetrics.widthPixels*1.5f), (int) (displayMetrics.heightPixels*1.5f));
    }

    @Override
    public LoadOptions disableDiskCache() {
        super.disableDiskCache();
        return this;
    }

    @Override
    public LoadOptions diskCacheTimeout(long diskCacheTimeout) {
        super.diskCacheTimeout(diskCacheTimeout);
        return this;
    }

    /**
     * 设置最大尺寸，在解码的时候会使用此Size来计算inSimpleSize
     * @param maxsize 最大尺寸
     * @return LoadOptions
     */
    public LoadOptions maxsize(ImageSize maxsize){
        this.maxsize = maxsize;
        return this;
    }

    /**
     * 设置最大尺寸，在解码的时候会使用此Size来计算inSimpleSize
     * @param width 宽
     * @param height 高
     * @return LoadOptions
     */
    public LoadOptions maxsize(int width, int height){
        this.maxsize = new ImageSize(width, height);
        return this;
    }

    /**
     * 重新修改宽高，BitmapProcessor会根据此宽高和ScaleType创建一张新的图片
     * @param resize 新的尺寸
     * @return LoadOptions
     */
    public LoadOptions resize(ImageSize resize){
        this.resize = resize;
        if(this.resize != null && imageProcessor == null){
            imageProcessor = new CutImageProcessor();
        }
        return this;
    }

    /**
     * 重新修改宽高，BitmapProcessor会根据此宽高和ScaleType创建一张新的图片
     * @param width 宽
     * @param height 高
     * @return LoadOptions
     */
    public LoadOptions resize(int width, int height){
        this.resize = new ImageSize(width, height);
        if(imageProcessor == null){
            imageProcessor = new CutImageProcessor();
        }
        return this;
    }

    /**
     * 设置图片处理器，图片处理器会根据resize和ScaleType创建一张新的图片
     * @param processor 图片处理器
     * @return LoadOptions
     */
    public LoadOptions processor(ImageProcessor processor){
        this.imageProcessor = processor;
        return this;
    }

    /**
     * 设置ScaleType，BitmapProcessor会根据resize和ScaleType创建一张新的图片
     * @param scaleType ScaleType
     * @return LoadOptions
     */
    public LoadOptions scaleType(ImageView.ScaleType scaleType){
        this.scaleType = scaleType;
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
     * 获取最大尺寸
     * @return 最大尺寸
     */
    public ImageSize getMaxsize() {
        return maxsize;
    }

    /**
     * 获取新尺寸
     * @return 新尺寸
     */
    public ImageSize getResize() {
        return resize;
    }

    /**
     * 获取图片处理器
     * @return 图片处理器
     */
    public ImageProcessor getImageProcessor() {
        return imageProcessor;
    }
}
