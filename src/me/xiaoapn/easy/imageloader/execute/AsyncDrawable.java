package me.xiaoapn.easy.imageloader.execute;

import java.lang.ref.WeakReference;

import me.xiaoapn.easy.imageloader.execute.task.LoadBitmapTask;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

public class AsyncDrawable extends BitmapDrawable {
    private final WeakReference<LoadBitmapTask> bitmapLoadTaskReference;

    public AsyncDrawable(Resources res, Bitmap bitmap, LoadBitmapTask runnable) {
        super(res, bitmap);
        bitmapLoadTaskReference = new WeakReference<LoadBitmapTask>(runnable);
    }

    public LoadBitmapTask getBitmapLoadTask() {
        return bitmapLoadTaskReference.get();
    }
}