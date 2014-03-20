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
import me.xiaopan.android.imageloader.task.TaskOptions;
import me.xiaopan.android.imageloader.util.ImageSize;
import android.content.Context;

/**
 * 显示选项
 */
public class LoadOptions extends TaskOptions{
	private Context context;	//上下文
	private ImageSize maxImageSize;	//最大图片尺寸
	private BitmapProcessor bitmapProcessor;	//位图处理器
	
	public LoadOptions(Context context) {
		this.context = context;
		setMaxImageSize(new ImageSize(context.getResources().getDisplayMetrics().widthPixels, context.getResources().getDisplayMetrics().heightPixels));
	}

	/**
	 * 获取最大尺寸
	 * @return
	 */
	public ImageSize getMaxImageSize() {
		return maxImageSize;
	}

	/**
	 * 设置最大尺寸
	 * @param maxImageSize
	 */
	public LoadOptions setMaxImageSize(ImageSize maxImageSize) {
		this.maxImageSize = maxImageSize;
		return this;
	}
	
	/**
	 * 获取位图处理器
	 * @return
	 */
	public BitmapProcessor getBitmapProcessor() {
		return bitmapProcessor;
	}

	/**
	 * 设置位图处理器
	 * @param bitmapProcessor
	 */
	public LoadOptions setBitmapProcessor(BitmapProcessor bitmapProcessor) {
		this.bitmapProcessor = bitmapProcessor;
		return this;
	}

	/**
	 * 将当前的DisplayOptions拷贝一份
	 * @return
	 */
	public LoadOptions copy(){
		LoadOptions loadOptions = new LoadOptions(context);
		
		loadOptions.setMaxRetryCount(getMaxRetryCount())
		.setDiskCachePeriodOfValidity(getDiskCachePeriodOfValidity())
		.setEnableDiskCache(isEnableDiskCache());
		
		loadOptions.setBitmapProcessor(bitmapProcessor != null?bitmapProcessor.copy():null)
		.setMaxImageSize(maxImageSize != null?maxImageSize.copy():null);
		return loadOptions;
	}
}
