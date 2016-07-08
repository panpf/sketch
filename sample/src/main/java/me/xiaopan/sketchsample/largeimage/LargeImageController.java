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
    private Paint paint = new Paint();
    private DecodeRegionImageTask lastTask;

    public LargeImageController(ImageView imageView) {
        this.imageView = imageView;
    }

    private static int getSimpleSize(Context context, Rect srcRect) {
        // TODO 这个算法还要再调一调，现在缩放比例还有有点儿小
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
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME + ". onDraw. failed");
            }
            return;
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
            if (Sketch.isDebugMode()) {
                Log.d(Sketch.TAG, NAME + ". onDecodeCompleted. src not match");
            }
            return;
        }

        Bitmap oldBitmap = bitmap;
        if (oldBitmap != null) {
            oldBitmap.recycle();
        }

        visibleRect.set(visibleRectF.left, visibleRectF.top, visibleRectF.right, visibleRectF.bottom);

        bitmap = newBitmap;
        bitmapRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        imageView.invalidate();
    }

    // todo 这个触发点，要再优化优化，比如说慢慢滑动的时候就一直触发，快速滑动的时候就停止的时候触发
    public void update(Matrix drawMatrix, RectF visibleRect, int drawableWidth, int drawableHeight) {
        if (decoder == null || !decoder.isReady() || drawMatrix == null || visibleRect == null || visibleRect.isEmpty() || drawableWidth == 0 || drawableHeight == 0) {
            return;
        }

        // 抛弃旧的任务
        if (lastTask != null) {
            lastTask.cancel();
            lastTask = null;
        }

        // 立即更新Matrix，一起缩放滑动
        matrix.set(drawMatrix);

        // visible区域适当的大一点儿
        int addWidth = (int) (visibleRect.width() * 0.12);
        int addHeight = (int) (visibleRect.height() * 0.2);
        RectF largeVisibleRect = new RectF(
                Math.max(0, visibleRect.left - addWidth),
                Math.max(0, visibleRect.top - addHeight),
                Math.min(drawableWidth, visibleRect.right + addWidth),
                Math.min(drawableHeight, visibleRect.bottom + addHeight));

        // 计算显示区域在完整图片中对应的区域
        // 各用个的缩放比例（这很重要），因为宽或高的比例可能不一样
        float widthScale = (float) decoder.getImageWidth() / drawableWidth;
        float heightScale = (float) decoder.getImageHeight() / drawableHeight;
        Rect newSrcRect = new Rect(
                (int) (largeVisibleRect.left * widthScale),
                (int) (largeVisibleRect.top * heightScale),
                (int) (largeVisibleRect.right * widthScale),
                (int) (largeVisibleRect.bottom * heightScale));

        // 别超出范围了
        newSrcRect.left = Math.max(0, newSrcRect.left);
        newSrcRect.top = Math.max(0, newSrcRect.top);
        newSrcRect.right = Math.max(0, newSrcRect.right);
        newSrcRect.bottom = Math.max(0, newSrcRect.bottom);
        newSrcRect.left = Math.min(newSrcRect.left, decoder.getImageWidth());
        newSrcRect.top = Math.min(newSrcRect.top, decoder.getImageHeight());
        newSrcRect.right = Math.min(newSrcRect.right, decoder.getImageWidth());
        newSrcRect.bottom = Math.min(newSrcRect.bottom, decoder.getImageHeight());

        // 无效的区域不要
        if (newSrcRect.isEmpty()) {
            if (Sketch.isDebugMode()) {
                Log.d(Sketch.TAG, NAME + ". update - " +
                        "imageSize=" + decoder.getImageWidth() + "x" + decoder.getImageHeight()
                        + ", visibleRect=" + visibleRect.toString()
                        + ", largeVisibleRect=" + largeVisibleRect.toString()
                        + ", scale=" + widthScale + "x" + heightScale
                        + ", newSrcRect=" + newSrcRect.toString());
            }
            return;
        }

        // 立马记住当前区域为了后续比较用
        currentSrcRect.set(newSrcRect.left, newSrcRect.top, newSrcRect.right, newSrcRect.bottom);

        // 根据src区域大小计算缩放比例
        int inSampleSize = getSimpleSize(decoder.getContext(), newSrcRect);

        if (Sketch.isDebugMode()) {
            Log.d(Sketch.TAG, NAME + ". update - "
                    + "visibleRect=" + visibleRect.toString()
                    + ", largeVisibleRect=" + largeVisibleRect.toString()
                    + ", srcRect=" + newSrcRect.toString()
                    + ", inSampleSize=" + inSampleSize
                    + ", imageSize=" + decoder.getImageWidth() + "x" + decoder.getImageHeight()
                    + ", scale=" + widthScale + "x" + heightScale);
        }

        // 读取图片
        lastTask = new DecodeRegionImageTask(this, decoder, newSrcRect, largeVisibleRect, inSampleSize);
        lastTask.execute(0);
    }
}
