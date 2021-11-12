package com.github.panpf.sketch.request;

import androidx.annotation.NonNull;

public class DownloadSuccessResult implements DispatchResult {
    @NonNull
    public final DownloadResult result;

    public DownloadSuccessResult(@NonNull DownloadResult result) {
        this.result = result;
    }
}
