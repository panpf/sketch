package me.panpf.sketch.request;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Set;

public interface ResultShareDownload {
    boolean canUseResultShare();

    boolean isCanceled();

    @NonNull
    String getDownloadResultShareKey();

    @NonNull
    String getDownloadResultShareLog();

    void byDownloadResultShare(ResultShareDownload request);

    @Nullable
    Set<ResultShareDownload> getDownloadResultShareSet();

    boolean processDownloadResultShare();
}
