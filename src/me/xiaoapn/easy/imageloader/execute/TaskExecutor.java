package me.xiaoapn.easy.imageloader.execute;

import java.util.concurrent.locks.ReentrantLock;

import me.xiaoapn.easy.imageloader.Configuration;
import me.xiaoapn.easy.imageloader.task.BitmapLoadTask;

/**
 * 任务执行器
 */
public interface TaskExecutor {
	public void execute(BitmapLoadTask bitmapLoadTask, Configuration configuration);
	public ReentrantLock getLockById(String id);
}