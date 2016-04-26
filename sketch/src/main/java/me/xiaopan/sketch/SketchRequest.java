package me.xiaopan.sketch;

import me.xiaopan.sketch.execute.RequestExecutor;

public abstract class SketchRequest implements Runnable {
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
     * 在分发线程中执行分发处理
     */
    protected abstract void runDispatch();

    /**
     * 在网络线程中执行下载处理
     */
    protected abstract void runDownload();

    /**
     * 在本地线程中执行加载处理
     */
    protected abstract void runLoad();

    protected void onPostRunDispatch(){

    }

    protected void onPostRunDownload(){

    }

    protected void onPostRunLoad(){

    }

    private void postRunDispatch() {
        onPostRunDispatch();

        this.runStatus = RunStatus.DISPATCH;
        requestExecutor.getRequestDispatchExecutor().execute(this);
    }

    protected void postRunDownload() {
        onPostRunDownload();

        this.runStatus = RunStatus.DOWNLOAD;
        requestExecutor.getNetRequestExecutor().execute(this);
    }

    protected void postRunLoad() {
        onPostRunLoad();

        this.runStatus = RunStatus.LOAD;
        requestExecutor.getLocalRequestExecutor().execute(this);
    }

    public void submit(){
        postRunDispatch();
    }

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
