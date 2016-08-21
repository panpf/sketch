package me.xiaopan.sketch.feature.large;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.util.Log;

import java.lang.ref.WeakReference;

import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.decode.ImageFormat;

// TODO 自定义任务执行器以及线程池
public class ImageRegionDecodeTask extends AsyncTask<Integer, Integer, Bitmap> {
    private static final String NAME = "ImageRegionDecodeTask";

    private WeakReference<SuperLargeImageViewer> largeImageViewerWeakReference;
    private WeakReference<ImageRegionDecoder> decoderReference;
    private Rect srcRect;
    private RectF visibleRect;
    private int inSampleSize;
    private float scale;

    private int cancelStatus;  // 0：未取消；1：软取消；2：强取消

    public ImageRegionDecodeTask(SuperLargeImageViewer largeImageViewer,
                                 ImageRegionDecoder decoder, Rect srcRect, RectF visibleRect,
                                 int inSampleSize, float scale) {
        this.largeImageViewerWeakReference = new WeakReference<SuperLargeImageViewer>(largeImageViewer);
        this.decoderReference = new WeakReference<ImageRegionDecoder>(decoder);
        this.srcRect = srcRect;
        this.visibleRect = visibleRect;
        this.inSampleSize = inSampleSize;
        this.scale = scale;
    }

    @SuppressWarnings("unused")
    public boolean isCanceled() {
        return cancelStatus != 0;
    }

    @SuppressWarnings("unused")
    public boolean isForceCanceled() {
        return cancelStatus == 2;
    }

    public void cancelTask(boolean force) {
        if (Sketch.isDebugMode()) {
            Log.w(Sketch.TAG, NAME + ". cancelTask. "
                    + "visibleRect=" + visibleRect.toString()
                    + ", inSampleSize=" + inSampleSize
                    + ", force=" + force);
        }
        cancelStatus = force ? 2 : 1;
    }

    @Override
    protected Bitmap doInBackground(Integer... params) {
        if (isCanceled()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME + ". canceled on just started. "
                        + "visibleRect=" + visibleRect.toString()
                        + ", inSampleSize=" + inSampleSize);
            }
            return null;
        }

        ImageRegionDecoder decoder = decoderReference.get();
        if (decoder == null) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME + ". decoder is null. "
                        + "visibleRect=" + visibleRect.toString()
                        + ", inSampleSize=" + inSampleSize);
            }
            return null;
        }

        if (!decoder.isReady()) {
            Log.w(Sketch.TAG, NAME + ". decoder not ready. "
                    + "visibleRect=" + visibleRect.toString()
                    + ", inSampleSize=" + inSampleSize);
            return null;
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;
        ImageFormat imageFormat = decoder.getImageFormat();
        if (imageFormat != null) {
            options.inPreferredConfig = imageFormat.getConfig(false);
        }

        Bitmap bitmap = decoder.decodeRegion(srcRect, options);
        if (bitmap == null || bitmap.isRecycled()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME + ". bitmap is null or recycled on decode after. "
                        + "visibleRect=" + visibleRect.toString()
                        + ", inSampleSize=" + inSampleSize);
            }
            return null;
        }

        // 为什么这里只过滤强制取消？
        // 首先都已经解码出来了，不显示就浪费了
        // 另外即使在缓慢的滑动过程中，也只有最后一个任务能够显示解码的图片，会给人一种解码速度很慢的感觉
        if (isForceCanceled()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME + ". force canceled on decode after. "
                        + "visibleRect=" + visibleRect.toString()
                        + ", inSampleSize=" + inSampleSize);
            }
            bitmap.recycle();
            return null;
        }

        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);

        if (bitmap == null) {
            return;
        }

        // 为什么这里不过滤取消？
        // 首席那都已经解码出来了，不显示就浪费了
        // 另外即使在缓慢的滑动过程中，也只有最后一个任务能够显示解码的图片，会给人一种解码速度很慢的感觉
        if (isForceCanceled()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME + ". force canceled on post execute. "
                        + "visibleRect=" + visibleRect.toString()
                        + ", inSampleSize=" + inSampleSize);
            }
            return;
        }

        SuperLargeImageViewer largeImageViewer = largeImageViewerWeakReference.get();
        if (largeImageViewer == null) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME + ". largeImageViewer on null in post execute. "
                        + "visibleRect=" + visibleRect.toString()
                        + ", inSampleSize=" + inSampleSize);
            }
            bitmap.recycle();
            return;
        }

        if (largeImageViewer.isAvailable()) {
            largeImageViewer.showImageRegion(srcRect, inSampleSize, bitmap, visibleRect, scale);
        } else {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME + ". largeImageViewer not available on post execute. "
                        + "visibleRect=" + visibleRect.toString()
                        + ", inSampleSize=" + inSampleSize);
            }
            bitmap.recycle();
        }
    }
}
