package me.panpf.sketch.viewfun;

import androidx.annotation.NonNull;

public class ViewFunctionItem {
    public final int priority;
    @NonNull
    public final ViewFunction function;

    public ViewFunctionItem(int priority, @NonNull ViewFunction function) {
        this.priority = priority;
        this.function = function;
    }
}
