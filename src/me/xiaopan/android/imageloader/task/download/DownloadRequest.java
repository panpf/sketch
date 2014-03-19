package me.xiaopan.android.imageloader.task.download;

import java.io.File;

/**
 * 下载请求
 */
public class DownloadRequest {
	private String url;
	private File saveFile;
	private DownloadListener downloadListener;
	
	private DownloadRequest(String url, File saveFile, DownloadListener downloadListener) {
		this.url = url;
		this.saveFile = saveFile;
		this.downloadListener = downloadListener;
	}

	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public File getSaveFile() {
		return saveFile;
	}
	
	public void setSaveFile(File saveFile) {
		this.saveFile = saveFile;
	}
	
	public DownloadListener getDownloadListener() {
		return downloadListener;
	}
	
	public void setDownloadListener(DownloadListener downloadListener) {
		this.downloadListener = downloadListener;
	}
}