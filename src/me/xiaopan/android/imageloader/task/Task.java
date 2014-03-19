package me.xiaopan.android.imageloader.task;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public abstract class Task extends FutureTask<Object> {
	private TaskRequest taskRequest;
	
	public Task(TaskRequest taskRequest, Callable<Object> callable) {
		super(callable);
		this.taskRequest = taskRequest;
	}

	public TaskRequest getTaskRequest() {
		return taskRequest;
	}

	public void setTaskRequest(TaskRequest taskRequest) {
		this.taskRequest = taskRequest;
	}
}
