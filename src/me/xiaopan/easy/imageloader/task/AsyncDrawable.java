package me.xiaopan.easy.imageloader.task;

import java.lang.ref.WeakReference;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

public class AsyncDrawable extends BitmapDrawable {
    private WeakReference<BitmapLoadTask> bitmapLoadTaskReference;

    public AsyncDrawable(Resources res, Bitmap bitmap, BitmapLoadTask runnable) {
        super(res, bitmap);
        bitmapLoadTaskReference = new WeakReference<BitmapLoadTask>(runnable);
    }

    public BitmapLoadTask getBitmapLoadTask() {
        return bitmapLoadTaskReference.get();
    }
}