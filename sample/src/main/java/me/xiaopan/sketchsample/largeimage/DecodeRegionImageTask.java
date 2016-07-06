package me.xiaopan.sketchsample.largeimage;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.util.Log;

import java.lang.ref.WeakReference;

import me.xiaopan.sketch.Sketch;

public class DecodeRegionImageTask extends AsyncTask<Integer, Integer, Bitmap> {
    private static final String NAME = "DecodeRegionImageTask";

    private WeakReference<LargeImageController> controllerReference;
    private WeakReference<ImageRegionDecoder> decoderReference;
    private Rect srcRect;
    private boolean canceled;

    public DecodeRegionImageTask(LargeImageController controller, ImageRegionDecoder decoder, Rect srcRect) {
        this.controllerReference = new WeakReference<LargeImageController>(controller);
        this.decoderReference = new WeakReference<ImageRegionDecoder>(decoder);
        this.srcRect = srcRect;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void cancel() {
        canceled = true;
    }

    @Override
    protected Bitmap doInBackground(Integer... params) {
        if (canceled) {
            return null;
        }

        ImageRegionDecoder decoder = decoderReference.get();
        if (decoder == null) {
            return null;
        }

        if (!decoder.isReady()) {
            decoder.init();

            if (!decoder.isReady()) {
                Log.d(Sketch.TAG, NAME + ". init ImageRegionDecoder failed");
                return null;
            }
        }

        if (canceled) {
            return null;
        }

        Bitmap bitmap = decoder.decodeRegion(srcRect, null);
        if (bitmap == null || bitmap.isRecycled()) {
            Log.d(Sketch.TAG, NAME + ". bitmap is null or recycled");
            return null;
        }

        if (canceled) {
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

        if (canceled) {
            bitmap.recycle();
            return;
        }

        LargeImageController controller = controllerReference.get();
        if (controller == null) {
            bitmap.recycle();
            return;
        }

        controller.onDecodeCompleted(bitmap, srcRect);
    }
}
