package me.xiaoapn.easy.imageloader.execute;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import me.xiaoapn.easy.imageloader.Configuration;
import me.xiaoapn.easy.imageloader.task.BitmapLoadTask;

/**
 * 基本的任务执行器
 */
public class BaseTaskExecutor implements TaskExecutor {
	private Executor taskDistributor;	//任务调度器
	private Executor netTaskExecutor;	//网络任务执行器
	private Executor localTaskExecutor;	//本地任务执行器
	private Map<String, ReentrantLock> uriLocks;	//uri锁池
	
	public BaseTaskExecutor(int corePoolSize, int maximumPoolSize, int workQueueSize){
		this.taskDistributor = Executors.newCachedThreadPool();
		this.netTaskExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(workQueueSize), new ThreadPoolExecutor.DiscardOldestPolicy());
		this.localTaskExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(workQueueSize), new ThreadPoolExecutor.DiscardOldestPolicy());
		this.uriLocks = new WeakHashMap<String, ReentrantLock>();
	}
	
	public BaseTaskExecutor(){
		this(1, 1, 20);
	}
	
	@Override
	public void execute(final BitmapLoadTask bitmapLoadTask, final Configuration configuration) {
		taskDistributor.execute(new Runnable() {
			@Override
			public void run() {
				if(bitmapLoadTask.getRequest().isNetworkLoad(configuration)){
					netTaskExecutor.execute(bitmapLoadTask);
				}else{
					localTaskExecutor.execute(bitmapLoadTask);
				}
			}
		});
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
