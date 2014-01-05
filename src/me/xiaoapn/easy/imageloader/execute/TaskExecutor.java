package me.xiaoapn.easy.imageloader.execute;

import java.util.concurrent.FutureTask;

import android.graphics.drawable.BitmapDrawable;

/**
 * 任务执行器
 */
public interface TaskExecutor {
	public void execute(FutureTask<BitmapDrawable> futureTask);
}