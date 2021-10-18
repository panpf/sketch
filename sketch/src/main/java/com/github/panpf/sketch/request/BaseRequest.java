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

package com.github.panpf.sketch.request;

import android.content.Context;

import androidx.annotation.AnyThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.annotation.WorkerThread;

import com.github.panpf.sketch.Configuration;
import com.github.panpf.sketch.SLog;
import com.github.panpf.sketch.Sketch;
import com.github.panpf.sketch.uri.UriModel;

@SuppressWarnings("WeakerAccess")
public abstract class BaseRequest implements Runnable {
    @NonNull
    private Sketch sketch;
    @NonNull
    private String uri;
    @NonNull
    private UriModel uriModel;
    @NonNull
    private String key;
    @NonNull
    private ResultShareManager resultShareManager;
    @NonNull
    private String logName;

    @Nullable
    private String diskCacheKey;
    @Nullable
    private Status status;
    @Nullable
    private ErrorCause errorCause;
    @Nullable
    private CancelCause cancelCause;
    @Nullable
    private RunStatus runStatus;
    private boolean sync;

    BaseRequest(@NonNull Sketch sketch, @NonNull String uri, @NonNull UriModel uriModel, @NonNull String key, @NonNull String logName) {
        this.sketch = sketch;
        this.uri = uri;
        this.uriModel = uriModel;
        this.key = key;
        this.logName = logName;
        this.resultShareManager = sketch.getConfiguration().getResultShareManager();
    }

    @NonNull
    public Sketch getSketch() {
        return sketch;
    }

    public Context getContext() {
        return sketch.getConfiguration().getContext();
    }

    public Configuration getConfiguration() {
        return sketch.getConfiguration();
    }

    @NonNull
    public String getUri() {
        return uri;
    }

    @NonNull
    public UriModel getUriModel() {
        return uriModel;
    }

    @NonNull
    public String getKey() {
        return key;
    }

    @NonNull
    public String getDiskCacheKey() {
        final String diskCacheKey = this.diskCacheKey;
        if (diskCacheKey != null) return diskCacheKey;

        String newDiskCacheKey = uriModel.getDiskCacheKey(uri);
        this.diskCacheKey = newDiskCacheKey;
        return newDiskCacheKey;
    }

    @NonNull
    public String getLogName() {
        return logName;
    }

    @Nullable
    public Status getStatus() {
        return status;
    }

    public void setStatus(@NonNull Status status) {
        this.status = status;
    }

    @Nullable
    public ErrorCause getErrorCause() {
        return errorCause;
    }

    protected void setErrorCause(@NonNull ErrorCause cause) {
        this.errorCause = cause;
        if (SLog.isLoggable(SLog.DEBUG)) {
            SLog.dmf(getLogName(), "Request error. %s. %s. %s", cause.name(), getThreadName(), getKey());
        }
    }

    @Nullable
    public CancelCause getCancelCause() {
        return cancelCause;
    }

    protected void setCancelCause(@NonNull CancelCause cause) {
        if (!isFinished()) {
            this.cancelCause = cause;
            if (SLog.isLoggable(SLog.DEBUG)) {
                SLog.dmf(getLogName(), "Request cancel. %s. %s. %s", cause.name(), getThreadName(), getKey());
            }
        }
    }

    public boolean isFinished() {
        return status == Status.COMPLETED || status == Status.CANCELED || status == Status.FAILED;
    }

    public boolean isCanceled() {
        return status == Status.CANCELED;
    }

    // todo 作为结果的一种传入 finished 方法处理
    protected void doError(@NonNull ErrorCause errorCause) {
        setErrorCause(errorCause);
        setStatus(Status.FAILED);
    }

    // todo 作为结果的一种传入 finished 方法处理
    protected void doCancel(@NonNull CancelCause cancelCause) {
        setCancelCause(cancelCause);
        setStatus(Status.CANCELED);
    }

    /**
     * Cancel Request
     *
     * @return false：request finished
     */
    public boolean cancel(@NonNull CancelCause cancelCause) {
        if (!isFinished()) {
            doCancel(cancelCause);
            return true;
        } else {
            return false;
        }
    }

    @NonNull
    public String getThreadName() {
        return Thread.currentThread().getName();
    }

    @Override
    public final void run() {
        final RunStatus runStatus = this.runStatus;
        if (runStatus == RunStatus.DISPATCH) {
            setStatus(Status.START_DISPATCH);
            DispatchResult dispatchResult = runDispatch();
            if (dispatchResult instanceof DownloadSuccessResult) {
                DownloadResult downloadResult = ((DownloadSuccessResult) dispatchResult).result;
                onRunDownloadFinished(downloadResult);
                if (this instanceof DownloadRequest) {
                    resultShareManager.unregisterDownloadShareProvider((DownloadRequest) this);
                }
            } else if (dispatchResult instanceof RunDownoadResult) {
                submitDownload();
            } else if (dispatchResult instanceof RunLoadResult) {
                submitLoad();
            }
        } else if (runStatus == RunStatus.DOWNLOAD) {
            setStatus(Status.START_DOWNLOAD);
            DownloadResult downloadResult = runDownload();
            onRunDownloadFinished(downloadResult);
            if (this instanceof DownloadRequest) {
                resultShareManager.unregisterDownloadShareProvider((DownloadRequest) this);
            }
        } else if (runStatus == RunStatus.LOAD) {
            setStatus(Status.START_LOAD);
            onRunLoadFinished(runLoad());
            if (this instanceof DisplayRequest) {
                resultShareManager.unregisterDisplayResultShareProvider((DisplayRequest) this);
            }
        } else {
            SLog.emf(getLogName(), "Unknown runStatus: %s", (runStatus != null ? runStatus.name() : null));
        }
    }

    boolean isSync() {
        return sync;
    }

    void setSync(boolean sync) {
        this.sync = sync;
    }

    void submitDispatch() {
        this.runStatus = RunStatus.DISPATCH;
        if (sync) {
            run();
        } else {
            setStatus(Status.WAIT_DISPATCH);
            getConfiguration().getExecutor().submitDispatch(this);
        }
    }

    void submitDownload() {
        this.runStatus = RunStatus.DOWNLOAD;
        if (sync) {
            run();
        } else {
            if (this instanceof DownloadRequest && ((DownloadRequest) this).canUseDownloadShare()) {
                DownloadRequest downloadRequest = (DownloadRequest) this;
                if (!resultShareManager.requestAttachDownloadShare(downloadRequest)) {
                    resultShareManager.registerDownloadShareProvider(downloadRequest);
                    setStatus(Status.WAIT_DOWNLOAD);
                    getConfiguration().getExecutor().submitDownload(this);
                }
            } else {
                setStatus(Status.WAIT_DOWNLOAD);
                getConfiguration().getExecutor().submitDownload(this);
            }
        }
    }

    void submitLoad() {
        this.runStatus = RunStatus.LOAD;
        if (sync) {
            run();
        } else {
            if (this instanceof DisplayRequest && ((DisplayRequest) this).canUseDisplayShare()) {
                DisplayRequest displayRequest = (DisplayRequest) this;
                if (!resultShareManager.requestAttachDisplayShare(displayRequest)) {
                    resultShareManager.registerDisplayResultShareProvider(displayRequest);
                    setStatus(Status.WAIT_LOAD);
                    getConfiguration().getExecutor().submitLoad(this);
                }
            } else {
                setStatus(Status.WAIT_LOAD);
                getConfiguration().getExecutor().submitLoad(this);
            }
        }
    }


    @Nullable
    @WorkerThread
    abstract DispatchResult runDispatch();

    @Nullable
    @WorkerThread
    abstract DownloadResult runDownload();

    @WorkerThread
    abstract void onRunDownloadFinished(@Nullable DownloadResult result);

    @Nullable
    @WorkerThread
    abstract LoadResult runLoad();

    @WorkerThread
    abstract void onRunLoadFinished(@Nullable LoadResult result);

    void postToMainRunUpdateProgress(int totalLength, int completedLength) {
        CallbackHandler.postRunUpdateProgress(this, totalLength, completedLength);
    }

    @UiThread
    abstract void runUpdateProgressInMain(int totalLength, int completedLength);

    @AnyThread
    protected void postRunCompleted() {
        CallbackHandler.postRunCompleted(this);
    }

    @UiThread
    abstract void runCompletedInMain();

    @AnyThread
    protected void postToMainRunError() {
        CallbackHandler.postRunError(this);
    }

    @UiThread
    abstract void runErrorInMain();

    @AnyThread
    void postToMainRunCanceled() {
        CallbackHandler.postRunCanceled(this);
    }

    @UiThread
    abstract void runCanceledInMain();

    public final void updateProgress(int totalLength, int completedLength) {
        onUpdateProgress(totalLength, completedLength);

        if (this instanceof DownloadRequest) {
            resultShareManager.updateDownloadProgress((DownloadRequest) this, totalLength, completedLength);
        }
    }

    abstract void onUpdateProgress(int totalLength, int completedLength);


    private enum RunStatus {
        DISPATCH, LOAD, DOWNLOAD,
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
