package me.xiaoapn.easy.imageloader.execute;

import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.graphics.Bitmap;

/**
 * 基本的任务执行器
 */
public class BaseTaskExecutor implements TaskExecutor {
	private ThreadPoolExecutor threadPoolExecutor;
	
	public BaseTaskExecutor(int corePoolSize, int maximumPoolSize, int workQueueSize){
		threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(workQueueSize), new ThreadPoolExecutor.DiscardOldestPolicy());
	}
	
	public BaseTaskExecutor(){
		this(5, 20, 10);
	}
	
	@Override
	public void execute(FutureTask<Bitmap> futureTask) {
		threadPoolExecutor.execute(futureTask);
	}
}
