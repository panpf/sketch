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

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.locks.ReentrantLock;

import me.xiaopan.sketch.Sketch;

abstract class BaseRequest implements Runnable {
    private static final Map<String, ReentrantLock> loadLocks = Collections.synchronizedMap(new WeakHashMap<String, ReentrantLock>());
    private static final Map<String, ReentrantLock> downloadLocks = Collections.synchronizedMap(new WeakHashMap<String, ReentrantLock>());

    private Sketch sketch;
    private RequestAttrs attrs;

    private RunStatus runStatus;

    private String logName = "BaseRequest";
    private Status status;
    private FailedCause failedCause;
    private CancelCause cancelCause;

    BaseRequest(Sketch sketch, RequestAttrs attrs) {
        this.sketch = sketch;
        this.attrs = attrs;
    }

    private ReentrantLock getLoadLock(String key) {
        if (key == null) {
            return null;
        }
        ReentrantLock loadLock = loadLocks.get(key);
        if (loadLock == null) {
            loadLock = new ReentrantLock();
            loadLocks.put(key, loadLock);
        }
        return loadLock;
    }

    private ReentrantLock getDownloadLock(String key) {
        if (key == null) {
            return null;
        }
        ReentrantLock downloadLock = downloadLocks.get(key);
        if (downloadLock == null) {
            downloadLock = new ReentrantLock();
            downloadLocks.put(key, downloadLock);
        }
        return downloadLock;
    }

    @Override
    public final void run() {
        if (runStatus != null) {
            switch (runStatus) {
                case DISPATCH:
                    runDispatch();
                    break;
                case DOWNLOAD:
                    setStatus(BaseRequest.Status.GET_DOWNLOAD_LOCK);
                    ReentrantLock downloadLock = getDownloadLock(attrs.getRealUri());
                    downloadLock.lock();
                    runDownload();
                    downloadLock.unlock();
                    break;
                case LOAD:
                    setStatus(BaseRequest.Status.GET_LOAD_LOCK);
                    ReentrantLock loadLock = getLoadLock(attrs.getId());
                    loadLock.lock();
                    runLoad();
                    loadLock.unlock();
                    break;
                default:
                    new IllegalArgumentException("unknown runStatus: " + runStatus.name()).printStackTrace();
                    break;
            }
        }
    }

    public Sketch getSketch() {
        return sketch;
    }

    /**
     * 获取请求基本属性
     */
    public RequestAttrs getAttrs() {
        return attrs;
    }

    /**
     * 获取日志名称
     */
    @SuppressWarnings("WeakerAccess")
    public String getLogName() {
        return logName;
    }

    /**
     * 日志名称
     */
    void setLogName(String logName) {
        this.logName = logName;
    }

    /**
     * 提交到分发线程执行分发
     */
    protected void submitRunDispatch() {
        this.runStatus = RunStatus.DISPATCH;
        sketch.getConfiguration().getRequestExecutor().submitDispatch(this);
    }

    /**
     * 提交到网络线程执行下载
     */
    protected void submitRunDownload() {
        this.runStatus = RunStatus.DOWNLOAD;
        sketch.getConfiguration().getRequestExecutor().submitDownload(this);
    }

    /**
     * 提交到本地线程执行加载
     */
    protected void submitRunLoad() {
        this.runStatus = RunStatus.LOAD;
        sketch.getConfiguration().getRequestExecutor().submitLoad(this);
    }

    /**
     * 提交请求
     */
    final void submit() {
        submitRunDispatch();
    }

    /**
     * 推到主线程处理完成
     */
    protected void postRunCompleted() {
        CallbackHandler.postRunCompleted(this);
    }

    /**
     * 推到主线程处理取消
     */
    void postRunCanceled() {
        CallbackHandler.postRunCanceled(this);
    }

    /**
     * 推到主线程处理失败
     */
    protected void postRunFailed() {
        CallbackHandler.postRunFailed(this);
    }

    /**
     * 推到主线程处理进度
     */
    void postRunUpdateProgress(int totalLength, int completedLength) {
        CallbackHandler.postRunUpdateProgress(this, totalLength, completedLength);
    }

    /**
     * 在分发线程执行分发
     */
    protected abstract void runDispatch();

    /**
     * 在网络线程执行下载
     */
    protected abstract void runDownload();

    /**
     * 在本地线程执行加载
     */
    protected abstract void runLoad();

    /**
     * 在主线程处理进度
     */
    protected abstract void runUpdateProgressInMainThread(int totalLength, int completedLength);

    /**
     * 在主线程处理取消
     */
    protected abstract void runCanceledInMainThread();

    /**
     * 在主线程处理完成
     */
    protected abstract void runCompletedInMainThread();

    /**
     * 在主线程处理失败
     */
    protected abstract void runFailedInMainThread();

    /**
     * 运行状态
     */
    private enum RunStatus {
        /**
         * 分发
         */
        DISPATCH,

        /**
         * 加载
         */
        LOAD,

        /**
         * 下载
         */
        DOWNLOAD,
    }

    /**
     * 获取状态
     */
    @SuppressWarnings("WeakerAccess")
    public Status getStatus() {
        return status;
    }

    /**
     * 设置状态
     */
    void setStatus(Status status) {
        this.status = status;
    }

    /**
     * 获取失败原因
     */
    public FailedCause getFailedCause() {
        return failedCause;
    }

    /**
     * 设置失败原因
     */
    @SuppressWarnings("unused")
    protected void setFailedCause(FailedCause failedCause) {
        this.failedCause = failedCause;
    }

    /**
     * 获取取消原因
     */
    public CancelCause getCancelCause() {
        return cancelCause;
    }

    /**
     * 设置取消原因
     */
    protected void setCancelCause(CancelCause cancelCause) {
        this.cancelCause = cancelCause;
    }

    /**
     * 请求是否已经结束了
     */
    public boolean isFinished() {
        return status == Status.COMPLETED || status == Status.CANCELED || status == Status.FAILED;
    }

    /**
     * 请求是不是已经取消了
     */
    public boolean isCanceled() {
        return status == Status.CANCELED;
    }

    /**
     * 失败了
     */
    protected void failed(FailedCause failedCause) {
        this.status = Status.FAILED;
        this.failedCause = failedCause;
    }

    /**
     * 取消了
     */
    protected void canceled(CancelCause cancelCause) {
        this.status = Status.CANCELED;
        this.cancelCause = cancelCause;
    }

    /**
     * 取消请求
     *
     * @return false：请求已经结束了
     */
    public boolean cancel() {
        if (!isFinished()) {
            canceled(CancelCause.NORMAL);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 请求的状态
     */
    @SuppressWarnings("WeakerAccess")
    public enum Status {
        /**
         * 等待分发
         */
        WAIT_DISPATCH,

        /**
         * 正在分发
         */
        DISPATCHING,

        /**
         * 等待下载
         */
        WAIT_DOWNLOAD,

        /**
         * 正在获取下载锁
         */
        GET_DOWNLOAD_LOCK,

        /**
         * 正在下载
         */
        DOWNLOADING,

        /**
         * 等待加载
         */
        WAIT_LOAD,

        /**
         * 正在获取加载锁
         */
        GET_LOAD_LOCK,

        /**
         * 正在加载
         */
        LOADING,

        /**
         * 等待显示
         */
        WAIT_DISPLAY,

        /**
         * 正在显示
         */
        DISPLAYING,

        /**
         * 已完成
         */
        COMPLETED,

        /**
         * 已失败
         */
        FAILED,

        /**
         * 已取消
         */
        CANCELED,
    }
}
