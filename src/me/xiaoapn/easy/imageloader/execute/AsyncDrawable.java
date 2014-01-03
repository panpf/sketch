package me.xiaoapn.easy.imageloader.execute;

import java.lang.ref.WeakReference;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

public class AsyncDrawable extends BitmapDrawable {
    private final WeakReference<RequestExecuteRunnable> bitmapWorkerTaskReference;

    public AsyncDrawable(Resources res, Bitmap bitmap, RequestExecuteRunnable runnable) {
        super(res, bitmap);
        bitmapWorkerTaskReference = new WeakReference<RequestExecuteRunnable>(runnable);
    }

    public RequestExecuteRunnable getBitmapWorkerTask() {
        return bitmapWorkerTaskReference.get();
    }
}