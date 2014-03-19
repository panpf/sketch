package me.xiaopan.android.imageloader.task.download;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * 下载任务
 */
public class DownloadTask<T> extends FutureTask<T>{
	public DownloadTask(Callable<T> callable) {
		super(callable);
	}
	
	
}
