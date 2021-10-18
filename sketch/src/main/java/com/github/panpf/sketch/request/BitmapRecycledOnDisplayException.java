package com.github.panpf.sketch.request;

import androidx.annotation.NonNull;

import com.github.panpf.sketch.SketchException;
import com.github.panpf.sketch.drawable.SketchDrawable;

public class BitmapRecycledOnDisplayException extends SketchException {
    @NonNull
    private DisplayRequest request;
    @NonNull
    private SketchDrawable sketchDrawable;

    public BitmapRecycledOnDisplayException(@NonNull DisplayRequest request, @NonNull SketchDrawable sketchDrawable) {
        this.request = request;
        this.sketchDrawable = sketchDrawable;
    }

    @NonNull
    public DisplayRequest getRequest() {
        return request;
    }

    @NonNull
    public SketchDrawable getSketchDrawable() {
        return sketchDrawable;
    }
}
