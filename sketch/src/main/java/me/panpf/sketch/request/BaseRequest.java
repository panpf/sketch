/*
 * Copyright (C) 2013 Peng fei Pan <sky@panpf.me>
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

package me.panpf.sketch.request;

import android.content.Context;
import androidx.annotation.NonNull;

import me.panpf.sketch.Configuration;
import me.panpf.sketch.SLog;
import me.panpf.sketch.Sketch;
import me.panpf.sketch.uri.UriModel;

public abstract class BaseRequest {
    private Sketch sketch;
    private String uri;
    private UriModel uriModel;
    private String key;

    private String diskCacheKey;
    private String logName = "Request";
    private Status status;
    private ErrorCause errorCause;
    private CancelCause cancelCause;

    BaseRequest(@NonNull Sketch sketch, @NonNull String uri, @NonNull UriModel uriModel, @NonNull String key) {
        this.sketch = sketch;
        this.uri = uri;
        this.uriModel = uriModel;
        this.key = key;
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

    public String getUri() {
        return uri;
    }

    public UriModel getUriModel() {
        return uriModel;
    }

    public String getKey() {
        return key;
    }

    public String getDiskCacheKey() {
        if (diskCacheKey == null) {
            diskCacheKey = uriModel.getDiskCacheKey(uri);
        }
        return diskCacheKey;
    }

    public String getLogName() {
        return logName;
    }

    void setLogName(String logName) {
        this.logName = logName;
    }

    @SuppressWarnings("unused")
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        if (!isFinished()) {
            this.status = status;
        }
    }

    public ErrorCause getErrorCause() {
        return errorCause;
    }

    @SuppressWarnings("unused")
    protected void setErrorCause(@NonNull ErrorCause cause) {
        if (!isFinished()) {
            this.errorCause = cause;
            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_FLOW)) {
                SLog.d(getLogName(), "Request error. %s. %s. %s", cause.name(), getThreadName(), getKey());
            }
        }
    }

    public CancelCause getCancelCause() {
        return cancelCause;
    }

    protected void setCancelCause(@NonNull CancelCause cause) {
        if (!isFinished()) {
            this.cancelCause = cause;
            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_FLOW)) {
                SLog.d(getLogName(), "Request cancel. %s. %s. %s", cause.name(), getThreadName(), getKey());
            }
        }
    }

    public boolean isFinished() {
        return status == Status.COMPLETED || status == Status.CANCELED || status == Status.FAILED;
    }

    public boolean isCanceled() {
        return status == Status.CANCELED;
    }

    protected void doError(@NonNull ErrorCause errorCause) {
        setErrorCause(errorCause);
        setStatus(Status.FAILED);
    }

    protected void doCancel(@NonNull CancelCause cancelCause) {
        setCancelCause(cancelCause);
        setStatus(Status.CANCELED);
    }

    /**
     * Cancel Request
     *
     * @return false：request finished
     */
    public boolean cancel(CancelCause cancelCause) {
        if (!isFinished()) {
            doCancel(cancelCause);
            return true;
        } else {
            return false;
        }
    }

    public String getThreadName() {
        return Thread.currentThread().getName();
    }

    public enum Status {
        WAIT_DISPATCH(),

        START_DISPATCH(),

        INTERCEPT_LOCAL_TASK(),


        WAIT_DOWNLOAD(),

        START_DOWNLOAD(),

        CHECK_DISK_CACHE(),

        CONNECTING(),

        READ_DATA(),


        WAIT_LOAD(),

        START_LOAD(),

        CHECK_MEMORY_CACHE(),

        DECODING(),

        PROCESSING(),

        WAIT_DISPLAY(),


        COMPLETED(),

        FAILED(),

        CANCELED(),
    }
}
