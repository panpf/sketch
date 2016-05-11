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

package me.xiaopan.sketch.request;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import me.xiaopan.sketch.Identifier;

/**
 * 请求执行器
 */
public class RequestExecutor implements Identifier {
    private static final String NAME = "RequestExecutor";
    private Executor netTaskExecutor;    //网络任务执行器
    private Executor localTaskExecutor;    //本地任务执行器
    private Handler dispatchHandler;

    public void submitDispatch(Runnable runnable) {
        if(dispatchHandler == null){
            synchronized (RequestExecutor.this){
                if(dispatchHandler == null){
                    DispatchThread dispatchThread = new DispatchThread("DispatchThread");
                    dispatchThread.start();
                    dispatchHandler = new Handler(dispatchThread.getLooper(), new DispatchCallback());
                }
            }
        }
        dispatchHandler.obtainMessage(0, runnable).sendToTarget();
    }

    public void submitLoad(Runnable runnable) {
        if (localTaskExecutor == null) {
            synchronized (RequestExecutor.this){
                if (localTaskExecutor == null) {
                    localTaskExecutor = new ThreadPoolExecutor(1, 1, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(200), new ThreadPoolExecutor.DiscardOldestPolicy());
                }
            }
        }
        localTaskExecutor.execute(runnable);
    }

    public void submitDownload(Runnable runnable) {
        if (netTaskExecutor == null) {
            synchronized (RequestExecutor.this){
                if (netTaskExecutor == null) {
                    netTaskExecutor = new ThreadPoolExecutor(5, 5, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(200), new ThreadPoolExecutor.DiscardOldestPolicy());
                }
            }
        }
        netTaskExecutor.execute(runnable);
    }

    @SuppressWarnings("unused")
    public void setLocalTaskExecutor(Executor localTaskExecutor) {
        this.localTaskExecutor = localTaskExecutor;
    }

    @SuppressWarnings("unused")
    public void setNetTaskExecutor(Executor netTaskExecutor) {
        this.netTaskExecutor = netTaskExecutor;
    }

    @Override
    public String getIdentifier() {
        return NAME;
    }

    @Override
    public StringBuilder appendIdentifier(StringBuilder builder) {
        return builder.append(NAME);
    }

    private static final class DispatchThread extends HandlerThread {

        public DispatchThread(String name) {
            // 调低线程优先级这对于流畅度很重要
            super(name, android.os.Process.THREAD_PRIORITY_BACKGROUND);
        }
    }

    private static final class DispatchCallback implements Handler.Callback{
        @Override
        public boolean handleMessage(Message msg) {
            ((Runnable) msg.obj).run();
            return true;
        }
    }
}