package me.xiaopan.sketchsample.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BaiduImageSearchResult {
    @SerializedName("listNum")
    private int total;

    @SerializedName("data")
    private List<BaiduImage> imageList;

    public int getTotal() {
        return total;
    }

    public List<BaiduImage> getImageList() {
        return imageList;
    }
}
