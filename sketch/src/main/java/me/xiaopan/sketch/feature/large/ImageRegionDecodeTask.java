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

public class ImageRegionDecodeTask extends AsyncTask<Integer, Integer, Bitmap> {
    private static final String NAME = "ImageRegionDecodeTask";

    private WeakReference<SuperLargeImageViewer> largeImageViewerWeakReference;
    private WeakReference<ImageRegionDecoder> decoderReference;
    private Rect srcRect;
    private boolean canceled;
    private RectF visibleRect;
    private int inSampleSize;

    // TODO 自定义任务执行器以及线程池
    public ImageRegionDecodeTask(SuperLargeImageViewer largeImageViewer, ImageRegionDecoder decoder, Rect srcRect, RectF visibleRect, int inSampleSize) {
        this.largeImageViewerWeakReference = new WeakReference<SuperLargeImageViewer>(largeImageViewer);
        this.decoderReference = new WeakReference<ImageRegionDecoder>(decoder);
        this.srcRect = srcRect;
        this.visibleRect = visibleRect;
        this.inSampleSize = inSampleSize;
    }

    @SuppressWarnings("unused")
    public boolean isCanceled() {
        return canceled;
    }

    public void cancel() {
        canceled = true;
    }

    @Override
    protected Bitmap doInBackground(Integer... params) {
        if (canceled) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME + ". canceled. just started. " + Integer.toHexString(hashCode()));
            }
            return null;
        }

        ImageRegionDecoder decoder = decoderReference.get();
        if (decoder == null) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME + ". decoder is null");
            }
            return null;
        }

        if (!decoder.isReady()) {
            Log.w(Sketch.TAG, NAME + ". init decoder not ready");
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
                Log.w(Sketch.TAG, NAME + ". bitmap is null or recycled");
            }
            return null;
        }

        if (isCanceled()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME + ". canceled. decode after. " + Integer.toHexString(hashCode()));
            }
            bitmap.recycle();
            return null;
        }

        Bitmap.Config config = bitmap.getConfig();
        if (Sketch.isDebugMode()) {
            Log.i(Sketch.TAG, NAME + ". decode success - "
                    + "visibleRect=" + visibleRect.toString()
                    + ", srcRect=" + srcRect.toString()
                    + ", inSampleSize=" + inSampleSize
                    + ", bitmapSize=" + bitmap.getWidth() + "x" + bitmap.getHeight()
                    + ", bitmapConfig=" + (config != null ? config.name() : null));
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);

        if (bitmap == null) {
            return;
        }

        if (isCanceled()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME + ". canceled. post execute. " + Integer.toHexString(hashCode()));
            }
            return;
        }

        SuperLargeImageViewer largeImageViewer = largeImageViewerWeakReference.get();
        if (largeImageViewer == null) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME + ". onPostExecute. largeImageViewer is null");
            }
            bitmap.recycle();
            return;
        }

        if (largeImageViewer.isAvailable()) {
            largeImageViewer.onDecodeCompleted(bitmap, visibleRect);
        } else {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME + ". onPostExecute. largeImageViewer not available");
            }
            bitmap.recycle();
        }

    }
}
