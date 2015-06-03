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

package me.xiaopan.sketch.execute;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 默认的请求执行器
 */
public class DefaultRequestExecutor implements RequestExecutor {
    private static final String NAME = "DefaultRequestExecutor";
	private Executor taskDispatchExecutor;	//任务调度执行器
	private Executor netTaskExecutor;	//网络任务执行器
	private Executor localTaskExecutor;	//本地任务执行器
	
	private DefaultRequestExecutor(Builder builder){
		this.taskDispatchExecutor = builder.taskDispatchExecutor;
        this.netTaskExecutor = builder.netTaskExecutor;
        this.localTaskExecutor = builder.localTaskExecutor;
	}
	
    @Override
    public Executor getRequestDispatchExecutor() {
        return taskDispatchExecutor;
    }

    @Override
    public Executor getLocalRequestExecutor() {
        return localTaskExecutor;
    }

    @Override
    public Executor getNetRequestExecutor() {
        return netTaskExecutor;
    }

    @Override
    public String getIdentifier() {
        return NAME;
    }

    @Override
    public StringBuilder appendIdentifier(StringBuilder builder) {
        return builder.append(NAME);
    }

    public static class Builder {
        private Executor taskDispatchExecutor;	//任务调度执行器
        private Executor netTaskExecutor;	//网络任务执行器
        private Executor localTaskExecutor;	//本地任务执行器

        public Builder taskDispatchExecutor(BlockingQueue<Runnable> workQueue){
            if(workQueue != null){
                workQueue = new LinkedBlockingQueue<Runnable>(200);
            }
            this.taskDispatchExecutor = new ThreadPoolExecutor(1, 1, 60, TimeUnit.SECONDS, workQueue, new ThreadPoolExecutor.DiscardOldestPolicy());
            return this;
        }

        public Builder netTaskExecutor(int maxPoolSize, BlockingQueue<Runnable> workQueue){
            if(maxPoolSize <= 0){
                maxPoolSize = 5;
            }
            if(workQueue == null){
                workQueue = new LinkedBlockingQueue<Runnable>(200);
            }
            this.netTaskExecutor = new ThreadPoolExecutor(maxPoolSize, maxPoolSize, 60, TimeUnit.SECONDS, workQueue, new ThreadPoolExecutor.DiscardOldestPolicy());
            return this;
        }

        public Builder localTaskExecutor(BlockingQueue<Runnable> workQueue){
            if(workQueue == null){
                workQueue = new LinkedBlockingQueue<Runnable>(200);
            }
            this.localTaskExecutor = new ThreadPoolExecutor(1, 1, 60, TimeUnit.SECONDS, workQueue, new ThreadPoolExecutor.DiscardOldestPolicy());
            return this;
        }

        public DefaultRequestExecutor build(){
            if(taskDispatchExecutor == null){
                taskDispatchExecutor = new ThreadPoolExecutor(1, 1, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(200), new ThreadPoolExecutor.DiscardOldestPolicy());
            }
            if(netTaskExecutor == null){
                netTaskExecutor = new ThreadPoolExecutor(5, 5, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(200), new ThreadPoolExecutor.DiscardOldestPolicy());
            }
            if(localTaskExecutor == null){
                localTaskExecutor = new ThreadPoolExecutor(1, 1, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(200), new ThreadPoolExecutor.DiscardOldestPolicy());
            }
            return new DefaultRequestExecutor(this);
        }
    }
}
