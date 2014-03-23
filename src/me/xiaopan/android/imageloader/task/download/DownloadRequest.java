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

package me.xiaopan.android.imageloader.task.download;

import me.xiaopan.android.imageloader.task.TaskRequest;

/**
 * 下载请求
 */
public class DownloadRequest extends TaskRequest{
	private DownloadOptions downloadOptions;
	private DownloadListener downloadListener;
	
	public DownloadRequest(String uri) {
		super(uri);
	}

	public DownloadOptions getDownloadOptions() {
		return downloadOptions;
	}

	public void setDownloadOptions(DownloadOptions downloadOptions) {
		this.downloadOptions = downloadOptions;
	}

	public DownloadListener getDownloadListener() {
		return downloadListener;
	}
	
	public DownloadRequest setDownloadListener(DownloadListener downloadListener) {
		this.downloadListener = downloadListener;
        return this;
	}

	@Override
	public boolean isEnableDiskCache() {
		return downloadOptions != null?downloadOptions.isEnableDiskCache():false;
	}

	@Override
	public long getDiskCachePeriodOfValidity() {
		return downloadOptions != null?downloadOptions.getDiskCachePeriodOfValidity():0;
	}
}