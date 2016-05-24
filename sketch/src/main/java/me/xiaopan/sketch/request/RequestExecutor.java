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

import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import me.xiaopan.sketch.Identifier;

/**
 * 请求执行器
 */
public class RequestExecutor implements Identifier {
    private static final String NAME = "RequestExecutor";
    private ExecutorService netTaskExecutor;    //网络任务执行器
    private ExecutorService localTaskExecutor;    //本地任务执行器
    private Handler dispatchHandler;
    private DispatchThread dispatchThread;
    private boolean shutdown;

    public void submitDispatch(Runnable runnable) {
        if (shutdown) {
            return;
        }

        // 之所有这里采用了懒加载的方式是为了兼容多进程，避免资源浪费
        if (dispatchHandler == null || dispatchThread == null) {
            synchronized (RequestExecutor.this) {
                if (dispatchHandler == null) {
                    dispatchThread = new DispatchThread("DispatchThread");
                    dispatchThread.start();
                    dispatchHandler = new Handler(dispatchThread.getLooper(), new DispatchCallback());
                }
            }
        }
        dispatchHandler.obtainMessage(0, runnable).sendToTarget();
    }

    public void submitLoad(Runnable runnable) {
        if (shutdown) {
            return;
        }

        // 之所有这里采用了懒加载的方式是为了兼容多进程，避免资源浪费
        if (localTaskExecutor == null) {
            synchronized (RequestExecutor.this) {
                if (localTaskExecutor == null) {
                    localTaskExecutor = new ThreadPoolExecutor(3, 3, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(200), new ThreadPoolExecutor.DiscardOldestPolicy());
                }
            }
        }
        localTaskExecutor.execute(runnable);
    }

    public void submitDownload(Runnable runnable) {
        if (shutdown) {
            return;
        }

        // 之所有这里采用了懒加载的方式是为了兼容多进程，避免资源浪费
        if (netTaskExecutor == null) {
            synchronized (RequestExecutor.this) {
                if (netTaskExecutor == null) {
                    netTaskExecutor = new ThreadPoolExecutor(3, 3, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(200), new ThreadPoolExecutor.DiscardOldestPolicy());
                }
            }
        }
        netTaskExecutor.execute(runnable);
    }

    @SuppressWarnings("unused")
    public void setLocalTaskExecutor(ExecutorService localTaskExecutor) {
        if (shutdown) {
            return;
        }

        this.localTaskExecutor = localTaskExecutor;
    }

    @SuppressWarnings("unused")
    public void setNetTaskExecutor(ExecutorService netTaskExecutor) {
        if (shutdown) {
            return;
        }

        this.netTaskExecutor = netTaskExecutor;
    }

    @Override
    public String getIdentifier() {
        return NAME;
    }

    @Override
    public StringBuilder appendIdentifier(StringBuilder builder) {
        return builder.append(NAME).append("(").append(shutdown ? "shutdown" : "running").append(")");
    }

    public void shutdown() {
        if (dispatchHandler != null) {
            dispatchHandler = null;
        }

        if (dispatchThread != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                dispatchThread.quitSafely();
            } else {
                dispatchThread.quit();
            }
            dispatchThread = null;
        }

        if (netTaskExecutor != null) {
            netTaskExecutor.shutdown();
            netTaskExecutor = null;
        }

        if (localTaskExecutor != null) {
            localTaskExecutor.shutdown();
            localTaskExecutor = null;
        }

        shutdown = true;
    }

    private static final class DispatchThread extends HandlerThread {

        public DispatchThread(String name) {
            // 调低线程优先级这对于流畅度很重要
            super(name, android.os.Process.THREAD_PRIORITY_BACKGROUND);
        }
    }

    private static final class DispatchCallback implements Handler.Callback {
        @Override
        public boolean handleMessage(Message msg) {
            ((Runnable) msg.obj).run();
            return true;
        }
    }
}