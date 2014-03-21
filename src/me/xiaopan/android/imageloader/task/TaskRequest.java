package me.xiaopan.android.imageloader.task;

import java.io.File;

import me.xiaopan.android.imageloader.Configuration;

public abstract class TaskRequest {
	private boolean canceled;	//是否已经取消
	private String uri;	//Uri
	private String name;	//名称，用于在输出LOG的时候区分不同的请求
	private File cacheFile;	//缓存文件
	private Task task;	//执行当前请求的任务，由于一个请求可能辗转被好几个任务处理
	private Configuration configuration;	//配置
	
	public TaskRequest(String uri){
		this.uri = uri;
	}

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getUri() {
		return uri;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	public File getCacheFile() {
		return cacheFile;
	}
	
	public void setCacheFile(File cacheFile) {
		this.cacheFile = cacheFile;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public boolean isCanceled() {
		return canceled;
	}

    public boolean cancel(boolean mayInterruptIfRunning) {
    	canceled = true;
    	if(task != null){
    		return task.cancel(mayInterruptIfRunning);
    	}else{
    		return true;
    	}
    }
    
    public abstract boolean isEnableDiskCache();
    public abstract int getDiskCachePeriodOfValidity();
}
