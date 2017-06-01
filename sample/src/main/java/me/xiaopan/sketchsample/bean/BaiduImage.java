package me.xiaopan.sketchsample.bean;

import com.google.gson.annotations.SerializedName;

public class BaiduImage {

    @SerializedName("thumbURL")
    private String sourceUrl;

    @SerializedName("width")
    private int width;

    @SerializedName("height")
    private int height;

    public String getSourceUrl() {
        return sourceUrl;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
