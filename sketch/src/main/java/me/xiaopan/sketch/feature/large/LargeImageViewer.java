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
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.text.TextUtils;

import java.util.List;

import me.xiaopan.sketch.SLogType;
import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.SLog;
import me.xiaopan.sketch.cache.BitmapPoolUtils;
import me.xiaopan.sketch.decode.ImageType;
import me.xiaopan.sketch.util.SketchUtils;

/**
 * 大图片查看器
 */
// TODO: 2017/5/8 重新规划设计大图查看器的实现，感觉现在的有些乱（初始化，解码，显示分离）
public class LargeImageViewer {
    private static final String NAME = "LargeImageViewer";

    private Context context;
    private Callback callback;

    private TileExecutor tileExecutor;
    private TileDecoder tileDecoder;
    private TileManager tileManager;

    private boolean showTileRect;
    private float zoomScale;
    private float lastZoomScale;
    private Paint drawTilePaint;
    private Paint drawTileRectPaint;
    private Paint drawLoadingTileRectPaint;
    private Matrix matrix;

    private boolean running;
    private boolean paused;
    private String imageUri;

    public LargeImageViewer(Context context, Callback callback) {
        context = context.getApplicationContext();
        this.context = context;
        this.callback = callback;

        this.tileExecutor = new TileExecutor(new ExecutorCallback());
        this.tileManager = new TileManager(context, this);
        this.tileDecoder = new TileDecoder(this);

        this.matrix = new Matrix();
        this.drawTilePaint = new Paint();
    }

    void draw(Canvas canvas) {
        if (tileManager.tileList != null && tileManager.tileList.size() > 0) {
            int saveCount = canvas.save();
            canvas.concat(matrix);

            for (Tile tile : tileManager.tileList) {
                if (!tile.isEmpty()) {
                    canvas.drawBitmap(tile.bitmap, tile.bitmapDrawSrcRect, tile.drawRect, drawTilePaint);
                    if (showTileRect) {
                        if (drawTileRectPaint == null) {
                            drawTileRectPaint = new Paint();
                            drawTileRectPaint.setColor(Color.parseColor("#88FF0000"));
                        }
                        canvas.drawRect(tile.drawRect, drawTileRectPaint);
                    }
                } else if (!tile.isDecodeParamEmpty()) {
                    if (showTileRect) {
                        if (drawLoadingTileRectPaint == null) {
                            drawLoadingTileRectPaint = new Paint();
                            drawLoadingTileRectPaint.setColor(Color.parseColor("#880000FF"));
                        }
                        canvas.drawRect(tile.drawRect, drawLoadingTileRectPaint);
                    }
                }
            }

            canvas.restoreToCount(saveCount);
        }
    }

    /**
     * 设置新的图片
     */
    void setImage(String imageUri, boolean correctImageOrientation) {
        clean("setImage");

        this.imageUri = imageUri;
        this.running = !TextUtils.isEmpty(imageUri);
        this.tileDecoder.setImage(imageUri, correctImageOrientation);
    }

    /**
     * 更新
     */
    void update(Matrix drawMatrix, Rect newVisibleRect, Point previewDrawableSize, Point imageViewSize, boolean zooming) {
        // 没有准备好就不往下走了
        if (!isReady()) {
            SLog.w(SLogType.LARGE, NAME, "not ready. %s", imageUri);
            return;
        }

        // 暂停中也不走了
        if (paused) {
            SLog.w(SLogType.LARGE, NAME, "not resuming. %s", imageUri);
            return;
        }

        // 传进来的参数不能用就什么也不显示
        if (newVisibleRect.isEmpty() || previewDrawableSize.x == 0 || previewDrawableSize.y == 0 || imageViewSize.x == 0 || imageViewSize.y == 0) {
            SLog.w(SLogType.LARGE, NAME, "update params is empty. update. newVisibleRect=%s, previewDrawableSize=%dx%d, imageViewSize=%dx%d. %s",
                    newVisibleRect.toShortString(), previewDrawableSize.x, previewDrawableSize.y, imageViewSize.x, imageViewSize.y, imageUri);
            clean("update param is empty");
            return;
        }

        // 如果当前完整显示预览图的话就清空什么也不显示
        if (newVisibleRect.width() == previewDrawableSize.x && newVisibleRect.height() == previewDrawableSize.y) {
            SLog.d(SLogType.LARGE, NAME, "full display. update. newVisibleRect=%s. %s",
                    newVisibleRect.toShortString(), imageUri);
            clean("full display");
            return;
        }

        // 更新Matrix
        lastZoomScale = zoomScale;
        matrix.set(drawMatrix);
        zoomScale = SketchUtils.formatFloat(SketchUtils.getMatrixScale(matrix), 2);

        callback.invalidate();

        tileManager.update(newVisibleRect, previewDrawableSize, imageViewSize, getImageSize(), zooming);
    }

    /**
     * 清理资源（不影响继续使用）
     */
    private void clean(String why) {
        tileExecutor.cleanDecode(why);

        matrix.reset();
        lastZoomScale = 0;
        zoomScale = 0;

        tileManager.clean(why);

        callback.invalidate();
    }

    /**
     * 回收资源（回收后需要重新setImage()才能使用）
     */
    void recycle(String why) {
        running = false;
        clean(why);
        tileExecutor.recycle(why);
        tileManager.recycle(why);
        tileDecoder.recycle(why);
    }

    void invalidateView() {
        callback.invalidate();
    }

    TileDecoder getTileDecoder() {
        return tileDecoder;
    }

    TileExecutor getTileExecutor() {
        return tileExecutor;
    }

    /**
     * 暂停
     */
    public void setPause(boolean pause) {
        if (pause == paused) {
            return;
        }
        paused = pause;

        if (paused) {
            SLog.w(SLogType.LARGE, NAME, "pause. %s", imageUri);

            if (running) {
                clean("pause");
            }
        } else {
            SLog.i(SLogType.LARGE, NAME, "resume. %s", imageUri);

            if (running) {
                callback.updateMatrix();
            }
        }
    }

    @SuppressWarnings("unused")
    public boolean isPaused() {
        return paused;
    }

    /**
     * 工作中？
     */
    @SuppressWarnings("unused")
    public boolean isWorking() {
        return !TextUtils.isEmpty(imageUri);
    }

    /**
     * 准备好了？
     */
    public boolean isReady() {
        return running && tileDecoder.isReady();
    }

    /**
     * 初始化中？
     */
    public boolean isInitializing() {
        return running && tileDecoder.isInitializing();
    }

    /**
     * 是否显示碎片的范围（红色表示已加载，蓝色表示正在加载）
     */
    public boolean isShowTileRect() {
        return showTileRect;
    }

    /**
     * 设置是否显示碎片的范围（红色表示已加载，蓝色表示正在加载）
     */
    @SuppressWarnings("unused")
    public void setShowTileRect(boolean showTileRect) {
        this.showTileRect = showTileRect;
        callback.invalidate();
    }

    /**
     * 获取当前缩放比例
     */
    public float getZoomScale() {
        return zoomScale;
    }

    /**
     * 获取上次的缩放比例
     */
    public float getLastZoomScale() {
        return lastZoomScale;
    }

    /**
     * 获取图片的尺寸
     */
    public Point getImageSize() {
        return tileDecoder.isReady() ? tileDecoder.getDecoder().getImageSize() : null;
    }

    /**
     * 获取图片的类型
     */
    @SuppressWarnings("unused")
    public ImageType getImageType() {
        return tileDecoder.isReady() ? tileDecoder.getDecoder().getImageType() : null;
    }

    /**
     * 获取图片URI
     */
    public String getImageUri() {
        return imageUri;
    }

    /**
     * 获取绘制区域
     */
    @SuppressWarnings("unused")
    public Rect getDrawRect() {
        return tileManager.drawRect;
    }

    /**
     * 获取绘制区域在原图中对应的位置
     */
    public Rect getDrawSrcRect() {
        return tileManager.drawSrcRect;
    }

    /**
     * 获取解码区域
     */
    public Rect getDecodeRect() {
        return tileManager.decodeRect;
    }

    /**
     * 获取解码区域在原图中对应的位置
     */
    public Rect getDecodeSrcRect() {
        return tileManager.decodeSrcRect;
    }

    /**
     * 获取碎片列表
     */
    public List<Tile> getTileList() {
        return tileManager.tileList;
    }

    /**
     * 获取碎片基数，例如碎片基数是3时，就将绘制区域分割成一个(3+1)x(3+1)=16个方块
     */
    public int getTiles() {
        return tileManager.tiles;
    }

    /**
     * 获取碎片变化监听器
     */
    @SuppressWarnings("unused")
    public OnTileChangedListener getOnTileChangedListener() {
        return tileManager.onTileChangedListener;
    }

    /**
     * 获取碎片变化监听器
     */
    public void setOnTileChangedListener(LargeImageViewer.OnTileChangedListener onTileChangedListener) {
        tileManager.onTileChangedListener = onTileChangedListener;
    }

    /**
     * 获取碎片占用的内存，单位字节
     */
    @SuppressWarnings("unused")
    public long getTilesAllocationByteCount() {
        if (tileManager.tileList == null || tileManager.tileList.size() <= 0) {
            return 0;
        }

        long bytes = 0;
        for (Tile tile : tileManager.tileList) {
            if (!tile.isEmpty()) {
                bytes += SketchUtils.getByteCount(tile.bitmap);
            }
        }
        return bytes;
    }

    public interface Callback {
        void invalidate();

        void updateMatrix();
    }

    public interface OnTileChangedListener {
        void onTileChanged(LargeImageViewer largeImageViewer);
    }

    private class ExecutorCallback implements TileExecutor.Callback {

        @Override
        public Context getContext() {
            return context;
        }

        @Override
        public void onInitCompleted(String imageUri, ImageRegionDecoder decoder) {
            if (!running) {
                SLog.w(SLogType.LARGE, NAME, "stop running. initCompleted. %s", imageUri);
                return;
            }

            tileDecoder.initCompleted(imageUri, decoder);

            callback.updateMatrix();
        }

        @Override
        public void onInitError(String imageUri, Exception e) {
            if (!running) {
                SLog.w(SLogType.LARGE, NAME, "stop running. initError. %s", imageUri);
                return;
            }

            tileDecoder.initError(imageUri, e);
        }

        @Override
        public void onDecodeCompleted(Tile tile, Bitmap bitmap, int useTime) {
            if (!running) {
                SLog.w(SLogType.LARGE, NAME, "stop running. decodeCompleted. tile=%s", tile.getInfo());
                BitmapPoolUtils.freeBitmapToPoolForRegionDecoder(bitmap, Sketch.with(context).getConfiguration().getBitmapPool());
                return;
            }

            tileManager.decodeCompleted(tile, bitmap, useTime);
        }

        @Override
        public void onDecodeError(Tile tile, DecodeHandler.DecodeErrorException exception) {
            if (!running) {
                SLog.w(SLogType.LARGE, NAME, "stop running. decodeError. tile=%s", tile.getInfo());
                return;
            }

            tileManager.decodeError(tile, exception);
        }
    }
}
