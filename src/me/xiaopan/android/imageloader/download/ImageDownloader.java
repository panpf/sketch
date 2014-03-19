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

package me.xiaopan.android.imageloader.download;

import java.io.File;

import me.xiaopan.android.imageloader.Configuration;
import me.xiaopan.android.imageloader.task.display.DisplayRequest;
import me.xiaopan.android.imageloader.task.download.DownloadListener;

/**
 * 图片下载器
 */
public interface ImageDownloader {
	/**
	 * 执行
	 * @param displayRequest
	 * @param cacheFile
	 * @param configuration
	 * @param onCompleteListener
	 */
	public void execute(DisplayRequest displayRequest, File cacheFile, Configuration configuration, DownloadListener onCompleteListener);
}