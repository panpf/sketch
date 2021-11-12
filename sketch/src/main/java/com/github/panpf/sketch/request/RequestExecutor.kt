/*
 * Copyright (C) 2019 panpf <panpfpanpf@outlook.com>
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
package com.github.panpf.sketch.request

import android.os.*
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * 请求执行器
 */
class RequestExecutor @JvmOverloads constructor(
    private val localThreadPoolSize: Int = DEFAULT_LOCAL_THREAD_POOL_SIZE,
    private val netThreadPoolSize: Int = DEFAULT_NET_THREAD_POOL_SIZE
) {

    companion object {
        const val DEFAULT_LOCAL_THREAD_POOL_SIZE = 3
        const val DEFAULT_NET_THREAD_POOL_SIZE = 3
        private const val KEY = "RequestExecutor"
    }

    private var netTaskExecutor: ExecutorService? = null //网络任务执行器
    private var localTaskExecutor: ExecutorService? = null //本地任务执行器
    private var dispatchHandler: Handler? = null
    private var dispatchThread: DispatchThread? = null
    var isShutdown = false
        private set

    fun submitDispatch(runnable: Runnable) {
        if (isShutdown) {
            return
        }

        // 之所有这里采用了懒加载的方式是为了兼容多进程，避免资源浪费
        if (dispatchHandler == null || dispatchThread == null) {
            synchronized(this@RequestExecutor) {
                if (dispatchHandler == null || dispatchThread == null) {
                    dispatchThread = DispatchThread("DispatchThread")
                    dispatchThread!!.start()
                    dispatchHandler = Handler(dispatchThread!!.looper, DispatchCallback())
                }
            }
        }
        dispatchHandler!!.obtainMessage(0, runnable).sendToTarget()
    }

    fun submitLoad(runnable: Runnable) {
        if (isShutdown) {
            return
        }

        // 之所有这里采用了懒加载的方式是为了兼容多进程，避免资源浪费
        if (localTaskExecutor == null) {
            synchronized(this@RequestExecutor) {
                if (localTaskExecutor == null) {
                    localTaskExecutor = ThreadPoolExecutor(
                        localThreadPoolSize,
                        localThreadPoolSize,
                        60, TimeUnit.SECONDS,
                        LinkedBlockingQueue(200),
                        DefaultThreadFactory("LoadThread"),
                        ThreadPoolExecutor.DiscardOldestPolicy()
                    )
                }
            }
        }
        localTaskExecutor!!.execute(runnable)
    }

    fun submitDownload(runnable: Runnable) {
        if (isShutdown) {
            return
        }

        // 之所有这里采用了懒加载的方式是为了兼容多进程，避免资源浪费
        if (netTaskExecutor == null) {
            synchronized(this@RequestExecutor) {
                if (netTaskExecutor == null) {
                    netTaskExecutor = ThreadPoolExecutor(
                        netThreadPoolSize,
                        netThreadPoolSize,
                        60, TimeUnit.SECONDS,
                        LinkedBlockingQueue(200),
                        DefaultThreadFactory("DownloadThread"),
                        ThreadPoolExecutor.DiscardOldestPolicy()
                    )
                }
            }
        }
        netTaskExecutor!!.execute(runnable)
    }

    fun setLocalTaskExecutor(localTaskExecutor: ExecutorService) {
        if (isShutdown) {
            return
        }
        this.localTaskExecutor = localTaskExecutor
    }

    fun setNetTaskExecutor(netTaskExecutor: ExecutorService) {
        if (isShutdown) {
            return
        }
        this.netTaskExecutor = netTaskExecutor
    }

    override fun toString(): String {
        return String.format("%s(%s", KEY, if (isShutdown) "shutdown" else "running)")
    }

    fun shutdown() {
        if (dispatchHandler != null) {
            dispatchHandler = null
        }
        if (dispatchThread != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                dispatchThread!!.quitSafely()
            } else {
                dispatchThread!!.quit()
            }
            dispatchThread = null
        }
        if (netTaskExecutor != null) {
            netTaskExecutor!!.shutdown()
            netTaskExecutor = null
        }
        if (localTaskExecutor != null) {
            localTaskExecutor!!.shutdown()
            localTaskExecutor = null
        }
        isShutdown = true
    }

    private class DispatchThread(name: String?) :
        HandlerThread(name, Process.THREAD_PRIORITY_BACKGROUND)

    private class DispatchCallback : Handler.Callback {
        override fun handleMessage(msg: Message): Boolean {
            (msg.obj as Runnable).run()
            return true
        }
    }

    private class DefaultThreadFactory(namePrefix: String) : ThreadFactory {
        private val group: ThreadGroup
        private val threadNumber = AtomicInteger(1)
        private val namePrefix: String

        init {
            val s = System.getSecurityManager()
            group = if (s != null) s.threadGroup else Thread.currentThread().threadGroup
            this.namePrefix = namePrefix
        }

        override fun newThread(r: Runnable): Thread {
            val t = Thread(
                group, r,
                namePrefix + threadNumber.getAndIncrement(),
                0
            )
            if (t.isDaemon) t.isDaemon = false
            if (t.priority != Thread.NORM_PRIORITY) t.priority = Thread.NORM_PRIORITY
            return t
        }
    }
}