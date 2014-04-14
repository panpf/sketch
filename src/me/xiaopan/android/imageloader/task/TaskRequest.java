/*
 * Copyright (C) 2013 Peng fei Pan <sky@xiaopan.me>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.xiaopan.android.imageloader.task;

public abstract class TaskRequest {
	private boolean canceled;	//是否已经取消
	private Task task;	//执行当前请求的任务，由于一个请求可能辗转被好几个任务处理

	void setTask(Task task) {
		this.task = task;
	}

	public boolean isCanceled() {
		return canceled;
	}

    public boolean cancel(boolean mayInterruptIfRunning) {
    	canceled = true;
        return task == null || task.cancel(mayInterruptIfRunning);
    }
}
