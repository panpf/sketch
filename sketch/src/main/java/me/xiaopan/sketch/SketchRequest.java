package me.xiaopan.sketch;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import me.xiaopan.sketch.execute.RequestExecutor;

public abstract class SketchRequest implements Runnable {
    protected static final Handler handler;
    private static final int WHAT_RUN_COMPLETED = 33002;
    private static final int WHAT_RUN_FAILED = 33003;
    private static final int WHAT_RUN_CANCELED = 33004;
    private static final int WHAT_RUN_UPDATE_PROGRESS = 33005;

    static {
        handler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.obj instanceof SketchRequest) {
                    ((SketchRequest) msg.obj).runInMainThread(msg.what, msg);
                    return true;
                }

                return false;
            }
        });
    }

    private RunStatus runStatus;
    private RequestExecutor requestExecutor;

    public SketchRequest(RequestExecutor requestExecutor) {
        this.requestExecutor = requestExecutor;
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

    /**
     * 提交到分发线程执行分发
     */
    protected void submitRunDispatch() {
        this.runStatus = RunStatus.DISPATCH;
        requestExecutor.getRequestDispatchExecutor().execute(this);
    }

    /**
     * 提交到网络线程执行下载
     */
    protected void submitRunDownload() {
        this.runStatus = RunStatus.DOWNLOAD;
        requestExecutor.getNetRequestExecutor().execute(this);
    }

    /**
     * 提交到本地线程执行加载
     */
    protected void submitRunLoad() {
        this.runStatus = RunStatus.LOAD;
        requestExecutor.getLocalRequestExecutor().execute(this);
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
}
