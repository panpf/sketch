package me.xiaopan.sketchsample.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BaiduImage {

    @SerializedName("replaceUrl")
    private List<ReplaceUrl> replaceUrlList;

    @SerializedName("width")
    private int width;

    @SerializedName("height")
    private int height;

    public String getUrl() {
        return replaceUrlList != null && replaceUrlList.size() > 0 ? replaceUrlList.get(0).objUrl : null;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public static class ReplaceUrl {
        @SerializedName("ObjURL")
        public String objUrl;
    }
}
