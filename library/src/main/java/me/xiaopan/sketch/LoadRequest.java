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

import java.io.File;

import me.xiaopan.sketch.process.ImageProcessor;

/**
 * 加载请求
 */
public interface LoadRequest extends DownloadRequest{
    /**
     * 获取新的尺寸，ImageProcessor会根据此尺寸来裁剪图片
     */
    Resize getResize();

    /**
     * 设置新的尺寸，ImageProcessor会根据此尺寸来裁剪图片
     */
    void setResize(Resize resize);

    /**
     * 是否强制使用resize
     * @return true：最终返回的图片尺寸一定跟resize一样
     */
    boolean isForceUseResize();

    /**
     * 设置是否强制使用resize
     * @param forceUseResize true：最终返回的图片尺寸一定跟resize一样
     */
    void setForceUseResize(boolean forceUseResize);

    /**
     * 获取最大尺寸，用于读取图片时计算inSampleSize
     */
    MaxSize getMaxSize();

    /**
     * 设置最大尺寸，用于读取图片时计算inSampleSize
     */
    void setMaxSize(MaxSize maxSize);

    /**
     * 是否返回低质量的图片
     */
    boolean isLowQualityImage();

    /**
     * 设置是否返回低质量的图片
     */
    void setLowQualityImage(boolean lowQualityImage);

    /**
     * 获取图片处理器
     */
    ImageProcessor getImageProcessor();

    /**
     * 设置图片处理器
     */
    void setImageProcessor(ImageProcessor imageProcessor);

    /**
     * 设置加载监听器
     */
    void setLoadListener(LoadListener loadListener);

    /**
     * 是否解码Gif图片，如果为false，Gif图将使用BitmapFactory来解码
     */
    boolean isDecodeGifImage();

    /**
     * 设置是否解码Gif图，如果为false，Gif图将使用BitmapFactory来解码
     */
    void setDecodeGifImage(boolean isDecodeGifImage);

    /**
     * 获取缓存文件
     * @return 缓存文件
     */
    File getCacheFile();

    /**
     * 获取图片数据
     * @return 图片数据
     */
    byte[] getImageData();

    /**
     * 是否是本地APK文件
     * @return true：是
     */
    boolean isLocalApkFile();

    /**
     * 获取图片类型
     * @return 图片类型
     */
    String getMimeType();

    /**
     * 设置图片类型
     * @param mimeType 图片类型
     */
    void setMimeType(String mimeType);
}
