package me.xiaopan.sketchsample.largeimage;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import me.xiaopan.sketchsample.R;

public class LargeImageController {
    private static final String NAME = "LargeImageController";

    private ImageView imageView;

    private ImageRegionDecoder decoder;
    private Bitmap bitmap;
    private Rect bitmapRect;
    private Matrix matrix = new Matrix();
    private Rect srcRect = new Rect();
    private Rect visibleRect = new Rect();
    private Paint paint;
    private DecodeRegionImageTask lastTask;

    public LargeImageController(ImageView imageView) {
        this.imageView = imageView;
        bitmap = ((BitmapDrawable) imageView.getResources().getDrawable(R.mipmap.ic_launcher)).getBitmap();
        bitmapRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
    }

    public void onAttachedToWindow() {

    }

    public void onLayout(boolean changed, int left, int top, int right, int bottom) {

    }

    public void onDraw(Canvas canvas) {
        if (bitmap == null || bitmap.isRecycled()) {
            return;
        }

        if (paint == null) {
            paint = new Paint();
        }

        int saveCount = canvas.save();

//        canvas.setMatrix(matrix);
//        visibleRect.left = (imageView.getWidth() - bitmap.getWidth()) / 2;
//        visibleRect.top = (imageView.getHeight() - bitmap.getHeight()) / 2;
//        visibleRect.right = visibleRect.left + imageView.getWidth();
//        visibleRect.bottom = visibleRect.top + imageView.getHeight();
//        canvas.drawBitmap(bitmap, bitmapRect, visibleRect, paint);
        canvas.concat(matrix);
        canvas.drawBitmap(bitmap, 0, 0, paint);

        canvas.restoreToCount(saveCount);
    }

    public void onDetachedFromWindow() {

    }

    public void setImage(String uri) {
        decoder = new ImageRegionDecoder(imageView.getContext(), uri);
        decoder.init();
//        if (!decoder.isReady()) {
//            Log.d(Sketch.TAG, NAME + ". init ImageRegionDecoder failed");
//            return;
//        }
//
//        int widthAverage = decoder.getImageWidth() / 3;
//        int heightAverage = decoder.getImageHeight() / 3;
//        int left = widthAverage;
//        int top = heightAverage;
//        int right = left + widthAverage;
//        int bottom = top + heightAverage;
//        srcRect.set(left, top, right, bottom);
    }

    public void onDecodeCompleted(Bitmap newBitmap, Rect srcRect) {
        if (!this.srcRect.equals(srcRect)) {
            return;
        }

        Bitmap oldBitmap = bitmap;
        if (oldBitmap != null) {
            oldBitmap.recycle();
        }
        bitmap = newBitmap;
        imageView.invalidate();
    }

    public void update(Matrix drawMatrix, RectF visibleRectF, RectF srcRectF, float scale) {
        if (lastTask != null) {
            lastTask.cancel();
            lastTask = null;
        }

        if (visibleRectF != null) {
            visibleRect.set((int) visibleRectF.left, (int) visibleRectF.top, (int) visibleRectF.right, (int) visibleRectF.bottom);
        } else {
            visibleRect.setEmpty();
        }

        if (srcRectF != null) {
            srcRect.set((int) srcRectF.left, (int) srcRectF.top, (int) srcRectF.right, (int) srcRectF.bottom);
        } else {
            srcRect.setEmpty();
        }

        if (drawMatrix != null) {
            matrix.set(drawMatrix);
        } else {
            matrix.reset();
        }

//        lastTask = new DecodeRegionImageTask(this, decoder, new Rect(srcRect));
//        lastTask.execute(0);
    }
}
