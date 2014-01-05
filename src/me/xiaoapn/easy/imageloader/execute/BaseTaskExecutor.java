package me.xiaoapn.easy.imageloader.execute;

import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import android.graphics.drawable.BitmapDrawable;

/**
 * 基本的任务执行器
 */
public class BaseTaskExecutor implements TaskExecutor {
	private ThreadPoolExecutor threadPoolExecutor;
	
	public BaseTaskExecutor(int corePoolSize, int maximumPoolSize, int workQueueSize){
		ThreadFactory threadFactory = new ThreadFactory() {
			private final AtomicInteger mCount = new AtomicInteger(1);
			@Override
			public Thread newThread(Runnable r) {
				Thread thread = new Thread(r, "AsyncTask #" + mCount.getAndIncrement());
				thread.setPriority(10);
				return thread;
			}
		};
		threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(workQueueSize), threadFactory, new ThreadPoolExecutor.DiscardOldestPolicy());
	}
	
	public BaseTaskExecutor(){
		this(5, 10, 20);
	}
	
	@Override
	public void execute(FutureTask<BitmapDrawable> futureTask) {
		threadPoolExecutor.execute(futureTask);
	}
}
