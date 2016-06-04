package me.xiaopan.sketch.request;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.locks.ReentrantLock;

import me.xiaopan.sketch.Sketch;

abstract class AsyncRequest extends Request implements Runnable{
    private static final Map<String, ReentrantLock> loadLocks = Collections.synchronizedMap(new WeakHashMap<String, ReentrantLock>());
    private static final Map<String, ReentrantLock> downloadLocks = Collections.synchronizedMap(new WeakHashMap<String, ReentrantLock>());

    private RunStatus runStatus;
    private boolean sync;

    AsyncRequest(Sketch sketch, RequestAttrs attrs) {
        super(sketch, attrs);
    }

    private synchronized ReentrantLock getLoadLock(String key) {
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

    private synchronized ReentrantLock getDownloadLock(String key) {
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
                    executeRunDispatch();
                    break;
                case DOWNLOAD:
                    executeRunDownload();
                    break;
                case LOAD:
                    executeRunLoad();
                    break;
                default:
                    new IllegalArgumentException("unknown runStatus: " + runStatus.name()).printStackTrace();
                    break;
            }
        }
    }

    public boolean isSync() {
        return sync;
    }

    /**
     * 设置是否同步处理
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
            executeRunDispatch();
        } else {
            getSketch().getConfiguration().getRequestExecutor().submitDispatch(this);
        }
    }

    /**
     * 执行分发
     */
    private void executeRunDispatch() {
        runDispatch();
    }

    /**
     * 提交到网络线程执行下载
     */
    protected void submitRunDownload() {
        this.runStatus = RunStatus.DOWNLOAD;
        if (sync) {
            executeRunDownload();
        } else {
            getSketch().getConfiguration().getRequestExecutor().submitDownload(this);
        }
    }

    /**
     * 执行下载
     */
    private void executeRunDownload() {
        setStatus(Request.Status.GET_DOWNLOAD_LOCK);
        ReentrantLock downloadLock = getDownloadLock(getAttrs().getRealUri());
        downloadLock.lock();
        runDownload();
        downloadLock.unlock();
    }

    /**
     * 提交到本地线程执行加载
     */
    protected void submitRunLoad() {
        this.runStatus = RunStatus.LOAD;
        if (sync) {
            executeRunLoad();
        } else {
            getSketch().getConfiguration().getRequestExecutor().submitLoad(this);
        }
    }

    /**
     * 执行加载
     */
    private void executeRunLoad() {
        boolean allowLoadLock = allowLoadLock();
        ReentrantLock loadLock = null;
        if (allowLoadLock) {
            setStatus(Request.Status.GET_LOAD_LOCK);
            loadLock = getLoadLock(getAttrs().getId());
            loadLock.lock();
        }

        runLoad();

        if (allowLoadLock) {
            loadLock.unlock();
        }
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
     * 是否允许给加载上锁
     */
    protected boolean allowLoadLock() {
        return false;
    }

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
