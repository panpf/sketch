package me.panpf.sketch.sample.vt.util;

import android.support.annotation.NonNull;

public interface NonNullObserver<DATA> {
    void onChanged(@NonNull DATA data);
}
