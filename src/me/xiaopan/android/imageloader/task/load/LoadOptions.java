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

package me.xiaopan.android.imageloader.task.load;

import me.xiaopan.android.imageloader.process.BitmapProcessor;
import me.xiaopan.android.imageloader.task.download.DownloadOptions;
import me.xiaopan.android.imageloader.util.ImageSize;
import android.widget.ImageView.ScaleType;

/**
 * 显示选项
 */
public class LoadOptions extends DownloadOptions{
    private ScaleType scaleType; //图片缩放方式，在处理图片的时候会用到
    private ImageSize decodeMaxSize;	//解码最大图片尺寸，用于读取图片时计算inSampleSize
    private ImageSize processSize;	// 处理尺寸，BitmapProcessor会根据此尺寸来创建新的图片
	private BitmapProcessor processor;	//位图处理器

	/**
     * 获取缩放方式
     * @return 图片缩放方式，在处理图片的时候会用到
     */
    public ScaleType getScaleType() {
        return scaleType;
    }

    /**
     * 设置图片缩放方式
     * @param scaleType 图片缩放方式，在处理图片的时候会用到
     */
    public LoadOptions setScaleType(ScaleType scaleType) {
        this.scaleType = scaleType;
        return this;
    }

    /**
     * 获取解码最大尺寸
     * @return 解码最大尺寸，将会根据最大尺寸在读取图片的时候适当的缩放
     */
    public ImageSize getDecodeMaxSize() {
        return decodeMaxSize;
    }

    /**
     * 设置解码最大尺寸
     * @param decodeMaxSize 解码最大尺寸，将会根据最大尺寸在读取图片的时候适当的缩放
     */
    public LoadOptions setDecodeMaxSize(ImageSize decodeMaxSize) {
        this.decodeMaxSize = decodeMaxSize;
        return this;
    }

	/**
     * 获取图片处理器
     * @return 图片处理器，在敬爱那个图片读到内存中之后会使用此处理器将图片处理一下，使之可以变成您想要的效果
     */
	public BitmapProcessor getProcessor() {
		return processor;
	}

    /**
     * 设置图片处理器
     * @param processor 图片处理器，在将图片读到内存中之后会使用此处理器将图片处理一下，使之可以变成您想要的效果
     */
	public LoadOptions setProcessor(BitmapProcessor processor) {
		this.processor = processor;
        return this;
	}

    /**
     * 获取处理尺寸
     * @return 处理尺寸
     */
    public ImageSize getProcessSize() {
		return processSize;
	}

    /**
     * 设置处理尺寸
     * @param processSize 处理尺寸
     */
	public LoadOptions setProcessSize(ImageSize processSize) {
		this.processSize = processSize;
		return this;
	}

    @Override
    public LoadOptions setEnableDiskCache(boolean enableDiskCache) {
        super.setEnableDiskCache(enableDiskCache);
        return this;
    }

    @Override
    public LoadOptions setDiskCachePeriodOfValidity(long diskCachePeriodOfValidity) {
        super.setDiskCachePeriodOfValidity(diskCachePeriodOfValidity);
        return this;
    }

    @Override
    public LoadOptions setMaxRetryCount(int maxRetryCount) {
        super.setMaxRetryCount(maxRetryCount);
        return this;
    }

    @Override
	public LoadOptions setEnableProgressCallback(boolean enableProgressCallback) {
    	super.setEnableProgressCallback(enableProgressCallback);
		return this; 
	}

	@Override
	public LoadOptions copy(){
        return new LoadOptions()
            .setMaxRetryCount(getMaxRetryCount())
            .setDiskCachePeriodOfValidity(getDiskCachePeriodOfValidity())
            .setEnableDiskCache(isEnableDiskCache())
            .setEnableProgressCallback(isEnableProgressCallback())

            .setScaleType(scaleType)
            .setDecodeMaxSize(decodeMaxSize != null ? decodeMaxSize.copy() : null)
            .setProcessSize(getProcessSize() != null?getProcessSize().copy():null)
            .setProcessor(processor != null ? processor.copy() : null);
	}
}
