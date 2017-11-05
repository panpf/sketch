package me.panpf.sketch.request;

import me.panpf.sketch.Sketch;
import me.panpf.sketch.uri.UriModel;

abstract class AsyncRequest extends BaseRequest implements Runnable {

    private RunStatus runStatus;
    private boolean sync;

    AsyncRequest(Sketch sketch, String uri, UriModel uriModel, String key) {
        super(sketch, uri, uriModel, key);
    }

    @Override
    public final void run() {
        if (runStatus != null) {
            switch (runStatus) {
                case DISPATCH:
                    executeDispatch();
                    break;
                case DOWNLOAD:
                    executeDownload();
                    break;
                case LOAD:
                    executeLoad();
                    break;
                default:
                    new IllegalArgumentException("unknown runStatus: " + runStatus.name()).printStackTrace();
                    break;
            }
        }
    }

    /**
     * 是否同步执行
     */
    public boolean isSync() {
        return sync;
    }

    /**
     * 设置是否同步执行
     */
    public void setSync(boolean sync) {
        this.sync = sync;
    }

    /**
     * 提交到分发线程执行分发
     */
    protected void submitRunDispatch() {
        this.runStatus = RunStatus.DISPATCH;
        if (sync) {
            executeDispatch();
        } else {
            getConfiguration().getExecutor().submitDispatch(this);
        }
    }

    private void executeDispatch() {
        setStatus(Status.START_DISPATCH);
        runDispatch();
    }

    /**
     * 提交到网络线程执行下载
     */
    protected void submitRunDownload() {
        this.runStatus = RunStatus.DOWNLOAD;
        if (sync) {
            executeDownload();
        } else {
            getConfiguration().getExecutor().submitDownload(this);
        }
    }

    private void executeDownload() {
        setStatus(Status.START_DOWNLOAD);
        runDownload();
    }

    /**
     * 提交到本地线程执行加载
     */
    protected void submitRunLoad() {
        this.runStatus = RunStatus.LOAD;
        if (sync) {
            executeLoad();
        } else {
            getConfiguration().getExecutor().submitLoad(this);
        }
    }

    private void executeLoad() {
        setStatus(Status.START_LOAD);
        runLoad();
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
    protected void postRunError() {
        CallbackHandler.postRunError(this);
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
     * 在主线程处理完成
     */
    protected abstract void runCompletedInMainThread();

    /**
     * 在主线程处理失败
     */
    protected abstract void runErrorInMainThread();

    /**
     * 在主线程处理取消
     */
    protected abstract void runCanceledInMainThread();

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
}
