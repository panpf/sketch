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

import android.util.Log;

import me.xiaopan.sketch.Sketch;

abstract class Request {
    private Sketch sketch;
    private RequestAttrs attrs;

    private String logName = "Request";
    private Status status;
    private FailedCause failedCause;
    private CancelCause cancelCause;

    Request(Sketch sketch, RequestAttrs attrs) {
        this.sketch = sketch;
        this.attrs = attrs;
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
     * 获取状态
     */
    @SuppressWarnings("unused")
    public Status getStatus() {
        return status;
    }

    /**
     * 设置状态
     */
    void setStatus(Status status) {
        this.status = status;
        if (Sketch.isDebugMode()) {
            if (status == Status.FAILED) {
                printLogW("new status", status.getLog(), failedCause != null ? failedCause.name() : null);
            } else if (status == Status.CANCELED) {
                printLogW("new status", status.getLog(), cancelCause != null ? cancelCause.name() : null);
            } else {
                printLogD("new status", (status != null ? status.getLog() : null));
            }
        }
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
        return status == null || status == Status.COMPLETED || status == Status.CANCELED || status == Status.FAILED;
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
        setFailedCause(failedCause);
        setStatus(Status.FAILED);
    }

    /**
     * 取消了
     */
    protected void canceled(CancelCause cancelCause) {
        setCancelCause(cancelCause);
        setStatus(Status.CANCELED);
    }

    /**
     * 取消请求
     *
     * @return false：请求已经结束了
     */
    public boolean cancel(CancelCause cancelCause) {
        if (!isFinished()) {
            canceled(cancelCause);
            return true;
        } else {
            return false;
        }
    }

    protected String getThreadName() {
        return Thread.currentThread().getName();
    }

    private void printLog(int level, String... items) {
        StringBuilder builder = new StringBuilder();
        builder.append(getLogName());

        if (items != null && items.length > 0) {
            for (String item : items) {
                builder.append(". ").append(item);
            }
        }

        builder.append(". ").append(getThreadName());
        builder.append(". ").append(getAttrs().getId());

        if (level == 0) {
            Log.d(Sketch.TAG, builder.toString());
        } else if (level == 1) {
            Log.i(Sketch.TAG, builder.toString());
        } else if (level == 2) {
            Log.w(Sketch.TAG, builder.toString());
        } else if (level == 3) {
            Log.e(Sketch.TAG, builder.toString());
        }
    }

    protected void printLogD(String... items) {
        printLog(0, items);
    }

    protected void printLogI(String... items) {
        printLog(1, items);
    }

    protected void printLogW(String... items) {
        printLog(2, items);
    }

    protected void printLogE(String... items) {
        printLog(3, items);
    }

    /**
     * 请求的状态
     */
    public enum Status {
        /**
         * 等待分发
         */
        WAIT_DISPATCH("waitDispatch"),

        /**
         * 开始分发
         */
        START_DISPATCH("startDispatch"),

        /**
         * 拦截本地任务
         */
        INTERCEPT_LOCAL_TASK("interceptLocalTask"),


        /**
         * 等待下载
         */
        WAIT_DOWNLOAD("waitDownload"),

        /**
         * 开始下载
         */
        START_DOWNLOAD("startDownload"),

        /**
         * 获取磁盘缓存编辑锁
         */
        GET_DISK_CACHE_EDIT_LOCK("getDiskCacheEditLock"),

        /**
         * 检查磁盘缓存
         */
        CHECK_DISK_CACHE("checkDiskCache"),

        /**
         * 连接中
         */
        CONNECTING("connecting"),

        /**
         * 检查响应
         */
        CHECK_RESPONSE("checkResponse"),

        /**
         * 读取数据
         */
        READ_DATA("readData"),


        /**
         * 等待加载
         */
        WAIT_LOAD("waitLoad"),

        /**
         * 开始加载
         */
        START_LOAD("startLoad"),

        /**
         * 获取内存缓存编辑锁
         */
        GET_MEMORY_CACHE_EDIT_LOCK("getMemoryCacheEditLock"),

        /**
         * 检查内存缓存
         */
        CHECK_MEMORY_CACHE("checkMemoryCache"),

        /**
         * 预处理
         */
        PRE_PROCESS("preProcess"),

        /**
         * 解码中
         */
        DECODING("decoding"),

        /**
         * 处理中
         */
        PROCESSING("processing"),

        /**
         * 等待显示
         */
        WAIT_DISPLAY("waitDisplay"),


        /**
         * 已完成
         */
        COMPLETED("completed"),

        /**
         * 已失败
         */
        FAILED("failed"),

        /**
         * 已取消
         */
        CANCELED("canceled"),
        ;

        private String log;

        Status(String log) {
            this.log = log;
        }

        public String getLog() {
            return log;
        }
    }
}
