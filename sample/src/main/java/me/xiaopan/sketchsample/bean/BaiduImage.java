package me.xiaopan.sketchsample.bean;

import com.google.gson.annotations.SerializedName;

// TODO: 2017/5/31 0031 跟baidusearchimage融合
public class BaiduImage {
    @SerializedName("thumbnail_url")
    private String thumbUrl;

    @SerializedName("image_url")
    private String sourceUrl;

    @SerializedName("image_width")
    private int width;

    @SerializedName("image_height")
    private int height;

    public String getThumbUrl() {
        return thumbUrl;
    }

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
