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

import java.io.File;

import me.xiaopan.android.imageloader.Configuration;
import me.xiaopan.android.imageloader.task.TaskRequest;
import me.xiaopan.android.imageloader.util.Scheme;

/**
 * 下载请求
 */
public class DownloadRequest extends TaskRequest{
	private String imageUri;	//图片地址
	private File cacheFile;	//缓存文件
	private String name;	//名称，用于在输出LOG的时候区分不同的请求
	private Scheme scheme;	// Uri协议格式
	private Configuration configuration;	//配置
	private DownloadListener downloadListener;
	private DownloadOptions downloadOptions;
	
	public DownloadRequest(String imageUri, Configuration configuration){
		this.imageUri = imageUri;
		this.configuration = configuration;
	}

	/**
	 * 获取URI
	 */
    public String getImageUri() {
		return imageUri;
	}

    /**
     * 获取请求名称
     */
	public String getName() {
		return name;
	}

	/**
	 * 获取Uri协议格式
	 * @return
	 */
	public Scheme getScheme() {
		return scheme;
	}

	/**
	 * 设置Uri协议格式
	 * @param scheme
	 */
	public void setScheme(Scheme scheme) {
		this.scheme = scheme;
	}

	/**
	 * 设置请求名称
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取ImageLoader配置
	 */
	public Configuration getConfiguration() {
		return configuration;
	}

	/**
	 * 获取缓存文件
	 */
	public File getCacheFile() {
		return cacheFile;
	}
	
	/**
	 * 设置缓存文件
	 */
	public void setCacheFile(File cacheFile) {
		this.cacheFile = cacheFile;
	}

	/**
     * 获取下载选项
     */
	public DownloadOptions getDownloadOptions() {
		return downloadOptions;
	}

    /**
     * 设置下载选项
     */
	public void setDownloadOptions(DownloadOptions downloadOptions) {
		this.downloadOptions = downloadOptions;
	}

    /**
     * 获取下载监听器
     */
	public DownloadListener getDownloadListener() {
		return downloadListener;
	}

    /**
     * 设置下载监听器
     */
	public DownloadRequest setDownloadListener(DownloadListener downloadListener) {
		this.downloadListener = downloadListener;
		return this;
	}
}