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
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import me.xiaopan.sketch.Identifier;

/**
 * 请求执行器
 */
public class RequestExecutor implements Identifier {
    public static final int DEFAULT_LOCAL_THREAD_POOL_SIZE = 3;
    public static final int DEFAULT_NET_THREAD_POOL_SIZE = 3;

    protected String logName = "RequestExecutor";

    private ExecutorService netTaskExecutor;    //网络任务执行器
    private ExecutorService localTaskExecutor;    //本地任务执行器
    private Handler dispatchHandler;
    private DispatchThread dispatchThread;
    private boolean shutdown;
    private int localThreadPoolSize;
    private int netThreadPoolSize;

    private Map<String, DisplayRequest> freeRideRequestMap;
    private final Object freeRideRequestMapLock = new Object();

    public RequestExecutor(int localThreadPoolSize, int netThreadPoolSize) {
        this.localThreadPoolSize = localThreadPoolSize;
        this.netThreadPoolSize = netThreadPoolSize;
    }

    public RequestExecutor() {
        this(DEFAULT_LOCAL_THREAD_POOL_SIZE, DEFAULT_NET_THREAD_POOL_SIZE);
    }

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

        // 尝试搭个顺风车
        if (runnable instanceof DisplayRequest) {
            DisplayRequest displayRequest = (DisplayRequest) runnable;
            if (displayRequest.canFreeRide()) {
                synchronized (freeRideRequestMapLock) {
                    DisplayRequest freeRideRequest = null;
                    if (freeRideRequestMap != null) {
                        freeRideRequest = freeRideRequestMap.get(displayRequest.getId());
                    }
                    if (freeRideRequest != null) {
                        freeRideRequest.addFreeRideRequest(displayRequest);
                        return;
                    }
                }
            }
        }

        // 之所有这里采用了懒加载的方式是为了兼容多进程，避免资源浪费
        if (localTaskExecutor == null) {
            synchronized (RequestExecutor.this) {
                if (localTaskExecutor == null) {
                    localTaskExecutor = new ThreadPoolExecutor(
                            localThreadPoolSize,
                            localThreadPoolSize,
                            60, TimeUnit.SECONDS,
                            new LinkedBlockingQueue<Runnable>(200),
                            new DefaultThreadFactory("LoadThread"),
                            new ThreadPoolExecutor.DiscardOldestPolicy());
                }
            }
        }
        localTaskExecutor.execute(runnable);

        // 现在把自己作为顺风车主
        if (runnable instanceof DisplayRequest) {
            DisplayRequest displayRequest = (DisplayRequest) runnable;
            if (displayRequest.canFreeRide()) {
                registerFreeRide(displayRequest);
            }
        }
    }

    public void submitDownload(Runnable runnable) {
        if (shutdown) {
            return;
        }

        // 之所有这里采用了懒加载的方式是为了兼容多进程，避免资源浪费
        if (netTaskExecutor == null) {
            synchronized (RequestExecutor.this) {
                if (netTaskExecutor == null) {
                    netTaskExecutor = new ThreadPoolExecutor(
                            netThreadPoolSize,
                            netThreadPoolSize,
                            60, TimeUnit.SECONDS,
                            new LinkedBlockingQueue<Runnable>(200),
                            new DefaultThreadFactory("DownloadThread"),
                            new ThreadPoolExecutor.DiscardOldestPolicy());
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

    private void installFreeRideRequestMap() {
        if (freeRideRequestMap == null) {
            synchronized (this) {
                if (freeRideRequestMap == null) {
                    freeRideRequestMap = new HashMap<String, DisplayRequest>();
                }
            }
        }
    }

    public synchronized void registerFreeRide(DisplayRequest request) {
        if (request.canFreeRide()) {
            synchronized (freeRideRequestMapLock) {
                installFreeRideRequestMap();
                freeRideRequestMap.put(request.getId(), request);
            }
        }
    }

    public synchronized void unregisterFreeRide(DisplayRequest request) {
        if (request.canFreeRide()) {
            synchronized (freeRideRequestMapLock) {
                installFreeRideRequestMap();
                freeRideRequestMap.remove(request.getId());
            }
        }
    }

    @Override
    public String getIdentifier() {
        return appendIdentifier(null, new StringBuilder()).toString();
    }

    @Override
    public StringBuilder appendIdentifier(String join, StringBuilder builder) {
        if (!TextUtils.isEmpty(join)) {
            builder.append(join);
        }
        if (TextUtils.isEmpty(join)) {
            builder.append(join);
        }
        return builder.append(logName).append("(").append(shutdown ? "shutdown" : "running)");
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

    private static class DefaultThreadFactory implements ThreadFactory {
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        private DefaultThreadFactory(String namePrefix) {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            this.namePrefix = namePrefix;
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }
}