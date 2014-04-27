package me.xiaopan.android.imageloader.download;

import me.xiaopan.android.imageloader.task.download.DownloadRequest;

/**
 * 下载器
 */
public interface Downloader {
	public Object down(DownloadRequest downloadRequest);
	public boolean isDownloadingByCacheFilePath(String cacheFilePath);
}
