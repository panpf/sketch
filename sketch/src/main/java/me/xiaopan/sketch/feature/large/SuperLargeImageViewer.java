/*
 * Copyright (C) 2016 Peng fei Pan <sky@xiaopan.me>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.util.SketchUtils;

/**
 * 超大图片查看器
 */
// TODO: 16/8/16 再细分成一个一个的小方块
// todo 慢慢滑动的时候就一直触发，快速滑动的时候就停止的时候触发
public class SuperLargeImageViewer {
    private static final String NAME = "SuperLargeImageViewer";

    private Context context;
    private InvalidateCallback invalidateCallback;

    private ImageRegionDecoder imageRegionDecoder;
    private RegionDecodeTask lastTask;

    private Bitmap bitmap;
    private Rect bitmapSrcRect = new Rect();
    private RectF bitmapVisibleRect = new RectF();
    private Paint paint = new Paint();
    private Matrix matrix = new Matrix();

    private UpdateParams waitUpdateParams;
    private UpdateParams updateParams = new UpdateParams();

    public SuperLargeImageViewer(Context context, InvalidateCallback invalidateCallback) {
        this.context = context.getApplicationContext();
        this.invalidateCallback = invalidateCallback;
    }

    private static int calculateRegionInSimpleSize(Context context, Rect regionSrcRect) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int targetWidth = (int) (displayMetrics.widthPixels * 1.5f);
        int targetHeight = (int) (displayMetrics.heightPixels * 1.5f);
        return Sketch.with(context).getConfiguration().getImageSizeCalculator().calculateInSampleSize(
                regionSrcRect.width(), regionSrcRect.height(),
                targetWidth, targetHeight);
    }

    public void draw(Canvas canvas) {
        if (bitmap == null || bitmap.isRecycled() || bitmapSrcRect.isEmpty() || bitmapVisibleRect.isEmpty()) {
            return;
        }

        int saveCount = canvas.save();

        canvas.concat(matrix);
        canvas.drawBitmap(bitmap, bitmapSrcRect, bitmapVisibleRect, paint);

        canvas.restoreToCount(saveCount);
    }

    public void setImage(String imageUri) {
        if (TextUtils.isEmpty(imageUri)) {
            clean();
            return;
        }

        reset();

        ImageRegionDecoderFactory.create(context, imageUri, new ImageRegionDecoderFactory.CreateCallback() {
            @Override
            public void onCreateCompleted(ImageRegionDecoder imageRegionDecoder) {
                SuperLargeImageViewer.this.imageRegionDecoder = imageRegionDecoder;
                if (waitUpdateParams != null && !waitUpdateParams.isEmpty()) {
                    if (Sketch.isDebugMode()) {
                        Log.d(Sketch.TAG, SketchUtils.concat(NAME, " - ", "Dealing waiting update params"));
                    }
                    update(waitUpdateParams);
                    waitUpdateParams.reset();
                }
            }

            @Override
            public void onCreateFailed(Exception e) {

            }
        });
    }

    public void update(UpdateParams updateParams) {
        if (updateParams == null || updateParams.isEmpty()) {
            clean();
            matrix.reset();
            invalidateCallback.invalidate();
            return;
        }

        // 如果解码器尚未初始化完成就缓存当前更新参数
        if (imageRegionDecoder == null || !imageRegionDecoder.isReady()) {
            if (waitUpdateParams == null) {
                waitUpdateParams = new UpdateParams();
            }
            waitUpdateParams.set(updateParams);
            return;
        }

        // 抛弃旧的任务
        if (lastTask != null) {
            lastTask.cancel();
            lastTask = null;
        }

        // 立即更新Matrix，一起缩放滑动
        matrix.set(updateParams.drawMatrix);

        // visible区域适当的大一点儿
        int addWidth = (int) (updateParams.visibleRect.width() * 0.2);
        int addHeight = (int) (updateParams.visibleRect.height() * 0.2);
        RectF largeVisibleRect = new RectF(
                Math.max(0, updateParams.visibleRect.left - addWidth),
                Math.max(0, updateParams.visibleRect.top - addHeight),
                Math.min(updateParams.previewDrawableWidth, updateParams.visibleRect.right + addWidth),
                Math.min(updateParams.previewDrawableHeight, updateParams.visibleRect.bottom + addHeight));

        // 如果是全部显示的话就不解码了
        if (largeVisibleRect.width() == updateParams.previewDrawableWidth && largeVisibleRect.height() == updateParams.previewDrawableHeight) {
            clean();
            invalidateCallback.invalidate();
            return;
        }

        // 如果largeVisibleRect没有变化的话就不解码了
        if (largeVisibleRect.equals(bitmapVisibleRect)) {
            return;
        }

        // 计算显示区域在完整图片中对应的区域
        // 各用个的缩放比例（这很重要），因为宽或高的比例可能不一样
        float widthScale = (float) imageRegionDecoder.getImageWidth() / updateParams.previewDrawableWidth;
        float heightScale = (float) imageRegionDecoder.getImageHeight() / updateParams.previewDrawableHeight;
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
        srcRect.left = Math.min(srcRect.left, imageRegionDecoder.getImageWidth());
        srcRect.top = Math.min(srcRect.top, imageRegionDecoder.getImageHeight());
        srcRect.right = Math.min(srcRect.right, imageRegionDecoder.getImageWidth());
        srcRect.bottom = Math.min(srcRect.bottom, imageRegionDecoder.getImageHeight());

        // 无效的区域不要
        if (srcRect.isEmpty()) {
            if (Sketch.isDebugMode()) {
                Log.d(Sketch.TAG, NAME + ". update - srcRect is empty - " +
                        "imageSize=" + imageRegionDecoder.getImageWidth() + "x" + imageRegionDecoder.getImageHeight()
                        + ", visibleRect=" + updateParams.visibleRect.toString()
                        + ", largeVisibleRect=" + largeVisibleRect.toString()
                        + ", scale=" + widthScale + "x" + heightScale
                        + ", newSrcRect=" + srcRect.toString());
            }
            clean();
            invalidateCallback.invalidate();
            return;
        }

        // 根据src区域大小计算缩放比例
        int inSampleSize = calculateRegionInSimpleSize(context, srcRect);

        if (Sketch.isDebugMode()) {
            Log.d(Sketch.TAG, NAME + ". update - "
                    + "visibleRect=" + updateParams.visibleRect.toString()
                    + ", largeVisibleRect=" + largeVisibleRect.toString()
                    + ", srcRect=" + srcRect.toString()
                    + ", inSampleSize=" + inSampleSize
                    + ", imageSize=" + imageRegionDecoder.getImageWidth() + "x" + imageRegionDecoder.getImageHeight()
                    + ", scale=" + widthScale + "x" + heightScale);
        }

        // 读取图片
        lastTask = new RegionDecodeTask(this, imageRegionDecoder, srcRect, largeVisibleRect, inSampleSize);
        lastTask.execute(0);
    }

    void onDecodeCompleted(Bitmap newBitmap, RectF visibleRect) {
        clean();

        if (newBitmap != null && !newBitmap.isRecycled()) {
            bitmap = newBitmap;
            bitmapSrcRect.set(0, 0, bitmap.getWidth(), bitmap.getHeight());
            bitmapVisibleRect.set(visibleRect.left, visibleRect.top, visibleRect.right, visibleRect.bottom);
        }

        invalidateCallback.invalidate();
    }

    private void clean() {
        Bitmap oldBitmap = bitmap;
        if (oldBitmap != null) {
            oldBitmap.recycle();
        }

        bitmap = null;
        bitmapSrcRect.setEmpty();
        bitmapVisibleRect.setEmpty();
    }

    private void reset() {
        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }

        if (imageRegionDecoder != null) {
            imageRegionDecoder.recycle();
            imageRegionDecoder = null;
        }

        if (waitUpdateParams != null) {
            waitUpdateParams.reset();
        }

        bitmapSrcRect.setEmpty();
        bitmapVisibleRect.setEmpty();
        matrix.reset();
    }

    public void recycle() {
        if (imageRegionDecoder != null) {
            imageRegionDecoder.recycle();
        }
    }

    public UpdateParams getUpdateParams() {
        return updateParams;
    }

    public interface InvalidateCallback {
        void invalidate();
    }

    public static class UpdateParams {
        private Matrix drawMatrix = new Matrix();
        private RectF visibleRect = new RectF();
        private int previewDrawableWidth;
        private int previewDrawableHeight;

        public void set(Matrix drawMatrix, RectF visibleRect, int previewDrawableWidth, int previewDrawableHeight) {
            if (drawMatrix == null || visibleRect == null || visibleRect.isEmpty() || previewDrawableWidth == 0 || previewDrawableHeight == 0) {
                reset();
            } else {
                this.drawMatrix.set(drawMatrix);
                this.visibleRect.set(visibleRect);
                this.previewDrawableWidth = previewDrawableWidth;
                this.previewDrawableHeight = previewDrawableHeight;
            }
        }

        public void set(UpdateParams updateParams) {
            if (updateParams == null) {
                reset();
            } else {
                this.drawMatrix.set(updateParams.drawMatrix);
                this.visibleRect.set(updateParams.visibleRect);
                this.previewDrawableWidth = updateParams.previewDrawableWidth;
                this.previewDrawableHeight = updateParams.previewDrawableHeight;
            }
        }

        public boolean isEmpty() {
            return visibleRect.isEmpty() || previewDrawableWidth == 0 || previewDrawableHeight == 0;
        }

        public void reset() {
            drawMatrix.reset();
            visibleRect.setEmpty();
            previewDrawableWidth = 0;
            previewDrawableHeight = 0;
        }

        public Matrix getDrawMatrix() {
            return drawMatrix;
        }

        public RectF getVisibleRect() {
            return visibleRect;
        }

        public void setPreviewDrawableHeight(int previewDrawableHeight) {
            this.previewDrawableHeight = previewDrawableHeight;
        }

        public void setPreviewDrawableWidth(int previewDrawableWidth) {
            this.previewDrawableWidth = previewDrawableWidth;
        }
    }
}
