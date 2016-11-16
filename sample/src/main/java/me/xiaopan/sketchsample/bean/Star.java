package me.xiaopan.sketchsample.bean;

import com.google.gson.annotations.SerializedName;

public class Star {
    public String name;
    @SerializedName("avatar")
    public String avatarUrl;
}
