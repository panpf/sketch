package me.xiaopan.android.spear.request;

/**
 * 你可以通过RequestFuture来查看请求的状态或者取消这个请求
 */
public class RequestFuture {
    public Request request;

    public RequestFuture(Request request) {
        this.request = request;
    }

    /**
     * 获取请求URI
     * @return 请求URI
     */
    public String getUri(){
        return request.getUri();
    }

    /**
     * 是否已经取消
     * @return 是否已经取消
     */
    public boolean isCanceled() {
        return request.isCanceled();
    }

    /**
     * 是否已经结束
     * @return 是否已经结束
     */
    public boolean isFinished() {
        return request.isFinished();
    }

    /**
     * 获取请求的状态
     * @return 请求的状态
     */
    public Request.Status getStatus() {
        return request.getStatus();
    }

    /**
     * 取消请求
     * @return true：取消成功；false：请求已经完成或已经取消
     */
    public boolean cancel() {
        return request.cancel();
    }
}