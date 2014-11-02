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

import android.widget.ImageView;

import me.xiaopan.android.spear.process.ImageProcessor;
import me.xiaopan.android.spear.util.ImageSize;

/**
 * 加载请求
 */
public class LoadRequest extends DownloadRequest{
    /* 可配置属性 */
    ImageSize maxsize;	// 最大尺寸，用于读取图片时计算inSampleSize
    ImageSize resize;	// 新的尺寸，BitmapProcessor会根据此尺寸和scaleType来创建新的图片
    ImageProcessor imageProcessor;	// 图片处理器
    ImageView.ScaleType scaleType; // 图片缩放方式，BitmapProcessor会根据resize和scaleType来创建新的图片

    LoadListener loadListener;	// 监听器
    ProgressListener loadProgressListener;  // 加载进度监听器

    /**
	 * 获取加载监听器
	 */
	public LoadListener getLoadListener() {
		return loadListener;
	}

    public LoadRequest setLoadListener(LoadListener loadListener) {
        this.loadListener = loadListener;
        return this;
    }

    /**
     * 获取缩放类型
     */
    public ImageView.ScaleType getScaleType() {
        return scaleType;
    }

    /**
     * 获取新的尺寸，BitmapProcessor会根据此尺寸和scaleType来创建新的图片
     */
    public ImageSize getResize() {
		return resize;
	}

	/**
	 * 获取最大尺寸，用于读取图片时计算inSampleSize
	 */
	public ImageSize getMaxsize() {
		return maxsize;
	}

    /**
     * 获取图片处理器
     */
    public ImageProcessor getImageProcessor() {
        return imageProcessor;
    }

    /**
     * 获取加载进度监听器
     * @return 加载进度监听器
     */
    public ProgressListener getLoadProgressListener() {
        return loadProgressListener;
    }

    /**
     * 设置加载进度监听器
     * @param loadProgressListener 加载进度监听器
     */
    public void setLoadProgressListener(ProgressListener loadProgressListener) {
        this.loadProgressListener = loadProgressListener;
    }
}
