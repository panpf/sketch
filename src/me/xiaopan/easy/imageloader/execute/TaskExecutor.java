package me.xiaopan.easy.imageloader.execute;

import java.util.concurrent.locks.ReentrantLock;

import me.xiaopan.easy.imageloader.Configuration;
import me.xiaopan.easy.imageloader.task.BitmapLoadTask;

/**
 * 任务执行器
 */
public interface TaskExecutor {
	public void execute(BitmapLoadTask bitmapLoadTask, Configuration configuration);
	public ReentrantLock getLockByRequestId(String requestId);
}