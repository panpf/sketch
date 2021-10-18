package com.github.panpf.sketch;

import androidx.annotation.NonNull;

public interface SketchCallback {
    void onError(@NonNull SketchException e);
}
