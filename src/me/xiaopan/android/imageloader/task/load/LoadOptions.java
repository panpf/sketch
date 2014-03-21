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

import android.widget.ImageView.ScaleType;
import me.xiaopan.android.imageloader.process.BitmapProcessor;
import me.xiaopan.android.imageloader.task.download.DownloadOptions;
import me.xiaopan.android.imageloader.util.ImageSize;

/**
 * 显示选项
 */
public class LoadOptions extends DownloadOptions{
    private ScaleType scaleType;
    private ImageSize maxImageSize;	//最大图片尺寸
	private BitmapProcessor bitmapProcessor;	//位图处理器
	
    public ScaleType getScaleType() {
        return scaleType;
    }

    public void setScaleType(ScaleType scaleType) {
        this.scaleType = scaleType;
    }

    public ImageSize getMaxImageSize() {
        return maxImageSize;
    }

    public void setMaxImageSize(ImageSize maxImageSize) {
        this.maxImageSize = maxImageSize;
    }
	
	public BitmapProcessor getBitmapProcessor() {
		return bitmapProcessor;
	}

	public void setBitmapProcessor(BitmapProcessor bitmapProcessor) {
		this.bitmapProcessor = bitmapProcessor;
	}

	/**
	 * 将当前的DisplayOptions拷贝一份
	 * @return
	 */
	public LoadOptions copy(){
		LoadOptions loadOptions = new LoadOptions();
		loadOptions.setMaxRetryCount(getMaxRetryCount());
        loadOptions.setDiskCachePeriodOfValidity(getDiskCachePeriodOfValidity());
        loadOptions.setEnableDiskCache(isEnableDiskCache());
		loadOptions.setBitmapProcessor(bitmapProcessor != null ? bitmapProcessor.copy() : null);
        loadOptions.setScaleType(scaleType);
		return loadOptions;
	}
}
