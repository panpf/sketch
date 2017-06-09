package me.xiaopan.sketchsample.bean;

import me.xiaopan.sketchsample.util.FileScanner;

/**
 * App信息
 */
public class AppInfo implements FileScanner.FileItem{
    private String name;
    private String packageName;
    private String id;
    private String versionName;
    private String formattedAppSize;
    private String sortName;
    private String apkFilePath;
    private int versionCode;
    private long appSize;
    private boolean tempInstalled;

    public AppInfo(boolean tempInstalled) {
        this.tempInstalled = tempInstalled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getFormattedAppSize() {
        return formattedAppSize;
    }

    public void setFormattedAppSize(String formattedAppSize) {
        this.formattedAppSize = formattedAppSize;
    }

    public String getSortName() {
        return sortName;
    }

    public void setSortName(String sortName) {
        this.sortName = sortName;
    }

    public String getApkFilePath() {
        return apkFilePath;
    }

    public void setApkFilePath(String apkFilePath) {
        this.apkFilePath = apkFilePath;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public boolean isTempInstalled() {
        return tempInstalled;
    }

    public long getAppSize() {
        return appSize;
    }

    public void setAppSize(long appSize) {
        this.appSize = appSize;
    }

    @Override
    public String getFilePath() {
        return apkFilePath;
    }

    @Override
    public long getFileLength() {
        return appSize;
    }
}
