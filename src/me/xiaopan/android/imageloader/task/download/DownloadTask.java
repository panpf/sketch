package me.xiaopan.android.imageloader.task.download;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * 下载任务
 */
public class DownloadTask extends FutureTask<File>{
	public DownloadTask(Callable<File> callable) {
		super(new DownloadCallable());
	}
	
	private static class DownloadCallable implements Callable<File>{
		@Override
		public File call() throws Exception {
			return null;
		}
	}
}
