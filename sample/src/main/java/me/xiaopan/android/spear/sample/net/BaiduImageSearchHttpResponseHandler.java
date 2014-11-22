package me.xiaopan.android.spear.sample.net;

import org.apache.http.HttpResponse;

import me.xiaopan.android.gohttp.HttpRequest;
import me.xiaopan.android.gohttp.HttpResponseHandler;

/**
 * 百度图片处理
 */
public class BaiduImageSearchHttpResponseHandler implements HttpResponseHandler{
    @Override
    public boolean canCache(HttpResponse httpResponse) {
        return false;
    }

    @Override
    public Object handleResponse(HttpRequest httpRequest, HttpResponse httpResponse) throws Throwable {
        return null;
    }
}
