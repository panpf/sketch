package me.xiaoapn.easy.imageloader.execute;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 基本的任务执行器
 */
public class BaseTaskExecutor implements TaskExecutor {
	private ThreadPoolExecutor threadPoolExecutor;
	
	public BaseTaskExecutor(int corePoolSize, int maximumPoolSize, int workQueueSize){
		threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(workQueueSize), new ThreadPoolExecutor.DiscardOldestPolicy());
	}
	
	public BaseTaskExecutor(){
		this(10, 20, 20);
	}
	
	@Override
	public void execute(Runnable task) {
		threadPoolExecutor.execute(task);
	}
}
