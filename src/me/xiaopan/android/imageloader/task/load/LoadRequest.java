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

import me.xiaopan.android.imageloader.task.TaskRequest;
import me.xiaopan.android.imageloader.util.ImageSize;
import android.graphics.Bitmap;

/**
 * 加载请求
 */
public class LoadRequest extends TaskRequest{
	private ImageSize targetSize;	//目标尺寸
	private LoadListener loadListener;	//监听器
	private LoadOptions loadOptions;	//显示选项
	
	public LoadRequest(String id, String uri) {
		setId(id);
		setUri(uri);
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
	public void setLoadListener(LoadListener loadListener) {
		this.loadListener = loadListener;
	}
	
	/**
	 * 获取加载选项
	 * @return
	 */
	public LoadOptions getDisplayOptions() {
		return loadOptions;
	}

	/**
	 * 设置加载选项
	 * @param loadOptions
	 */
	public void setLoadOptions(LoadOptions loadOptions) {
		this.loadOptions = loadOptions;
	}

	@Override
	public boolean isEnableDiskCache() {
		return loadOptions != null?loadOptions.isEnableDiskCache():false;
	}

	@Override
	public int getDiskCachePeriodOfValidity() {
		return loadOptions != null?loadOptions.getDiskCachePeriodOfValidity():0;
	}
	
	/**
	 * 加载监听器
	 */
	public interface LoadListener {
		public void onStart();
		public void onFailure();
		public void onComplete(Bitmap bitmap);
		public void onCancel();
	}
}
