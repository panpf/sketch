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
import android.os.Looper;
import android.os.Message;

import me.xiaopan.sketch.Sketch;

public abstract class BaseRequest implements Runnable {
    protected static final Handler handler;
    private static final int WHAT_RUN_COMPLETED = 33002;
    private static final int WHAT_RUN_FAILED = 33003;
    private static final int WHAT_RUN_CANCELED = 33004;
    private static final int WHAT_RUN_UPDATE_PROGRESS = 33005;

    static {
        handler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.obj instanceof BaseRequest) {
                    ((BaseRequest) msg.obj).runInMainThread(msg.what, msg);
                    return true;
                }

                return false;
            }
        });
    }

    private Sketch sketch;

    private RunStatus runStatus;

    private String logName = "SketchRequest";
    private Status status;
    private FailedCause failedCause;
    private CancelCause cancelCause;

    public BaseRequest(Sketch sketch) {
        this.sketch = sketch;
    }

    @Override
    public final void run() {
        if (runStatus != null) {
            switch (runStatus) {
                case DISPATCH:
                    runDispatch();
                    break;
                case DOWNLOAD:
                    runDownload();
                    break;
                case LOAD:
                    runLoad();
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

    public String getLogName() {
        return logName;
    }

    protected void setLogName(String logName) {
        this.logName = logName;
    }

    /**
     * 提交到分发线程执行分发
     */
    protected void submitRunDispatch() {
        this.runStatus = RunStatus.DISPATCH;
        sketch.getConfiguration().getRequestExecutor().getRequestDispatchExecutor().execute(this);
    }

    /**
     * 提交到网络线程执行下载
     */
    protected void submitRunDownload() {
        this.runStatus = RunStatus.DOWNLOAD;
        sketch.getConfiguration().getRequestExecutor().getNetRequestExecutor().execute(this);
    }

    /**
     * 提交到本地线程执行加载
     */
    protected void submitRunLoad() {
        this.runStatus = RunStatus.LOAD;
        sketch.getConfiguration().getRequestExecutor().getLocalRequestExecutor().execute(this);
    }

    /**
     * 推到主线程处理完成
     */
    protected void postRunCompleted() {
        handler.obtainMessage(WHAT_RUN_COMPLETED, this).sendToTarget();
    }

    /**
     * 推到主线程处理取消
     */
    protected void postRunCanceled() {
        handler.obtainMessage(WHAT_RUN_CANCELED, this).sendToTarget();
    }

    /**
     * 推到主线程处理失败
     */
    protected void postRunFailed() {
        handler.obtainMessage(WHAT_RUN_FAILED, this).sendToTarget();
    }

    /**
     * 推到主线程处理进度
     */
    protected void postRunUpdateProgress(int totalLength, int completedLength) {
        handler.obtainMessage(WHAT_RUN_UPDATE_PROGRESS, totalLength, completedLength, this).sendToTarget();
    }

    /**
     * 提交请求
     */
    public final void submit() {
        submitRunDispatch();
    }

    /**
     * 在主线程执行
     */
    protected void runInMainThread(int what, Message msg) {
        switch (what) {
            case WHAT_RUN_UPDATE_PROGRESS:
                runUpdateProgressInMainThread(msg.arg1, msg.arg2);
                break;
            case WHAT_RUN_CANCELED:
                runCanceledInMainThread();
                break;
            case WHAT_RUN_COMPLETED:
                runCompletedInMainThread();
                break;
            case WHAT_RUN_FAILED:
                runFailedInMainThread();
                break;
        }
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
    public Status getStatus() {
        return status;
    }

    /**
     * 设置状态
     */
    public void setStatus(Status status) {
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
    protected void failed(FailedCause failedCause){
        this.status = Status.FAILED;
        this.failedCause = failedCause;
    }

    /**
     * 取消了
     */
    protected void canceled(CancelCause cancelCause){
        this.status = Status.CANCELED;
        this.cancelCause = cancelCause;
    }

    /**
     * 取消请求
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
