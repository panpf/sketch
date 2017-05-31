package me.xiaopan.sketchsample.bean;

import com.google.gson.annotations.SerializedName;

public class BaiduSearchImage extends BaiduImage {
    @SerializedName("thumbURL")
    private String sourceUrl;

    @SerializedName("width")
    private int width;

    @SerializedName("height")
    private int height;

    public String getSourceUrl() {
        return sourceUrl;
    }

    @Override
    public String getThumbUrl() {
        return sourceUrl;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
