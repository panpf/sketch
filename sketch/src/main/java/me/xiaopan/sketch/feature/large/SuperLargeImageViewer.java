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
import android.util.Log;

import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.feature.ImageSizeCalculator;
import me.xiaopan.sketch.util.SketchUtils;

/**
 * 超大图片查看器
 */
// TODO: 16/8/16 再细分成一个一个的小方块
public class SuperLargeImageViewer {
    private static final String NAME = "SuperLargeImageViewer";

    private Context context;
    private Callback callback;

    private ImageRegionDecodeExecutor executor;

    private Bitmap bitmap;
    private Rect bitmapSrcRect = new Rect();
    private RectF bitmapVisibleRect = new RectF();
    private Paint paint = new Paint();
    private Matrix matrix = new Matrix();
    private float scale;

    private UpdateParams waitUpdateParams;
    private UpdateParams updateParams = new UpdateParams();
    private RectF lastVisibleRect;
    private boolean running;

    public SuperLargeImageViewer(Context context, Callback callback) {
        this.context = context.getApplicationContext();
        this.callback = callback;
        this.executor = new ImageRegionDecodeExecutor(new ExecutorCallback());
    }

    public void draw(Canvas canvas) {
        if (running && bitmap == null || bitmap.isRecycled() || bitmapSrcRect.isEmpty() || bitmapVisibleRect.isEmpty()) {
            return;
        }

        int saveCount = canvas.save();
        canvas.concat(matrix);
        canvas.drawBitmap(bitmap, bitmapSrcRect, bitmapVisibleRect, paint);
        canvas.restoreToCount(saveCount);
    }

    public void setImage(String imageUri) {
        clean();

        if (!TextUtils.isEmpty(imageUri)) {
            running = true;
            executor.initDecoder(imageUri);
        } else {
            running = false;
            executor.initDecoder(null);
        }
    }

    private void clean() {
        executor.clean();

        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
        if (!bitmapSrcRect.isEmpty()) {
            bitmapSrcRect.setEmpty();
        }
        if (!bitmapVisibleRect.isEmpty()) {
            bitmapVisibleRect.setEmpty();
        }
        if (waitUpdateParams != null) {
            waitUpdateParams.reset();
        }
        matrix.reset();
        scale = SketchUtils.getMatrixScale(matrix);

        callback.invalidate();
    }

    public void update(UpdateParams updateParams) {
        // 不可用，也没有初始化就直接结束
        if (!isAvailable() && !isInitializing()) {
            return;
        }

        // 传进来的参数不能用就什么也不显示
        if (updateParams == null || updateParams.isEmpty()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME + ". update. params is empty");
            }
            clean();
            return;
        }

        // 如果正在初始化就就缓存当前更新参数
        if (!isAvailable() && isInitializing()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME + ". update. initializing. Wait a minute!");
            }
            if (waitUpdateParams == null) {
                waitUpdateParams = new UpdateParams();
            }
            waitUpdateParams.set(updateParams);
            return;
        }

        // 过滤掉重复的刷新
        if (lastVisibleRect != null && lastVisibleRect.equals(updateParams.visibleRect)) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME + ". update. repeated. visibleRect=" + updateParams.visibleRect.toString());
            }
            return;
        }
        if (lastVisibleRect == null) {
            lastVisibleRect = new RectF();
        }
        lastVisibleRect.set(updateParams.visibleRect);

        // 将显示区域加大一点儿
        int previewImageWidth = updateParams.previewDrawableWidth;
        int previewImageHeight = updateParams.previewDrawableHeight;
        int addWidth = (int) (updateParams.visibleRect.width() * 0.2);
        int addHeight = (int) (updateParams.visibleRect.height() * 0.2);
        RectF finalVisibleRect = new RectF(
                Math.max(0, updateParams.visibleRect.left - addWidth),
                Math.max(0, updateParams.visibleRect.top - addHeight),
                Math.min(previewImageWidth, updateParams.visibleRect.right + addWidth),
                Math.min(previewImageHeight, updateParams.visibleRect.bottom + addHeight));

        // 如果全部显示的话就清空什么也不显示
        if (finalVisibleRect.width() == previewImageWidth
                && finalVisibleRect.height() == previewImageHeight) {
            if (Sketch.isDebugMode()) {
                Log.d(Sketch.TAG, NAME + ". update. full display");
            }
            clean();
            return;
        }

        // 显示区域没有变化就啥也不用干
        if (finalVisibleRect.equals(bitmapVisibleRect)) {
            if (Sketch.isDebugMode()) {
                Log.d(Sketch.TAG, NAME + ". update. there is no change");
            }

            // 取消旧的解码任务
            executor.cancelDecode(true);
            return;
        }

        // 计算显示区域在完整图片中对应的区域，重点是各用各的缩放比例（这很重要），因为宽或高的比例可能不一样
        int originImageWidth = executor.getDecoder().getImageWidth();
        int originImageHeight = executor.getDecoder().getImageHeight();
        float widthScale = (float) originImageWidth / previewImageWidth;
        float heightScale = (float) originImageHeight / previewImageHeight;
        Rect srcRect = new Rect(
                (int) (finalVisibleRect.left * widthScale),
                (int) (finalVisibleRect.top * heightScale),
                (int) (finalVisibleRect.right * widthScale),
                (int) (finalVisibleRect.bottom * heightScale));

        // 别超出范围了
        srcRect.left = Math.min(Math.max(0, srcRect.left), originImageWidth);
        srcRect.top = Math.min(Math.max(0, srcRect.top), originImageHeight);
        srcRect.right = Math.min(Math.max(0, srcRect.right), originImageWidth);
        srcRect.bottom = Math.min(Math.max(0, srcRect.bottom), originImageHeight);

        // 无效的区域不要
        if (srcRect.isEmpty()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME + ". update" +
                        ". srcRect is empty" +
                        ". imageSize=" + executor.getDecoder().getImageWidth() + "x" + executor.getDecoder().getImageHeight() +
                        ", visibleRect=" + updateParams.visibleRect.toString() +
                        ", largeVisibleRect=" + finalVisibleRect.toString() +
                        ", scale=" + widthScale + "x" + heightScale +
                        ", newSrcRect=" + srcRect.toString());
            }
            clean();
            return;
        }

        // 更新Matrix
        matrix.set(updateParams.drawMatrix);
        scale = SketchUtils.getMatrixScale(matrix);

        callback.invalidate();

        // 取消旧的任务
        executor.cancelDecode(false);

        // 根据src区域大小计算缩放比例
        int srcWidth = srcRect.width();
        int srcHeight = srcRect.height();
        int targetWidth = (int) (updateParams.imageViewWidth * 1.4f);
        int targetHeight = (int) (updateParams.imageViewHeight * 1.4f);
        ImageSizeCalculator imageSizeCalculator = Sketch.with(context).getConfiguration().getImageSizeCalculator();
        int inSampleSize = imageSizeCalculator.calculateInSampleSize(srcWidth, srcHeight, targetWidth, targetHeight, false);

        if (Sketch.isDebugMode()) {
            Log.d(Sketch.TAG, NAME + ". update" +
                    ". visibleRect=" + finalVisibleRect.toString() +
                    ", inSampleSize=" + inSampleSize +
                    ", srcRect=" + srcRect.toString() +
                    ", targetSize=" + targetWidth + "x" + targetHeight);
        }

        // 提交解码请求
        executor.submit(srcRect, inSampleSize, finalVisibleRect, scale);
    }

    public void recycle() {
        running = false;
        executor.recycle();
    }

    public UpdateParams getUpdateParams() {
        return updateParams;
    }

    public boolean isAvailable() {
        return running && executor.isReady();
    }

    public boolean isInitializing() {
        return running && executor.isInitializing();
    }

    public interface Callback {
        void invalidate();
    }

    private class ExecutorCallback implements ImageRegionDecodeExecutor.Callback {

        @Override
        public Context getContext() {
            return context;
        }

        @Override
        public void onInitCompleted() {
            if (!running) {
                if (Sketch.isDebugMode()) {
                    Log.w(Sketch.TAG, NAME + ". stop running. initCompleted");
                }
                return;
            }

            if (waitUpdateParams != null && !waitUpdateParams.isEmpty()) {
                if (Sketch.isDebugMode()) {
                    Log.d(Sketch.TAG, NAME + ". initCompleted. Dealing waiting update params");
                }

                UpdateParams updateParams = new UpdateParams();
                updateParams.set(waitUpdateParams);
                waitUpdateParams.reset();

                update(updateParams);
            }
        }

        @Override
        public void onInitFailed(Exception e) {
            if (!running) {
                if (Sketch.isDebugMode()) {
                    Log.w(Sketch.TAG, NAME + ". stop running. initFailed");
                }
                return;
            }

            if (Sketch.isDebugMode()) {
                Log.d(Sketch.TAG, NAME + ". initFailed");
            }
        }

        @Override
        public void onDecodeCompleted(Rect srcRect, int inSampleSize, Bitmap newBitmap, RectF visibleRect, float scale) {
            if (!running) {
                if (Sketch.isDebugMode()) {
                    Log.w(Sketch.TAG, NAME +
                            ". stop running" +
                            ". decodeCompleted" +
                            ". visibleRect=" + visibleRect.toString() +
                            ", inSampleSize=" + inSampleSize);
                }
                newBitmap.recycle();
                return;
            }

            if (newBitmap == null || newBitmap.isRecycled()) {
                if (Sketch.isDebugMode()) {
                    Log.w(Sketch.TAG, NAME +
                            ". new bitmap recycled" +
                            ". decodeCompleted" +
                            ". visibleRect=" + visibleRect.toString() +
                            ", inSampleSize=" + inSampleSize);
                }
                return;
            }

            if (SuperLargeImageViewer.this.scale != scale) {
                if (Sketch.isDebugMode()) {
                    Log.w(Sketch.TAG, NAME +
                            ". scale changed" +
                            ". decodeCompleted" +
                            ". visibleRect=" + visibleRect.toString() +
                            ", inSampleSize=" + inSampleSize);
                }
                newBitmap.recycle();
                return;
            }

            if (Sketch.isDebugMode()) {
                Bitmap.Config newBitmapConfig = newBitmap.getConfig();
                Log.i(Sketch.TAG, NAME +
                        ". show image region" +
                        ". decodeCompleted" +
                        ". visibleRect=" + visibleRect.toString() +
                        ", inSampleSize=" + inSampleSize +
                        ", srcRect=" + srcRect.toString() +
                        ", originImageSize=" + executor.getDecoder().getImageWidth() + "x" + executor.getDecoder().getImageHeight() +
                        ", bitmapSize=" + newBitmap.getWidth() + "x" + newBitmap.getHeight() +
                        ", bitmapConfig=" + (newBitmapConfig != null ? newBitmapConfig.name() : null));
            }

            Bitmap oldBitmap = bitmap;
            bitmap = newBitmap;
            bitmapSrcRect.set(0, 0, bitmap.getWidth(), bitmap.getHeight());
            bitmapVisibleRect.set(visibleRect.left, visibleRect.top, visibleRect.right, visibleRect.bottom);

            callback.invalidate();

            if (oldBitmap != null) {
                oldBitmap.recycle();
            }
        }
    }
}
