package me.xiaopan.android.spear.sample.net.request;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.xiaopan.android.gohttp.HttpRequest;
import me.xiaopan.android.gohttp.requestobject.Request;
import me.xiaopan.android.gohttp.requestobject.URL;
import me.xiaopan.android.spear.sample.net.NetUtils;

/**
 * 明星目录请求
 */
public abstract class StarCatalogRequest implements Request{

    public static class ResponseHandler implements HttpRequest.ResponseHandleCompletedAfterListener<String> {
        @Override
        public Object onResponseHandleAfter(HttpRequest httpRequest, HttpResponse httpResponse, String sourceContent, boolean b, boolean b2) throws Throwable {
            Result result = new Result();
            String json = NetUtils.substring(sourceContent, "\"data\" : ", "\\}\\)\\;", null);
            result.setWomanStarList((List<Star>) new Gson().fromJson(json, new TypeToken<List<Star>>(){}.getType()));
            return result;
        }
    }

    public static class Result {
        private List<Star> womanStarList;

        public List<Star> getWomanStarList() {
            return womanStarList;
        }

        public void setWomanStarList(List<Star> womanStarList) {
            this.womanStarList = womanStarList;
        }
    }

    public static class Star{
        private String name;
        @SerializedName("avatar") private String avatarUrl;

        public String getName() {
            return name;
        }

        public String getAvatarUrl() {
            return avatarUrl;
        }
    }
}
