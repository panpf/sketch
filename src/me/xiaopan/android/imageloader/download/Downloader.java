package me.xiaopan.android.imageloader.download;

import me.xiaopan.android.imageloader.task.download.DownloadRequest;

/**
 * 下载器
 */
public interface Downloader {
	/**
	 * 下载
	 */
	public Object download(DownloadRequest downloadRequest);
	
	/**
	 * 判断给定缓存文件地址的文件是否正在下载
	 */
	public boolean isDownloadingByCacheFilePath(String cacheFilePath);
}
