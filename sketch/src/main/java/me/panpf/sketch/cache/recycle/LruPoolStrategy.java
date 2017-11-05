package me.panpf.sketch.cache.recycle;

import android.graphics.Bitmap;

import me.panpf.sketch.Key;

public interface LruPoolStrategy extends Key {
    void put(Bitmap bitmap);

    Bitmap get(int width, int height, Bitmap.Config config);

    Bitmap removeLast();

    String logBitmap(Bitmap bitmap);

    String logBitmap(int width, int height, Bitmap.Config config);

    int getSize(Bitmap bitmap);
}
