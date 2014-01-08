package me.xiaoapn.easy.imageloader.execute;

import java.util.concurrent.FutureTask;
import java.util.concurrent.locks.ReentrantLock;

import android.graphics.drawable.BitmapDrawable;

/**
 * 任务执行器
 */
public interface TaskExecutor {
	public void execute(FutureTask<BitmapDrawable> futureTask);
	public ReentrantLock getLockById(String id);
}