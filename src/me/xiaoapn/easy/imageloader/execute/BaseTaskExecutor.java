package me.xiaoapn.easy.imageloader.execute;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import android.graphics.drawable.BitmapDrawable;

/**
 * 基本的任务执行器
 */
public class BaseTaskExecutor implements TaskExecutor {
	private ThreadPoolExecutor threadPoolExecutor;
	private Map<String, ReentrantLock> uriLocks;
	
	public BaseTaskExecutor(int corePoolSize, int maximumPoolSize, int workQueueSize){
		this.threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(workQueueSize), new ThreadPoolExecutor.DiscardOldestPolicy());
		this.uriLocks = new WeakHashMap<String, ReentrantLock>();
	}
	
	public BaseTaskExecutor(){
		this(5, 10, 20);
	}
	
	@Override
	public void execute(FutureTask<BitmapDrawable> futureTask) {
		threadPoolExecutor.execute(futureTask);
	}

	@Override
	public ReentrantLock getLockById(String id) {
		ReentrantLock lock = uriLocks.get(id);
		if (lock == null) {
			lock = new ReentrantLock();
			uriLocks.put(id, lock);
		}
		return lock;
	}
}
