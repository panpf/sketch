package me.xiaopan.sketchsample.net.request;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpResponse;

import java.util.List;

import me.xiaopan.gohttp.HttpRequest;
import me.xiaopan.gohttp.requestobject.Request;
import me.xiaopan.sketchsample.bean.Star;
import me.xiaopan.sketchsample.net.NetUtils;

/**
 * 明星目录请求
 */
public abstract class StarCatalogRequest implements Request {

    public static class ResponseHandler implements HttpRequest.ResponseHandleCompletedAfterListener<String> {
        public boolean isMan;

        public ResponseHandler(boolean isMan) {
            this.isMan = isMan;
        }

        @Override
        public Object onResponseHandleAfter(HttpRequest httpRequest, HttpResponse httpResponse, String sourceContent, boolean b, boolean b2) throws Throwable {
            Result result = new Result();
            result.setTitle(isMan ? "男明星" : "女明星");
            String json = NetUtils.substring(sourceContent, "\"data\" : ", "\\}\\)\\;", null);
            result.setStarList((List<Star>) new Gson().fromJson(json, new TypeToken<List<Star>>() {}.getType()));
            return result;
        }
    }

    public static class Result {
        private String title;
        private List<Star> starList;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public List<Star> getStarList() {
            return starList;
        }

        public void setStarList(List<Star> starList) {
            this.starList = starList;
        }
    }

}
