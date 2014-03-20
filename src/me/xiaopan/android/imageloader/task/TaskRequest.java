package me.xiaopan.android.imageloader.task;

import me.xiaopan.android.imageloader.Configuration;

public abstract class TaskRequest {
	private boolean canceled;	//是否已经取消
	private Task task;	//执行当前请求的任务，由于一个请求可能辗转被好几个任务处理
	private String name;	//名称
	private Configuration configuration;	//配置

	/**
	 * 获取名称，用于在输出log时区分不同的请求
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * 设置名称，用于在输出log时区分不同的请求
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取配置
	 * @return
	 */
	public Configuration getConfiguration() {
		return configuration;
	}

	/**
	 * 设置配置
	 * @param configuration
	 */
	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	/**
	 * 设置当前任务
	 * @param task
	 */
	public void setTask(Task task) {
		this.task = task;
	}

	/**
	 * 是否已经取消
	 * @return
	 */
	public boolean isCanceled() {
		return canceled;
	}

    /**
     * 取消请求
     * @param mayInterruptIfRunning
     * @return
     */
    public boolean cancel(boolean mayInterruptIfRunning) {
    	canceled = true;
    	if(task != null){
    		return task.cancel(mayInterruptIfRunning);
    	}else{
    		return true;
    	}
    }
}
