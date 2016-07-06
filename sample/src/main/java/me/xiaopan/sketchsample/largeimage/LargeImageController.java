package me.xiaopan.sketchsample.largeimage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;

import me.xiaopan.sketch.Sketch;

public class LargeImageController {
    private static final String NAME = "LargeImageController";

    private ImageView imageView;

    private ImageRegionDecoder decoder;
    private Bitmap bitmap;
    private Rect bitmapRect;
    private Matrix matrix = new Matrix();
    private Rect currentSrcRect = new Rect();
    private RectF visibleRect = new RectF();
    private Paint paint;
    private DecodeRegionImageTask lastTask;

    public LargeImageController(ImageView imageView) {
        this.imageView = imageView;
    }

    private static int getSimpleSize(Context context, Rect srcRect) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Sketch.with(context).getConfiguration().getImageSizeCalculator().calculateInSampleSize(
                srcRect.width(), srcRect.height(),
                displayMetrics.widthPixels, displayMetrics.heightPixels);
    }

    public void onAttachedToWindow() {

    }

    public void onLayout(boolean changed, int left, int top, int right, int bottom) {

    }

    public void onDraw(Canvas canvas) {
        if (bitmap == null || bitmap.isRecycled() || bitmapRect == null || visibleRect.isEmpty()) {
            return;
        }

        if (paint == null) {
            paint = new Paint();
        }

        int saveCount = canvas.save();

        canvas.concat(matrix);
        canvas.drawBitmap(bitmap, bitmapRect, visibleRect, paint);

        canvas.restoreToCount(saveCount);
    }

    public void onDetachedFromWindow() {

    }

    public void setImage(String uri) {
        decoder = new ImageRegionDecoder(imageView.getContext(), uri);
        try {
            decoder.init();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void onDecodeCompleted(Bitmap newBitmap, Rect srcRect, RectF visibleRectF) {
        if (!this.currentSrcRect.equals(srcRect)) {
            return;
        }

        visibleRect.set(visibleRectF.left, visibleRectF.top, visibleRectF.right, visibleRectF.bottom);

        Bitmap oldBitmap = bitmap;
        if (oldBitmap != null) {
            oldBitmap.recycle();
        }

        bitmap = newBitmap;
        bitmapRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        imageView.invalidate();
    }

    public void update(Matrix drawMatrix, RectF visibleRect, int drawableWidth) {
        if (decoder == null || !decoder.isReady() || drawMatrix == null || visibleRect == null || visibleRect.isEmpty() || drawableWidth == 0) {
            return;
        }

        // 抛弃旧的任务
        if (lastTask != null) {
            lastTask.cancel();
            lastTask = null;
        }

        // 立即更新Matrix，一起缩放滑动
        matrix.set(drawMatrix);

        // 根据现实区域计算Src区域
        float scale = (float) decoder.getImageWidth() / drawableWidth;
        Rect newSrcRect = new Rect((int) (visibleRect.left * scale), (int) (visibleRect.top * scale), (int) (visibleRect.right * scale), (int) (visibleRect.bottom * scale));

        // 别超出范围了
        newSrcRect.left = Math.max(0, newSrcRect.left);
        newSrcRect.top = Math.max(0, newSrcRect.top);
        newSrcRect.right = Math.max(0, newSrcRect.right);
        newSrcRect.bottom = Math.max(0, newSrcRect.bottom);
        newSrcRect.left = Math.min(newSrcRect.left, decoder.getImageWidth());
        newSrcRect.top = Math.min(newSrcRect.top, decoder.getImageHeight());
        newSrcRect.right = Math.min(newSrcRect.right, decoder.getImageWidth());
        newSrcRect.bottom = Math.min(newSrcRect.bottom, decoder.getImageHeight());

        if (newSrcRect.isEmpty()) {
            Log.d(Sketch.TAG, NAME + ". update - " +
                    "imageSize=" + decoder.getImageWidth() + "x" + decoder.getImageHeight()
                    + ", visibleRect=" + visibleRect.toString()
                    + ", scale=" + scale
                    + ", newSrcRect=" + newSrcRect.toString());
            return;
        }

        currentSrcRect.set(newSrcRect.left, newSrcRect.top, newSrcRect.right, newSrcRect.bottom);

        int inSampleSize = getSimpleSize(decoder.getContext(), newSrcRect);

        if (Sketch.isDebugMode()) {
            Log.d(Sketch.TAG, NAME + ". update - "
                    + "visibleRect=" + visibleRect.toString()
                    + ", srcRect=" + newSrcRect.toString()
                    + ", inSampleSize=" + inSampleSize
                    + ", imageSize=" + decoder.getImageWidth() + "x" + decoder.getImageHeight()
                    + ", scale=" + scale);
        }

        lastTask = new DecodeRegionImageTask(this, decoder, newSrcRect, new RectF(visibleRect), inSampleSize);
        lastTask.execute(0);
    }
}
