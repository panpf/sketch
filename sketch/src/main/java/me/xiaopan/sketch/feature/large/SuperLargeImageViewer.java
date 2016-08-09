package me.xiaopan.sketch.feature.large;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;

import me.xiaopan.sketch.Sketch;

/**
 * 超大图片查看器
 */
public class SuperLargeImageViewer {
    private static final String NAME = "SuperLargeImageViewer";

    private ImageView imageView;

    private ImageRegionDecoder decoder;
    private RegionDecodeTask lastTask;

    private Bitmap bitmap;
    private Rect bitmapSrcRect = new Rect();
    private RectF bitmapVisibleRect = new RectF();
    private Paint paint = new Paint();
    private Matrix matrix = new Matrix();

    public SuperLargeImageViewer(ImageView imageView) {
        this.imageView = imageView;
    }

    private static int getSimpleSize(Context context, Rect srcRect) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int targetWidth = (int) (displayMetrics.widthPixels * 1.5f);
        int targetHeight = (int) (displayMetrics.heightPixels * 1.5f);
        return Sketch.with(context).getConfiguration().getImageSizeCalculator().calculateInSampleSize(
                srcRect.width(), srcRect.height(),
                targetWidth, targetHeight);
    }

    public void onAttachedToWindow() {

    }

    public void onLayout(boolean changed, int left, int top, int right, int bottom) {

    }

    public void onDraw(Canvas canvas) {
        if (bitmap == null || bitmap.isRecycled() || bitmapSrcRect.isEmpty() || bitmapVisibleRect.isEmpty()) {
            return;
        }

        int saveCount = canvas.save();

        canvas.concat(matrix);
        canvas.drawBitmap(bitmap, bitmapSrcRect, bitmapVisibleRect, paint);

        canvas.restoreToCount(saveCount);
    }

    public void onDetachedFromWindow() {

    }

    private void reset() {
        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }

        if (decoder != null) {
            decoder.recycle();
            decoder = null;
        }

        bitmapSrcRect.setEmpty();
        bitmapVisibleRect.setEmpty();
        matrix.reset();
    }

    private void cleanBitmap() {
        Bitmap oldBitmap = bitmap;
        if (oldBitmap != null) {
            oldBitmap.recycle();
        }

        bitmap = null;
        bitmapSrcRect.setEmpty();
        bitmapVisibleRect.setEmpty();
    }

    private void invalidate() {
        if (imageView != null) {
            imageView.invalidate();
        }
    }

    public void setImage(String uri) {
        if (uri == null) {
            cleanBitmap();
            return;
        }

        if (decoder != null && uri.equals(decoder.getImageUri())) {
            return;
        }

        reset();

        if (!TextUtils.isEmpty(uri)) {
            decoder = new ImageRegionDecoder(imageView.getContext(), uri);
            try {
                decoder.init();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        invalidate();
    }

    // todo 这个触发点，要再优化优化，比如说慢慢滑动的时候就一直触发，快速滑动的时候就停止的时候触发
    public void update(Matrix drawMatrix, RectF visibleRect, int drawableWidth, int drawableHeight) {
        if (decoder == null || !decoder.isReady() || drawMatrix == null || visibleRect == null || visibleRect.isEmpty() || drawableWidth == 0 || drawableHeight == 0) {
            cleanBitmap();
            matrix.reset();
            invalidate();
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
        int addWidth = (int) (visibleRect.width() * 0.2);
        int addHeight = (int) (visibleRect.height() * 0.2);
        RectF largeVisibleRect = new RectF(
                Math.max(0, visibleRect.left - addWidth),
                Math.max(0, visibleRect.top - addHeight),
                Math.min(drawableWidth, visibleRect.right + addWidth),
                Math.min(drawableHeight, visibleRect.bottom + addHeight));

        // 如果是全部显示的话就不解码了
        if (largeVisibleRect.width() == drawableWidth && largeVisibleRect.height() == drawableHeight) {
            cleanBitmap();
            invalidate();
            return;
        }

        // 如果largeVisibleRect没有变化的话就不解码了
        if (largeVisibleRect.equals(bitmapVisibleRect)) {
            return;
        }

        // 计算显示区域在完整图片中对应的区域
        // 各用个的缩放比例（这很重要），因为宽或高的比例可能不一样
        float widthScale = (float) decoder.getImageWidth() / drawableWidth;
        float heightScale = (float) decoder.getImageHeight() / drawableHeight;
        Rect srcRect = new Rect(
                (int) (largeVisibleRect.left * widthScale),
                (int) (largeVisibleRect.top * heightScale),
                (int) (largeVisibleRect.right * widthScale),
                (int) (largeVisibleRect.bottom * heightScale));

        // 别超出范围了
        srcRect.left = Math.max(0, srcRect.left);
        srcRect.top = Math.max(0, srcRect.top);
        srcRect.right = Math.max(0, srcRect.right);
        srcRect.bottom = Math.max(0, srcRect.bottom);
        srcRect.left = Math.min(srcRect.left, decoder.getImageWidth());
        srcRect.top = Math.min(srcRect.top, decoder.getImageHeight());
        srcRect.right = Math.min(srcRect.right, decoder.getImageWidth());
        srcRect.bottom = Math.min(srcRect.bottom, decoder.getImageHeight());

        // 无效的区域不要
        if (srcRect.isEmpty()) {
            if (Sketch.isDebugMode()) {
                Log.d(Sketch.TAG, NAME + ". update - srcRect is empty - " +
                        "imageSize=" + decoder.getImageWidth() + "x" + decoder.getImageHeight()
                        + ", visibleRect=" + visibleRect.toString()
                        + ", largeVisibleRect=" + largeVisibleRect.toString()
                        + ", scale=" + widthScale + "x" + heightScale
                        + ", newSrcRect=" + srcRect.toString());
            }
            cleanBitmap();
            invalidate();
            return;
        }

        // 根据src区域大小计算缩放比例
        int inSampleSize = getSimpleSize(decoder.getContext(), srcRect);

        if (Sketch.isDebugMode()) {
            Log.d(Sketch.TAG, NAME + ". update - "
                    + "visibleRect=" + visibleRect.toString()
                    + ", largeVisibleRect=" + largeVisibleRect.toString()
                    + ", srcRect=" + srcRect.toString()
                    + ", inSampleSize=" + inSampleSize
                    + ", imageSize=" + decoder.getImageWidth() + "x" + decoder.getImageHeight()
                    + ", scale=" + widthScale + "x" + heightScale);
        }

        // 读取图片
        lastTask = new RegionDecodeTask(this, decoder, srcRect, largeVisibleRect, inSampleSize);
        lastTask.execute(0);
    }

    void onDecodeCompleted(Bitmap newBitmap, RectF visibleRect) {
        cleanBitmap();

        if (newBitmap != null && !newBitmap.isRecycled()) {
            bitmap = newBitmap;
            bitmapSrcRect.set(0, 0, bitmap.getWidth(), bitmap.getHeight());
            bitmapVisibleRect.set(visibleRect.left, visibleRect.top, visibleRect.right, visibleRect.bottom);
        }

        invalidate();
    }
}
