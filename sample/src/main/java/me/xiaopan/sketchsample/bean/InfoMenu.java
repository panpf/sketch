package me.xiaopan.sketchsample.bean;

import android.support.annotation.NonNull;

import me.xiaopan.assemblyadapter.AssemblyRecyclerAdapter;

public abstract class InfoMenu {
    public String title;
    private String info;

    public InfoMenu(String title) {
        this.title = title;
    }

    @NonNull
    public abstract String getInfo();
    public abstract void onClick(AssemblyRecyclerAdapter adapter);
}
