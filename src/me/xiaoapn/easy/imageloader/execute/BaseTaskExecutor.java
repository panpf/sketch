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
import android.util.Log;

/**
 * 基本的任务执行器
 */
public class BaseTaskExecutor implements TaskExecutor {
	private Executor taskDistributor;	//任务调度器
	private Executor netTaskExecutor;	//网络任务执行器
	private Executor localTaskExecutor;	//本地任务执行器
	private Map<String, ReentrantLock> uriLocks;	//uri锁池
	
	public BaseTaskExecutor(Executor netTaskExecutor, Executor localTaskExecutor){
		this.uriLocks = new WeakHashMap<String, ReentrantLock>();
		this.taskDistributor = Executors.newCachedThreadPool();
		this.netTaskExecutor = netTaskExecutor;
		this.localTaskExecutor = localTaskExecutor;
	}
	
	public BaseTaskExecutor(Executor netTaskExecutor){
		this(netTaskExecutor, new ThreadPoolExecutor(1, 1, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(20), new ThreadPoolExecutor.DiscardOldestPolicy()));
	}
	
	public BaseTaskExecutor(){
		this(new ThreadPoolExecutor(5, 10, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(20), new ThreadPoolExecutor.DiscardOldestPolicy()));
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
	public ReentrantLock getLockByRequestId(String requestId) {
		Log.d("", "URI锁ID="+requestId);
		ReentrantLock lock = uriLocks.get(requestId);
		if (lock == null) {
			lock = new ReentrantLock();
			uriLocks.put(requestId, lock);
		}
		return lock;
	}
}
