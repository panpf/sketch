package com.github.panpf.sketch.cache;

import androidx.annotation.NonNull;

import java.io.File;

import com.github.panpf.sketch.SketchException;

public class InstallDiskCacheException extends SketchException {
    @NonNull
    private File cacheDir;

    public InstallDiskCacheException(@NonNull Throwable cause, @NonNull File cacheDir) {
        super(cause);
        this.cacheDir = cacheDir;
    }

    @NonNull
    @Override
    public synchronized Throwable getCause() {
        return super.getCause();
    }

    @NonNull
    public File getCacheDir() {
        return cacheDir;
    }
}
