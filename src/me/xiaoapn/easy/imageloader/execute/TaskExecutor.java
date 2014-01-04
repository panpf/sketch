package me.xiaoapn.easy.imageloader.execute;

import java.util.concurrent.FutureTask;

import android.graphics.Bitmap;

/**
 * 任务执行器
 */
public interface TaskExecutor {
	public void execute(FutureTask<Bitmap> futureTask);
}