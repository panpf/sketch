package me.xiaopan.sketchsample.bean;

import me.xiaopan.assemblyadapter.AssemblyRecyclerAdapter;

public abstract class InfoMenu {
    public String title;
    private String info;

    public InfoMenu(String title) {
        this.title = title;
    }

    public abstract String getInfo();
    public abstract void onClick(AssemblyRecyclerAdapter adapter);
}
