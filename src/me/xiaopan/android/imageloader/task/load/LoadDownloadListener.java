package me.xiaopan.android.imageloader.task.load;

import java.io.File;

import me.xiaopan.android.imageloader.task.download.DownloadRequest.DownloadListener;

public class LoadDownloadListener implements DownloadListener {

	@Override
	public void onStart() {

	}

	@Override
	public void onUpdateProgress(long totalLength, long completedLength) {

	}

	@Override
	public void onComplete(File cacheFile) {

	}

	@Override
	public void onComplete(byte[] data) {

	}

	@Override
	public void onFailed() {

	}
}
