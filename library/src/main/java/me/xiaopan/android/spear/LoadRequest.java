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

package me.xiaopan.android.spear;

import android.widget.ImageView;

import me.xiaopan.android.spear.process.ImageProcessor;

/**
 * 加载请求
 */
public interface LoadRequest extends DownloadRequest{
    /**
     * 获取裁剪尺寸，ImageProcessor会根据此尺寸和scaleType来创建新的图片
     */
    ImageSize getResize();

    /**
     * 设置裁剪尺寸，ImageProcessor会根据此尺寸和scaleType来创建新的图片
     */
    void setResize(ImageSize resize);

    /**
     * 获取最大尺寸，用于读取图片时计算inSampleSize
     */
    ImageSize getMaxsize();

    /**
     * 设置最大尺寸，用于读取图片时计算inSampleSize
     */
    void setMaxsize(ImageSize maxsize);

    /**
     * 获取缩放类型
     */
    ImageView.ScaleType getScaleType();

    /**
     * 设置缩放类型
     */
    void setScaleType(ImageView.ScaleType scaleType);

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
     * 获取图片数据
     * @return 图片数据
     */
    byte[] getImageData();
}
