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

import android.content.Context;

import me.xiaopan.sketch.Configuration;
import me.xiaopan.sketch.SLog;
import me.xiaopan.sketch.SLogType;
import me.xiaopan.sketch.Sketch;

public abstract class BaseRequest {
    private BaseInfo info;
    private Sketch sketch;
    private String logName = "Request";
    private Status status;
    private ErrorCause errorCause;
    private CancelCause cancelCause;

    BaseRequest(Sketch sketch, BaseInfo info) {
        this.sketch = sketch;
        this.info = info;
    }

    public Sketch getSketch() {
        return sketch;
    }

    public Context getContext() {
        return sketch.getConfiguration().getContext();
    }

    public Configuration getConfiguration() {
        return sketch.getConfiguration();
    }

    public BaseInfo getInfo() {
        return info;
    }

    /**
     * 获取KEY
     */
    public String getKey() {
        return info.getKey();
    }

    /**
     * 获取uri
     */
    public String getUri() {
        return info.getUri();
    }

    /**
     * 获取真实的uri，例如原始uri是asset://sample.png，那么真实uri就是sample.png
     */
    public String getRealUri() {
        return info.getRealUri();
    }

    /**
     * 获取uri类型
     */
    public UriScheme getUriScheme() {
        return info.getUriScheme();
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
    public void setStatus(Status status) {
        this.status = status;
        if (SLogType.REQUEST.isEnabled()) {
            if (status == Status.FAILED) {
                printLogW("new status", status.getLog(), errorCause != null ? errorCause.name() : null);
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
    public ErrorCause getErrorCause() {
        return errorCause;
    }

    /**
     * 设置失败原因
     */
    @SuppressWarnings("unused")
    protected void setErrorCause(ErrorCause errorCause) {
        this.errorCause = errorCause;
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
    protected void error(ErrorCause errorCause) {
        setErrorCause(errorCause);
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

    private void printLog(int level, Object... items) {
        StringBuilder builder = new StringBuilder();
        if (items != null && items.length > 0) {
            for (Object item : items) {
                if (builder.length() > 0) {
                    builder.append(". ");
                }
                builder.append(item);
            }
        }

        builder.append(". ").append(Thread.currentThread().getName());
        builder.append(". ").append(getKey());

        if (level == 0) {
            SLog.d(SLogType.REQUEST, getLogName(), builder.toString());
        } else if (level == 1) {
            SLog.i(SLogType.REQUEST, getLogName(), builder.toString());
        } else if (level == 2) {
            SLog.w(SLogType.REQUEST, getLogName(), builder.toString());
        } else if (level == 3) {
            SLog.e(SLogType.REQUEST, getLogName(), builder.toString());
        }
    }

    protected void printLogD(Object... items) {
        printLog(0, items);
    }

    protected void printLogI(Object... items) {
        printLog(1, items);
    }

    protected void printLogW(Object... items) {
        printLog(2, items);
    }

    protected void printLogE(Object... items) {
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
        CANCELED("canceled"),;

        private String log;

        Status(String log) {
            this.log = log;
        }

        public String getLog() {
            return log;
        }
    }
}
