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

import android.widget.ImageView;
import me.xiaopan.android.imageloader.task.TaskRequest;
import me.xiaopan.android.imageloader.task.display.DisplayLoadListener;
import me.xiaopan.android.imageloader.task.display.DisplayRequest;
import me.xiaopan.android.imageloader.task.download.DownloadRequest;
import me.xiaopan.android.imageloader.util.ImageSize;
import android.graphics.Bitmap;

/**
 * 加载请求
 */
public class LoadRequest extends DownloadRequest{
	private ImageSize targetSize;	//目标尺寸
	private LoadListener loadListener;	//监听器
	private LoadOptions loadOptions;	//显示选项
	
	public LoadRequest(String uri) {
		super(uri);
	}
	
	/**
	 * 获取目标尺寸
	 * @return
	 */
	public ImageSize getTargetSize() {
		return targetSize;
	}

	/**
	 * 设置目标尺寸
	 * @param targetSize
	 */
	public void setTargetSize(ImageSize targetSize) {
		this.targetSize = targetSize;
	}

	/**
	 * 获取加载监听器
	 * @return
	 */
	public LoadListener getLoadListener() {
		return loadListener;
	}

	/**
	 * 设置加载监听器
	 * @param loadListener
	 */
	public LoadRequest setLoadListener(LoadListener loadListener) {
		this.loadListener = loadListener;
        return this;
	}
	
	/**
	 * 获取加载选项
	 * @return
	 */
	public LoadOptions getLoadOptions() {
		return loadOptions;
	}

	/**
	 * 设置加载选项，同时也设置下载选项
	 * @param loadOptions
	 */
	public void setLoadOptions(LoadOptions loadOptions) {
		this.loadOptions = loadOptions;
        setDownloadOptions(loadOptions);
	}

    public ImageView.ScaleType getScaleType() {
        return loadOptions != null?loadOptions.getScaleType(): ImageView.ScaleType.CENTER_CROP;
    }

	/**
	 * 加载监听器
	 */
	public interface LoadListener {
		public void onStart();
		public void onComplete(Bitmap bitmap);
        public void onFailure();
		public void onCancel();
	}
}
