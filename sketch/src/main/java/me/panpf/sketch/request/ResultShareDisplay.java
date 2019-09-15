package me.panpf.sketch.request;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Set;

public interface ResultShareDisplay {
    @NonNull
    String getDisplayResultShareKey();

    boolean canByDisplayResultShare();

    boolean isCanceled();

    @NonNull
    String getDisplayResultShareLog();

    void byDisplayResultShare(ResultShareDisplay request);

    @Nullable
    Set<ResultShareDisplay> getDisplayResultShareSet();

    boolean processDisplayResultShare();
}
