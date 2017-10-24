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

package me.xiaopan.sketch.zoom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.widget.ImageView;

import java.util.List;

import me.xiaopan.sketch.SLog;
import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.cache.BitmapPoolUtils;
import me.xiaopan.sketch.decode.ImageType;
import me.xiaopan.sketch.drawable.SketchDrawable;
import me.xiaopan.sketch.drawable.SketchLoadingDrawable;
import me.xiaopan.sketch.util.SketchUtils;
import me.xiaopan.sketch.viewfun.FunctionPropertyView;
import me.xiaopan.sketch.zoom.block.ImageRegionDecoder;
import me.xiaopan.sketch.zoom.block.Tile;
import me.xiaopan.sketch.zoom.block.TileDecodeHandler;
import me.xiaopan.sketch.zoom.block.TileDecoder;
import me.xiaopan.sketch.zoom.block.TileExecutor;
import me.xiaopan.sketch.zoom.block.TileManager;

/**
 * 对于超大图片，分块显示可见区域
 */
// TODO: 2017/5/8 重新规划设计大图查看器的实现，感觉现在的有些乱（初始化，解码，显示分离）
public class BlockDisplayer {
    private static final String NAME = "HugeImageViewer";

    private Context context;
    private ImageZoomer imageZoomer;

    private Matrix tempDrawMatrix;
    private Rect tempVisibleRect;

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

    public BlockDisplayer(Context context, ImageZoomer imageZoomer) {
        context = context.getApplicationContext();
        this.context = context;
        this.imageZoomer = imageZoomer;

        this.tileExecutor = new TileExecutor(new ExecutorCallback());
        this.tileManager = new TileManager(context, this);
        this.tileDecoder = new TileDecoder(this);

        this.matrix = new Matrix();
        this.drawTilePaint = new Paint();

        if (!SketchUtils.sdkSupportBitmapRegionDecoder()) {
            SLog.e(NAME, "huge image function the minimum support to GINGERBREAD_MR1");
        }
    }


    /* -----------主要方法----------- */


    public void reset() {
        if (!SketchUtils.sdkSupportBitmapRegionDecoder()) {
            return;
        }

        ImageView imageView = imageZoomer.getImageView();

        Drawable previewDrawable = SketchUtils.getLastDrawable(imageZoomer.getImageView().getDrawable());
        SketchDrawable sketchDrawable = null;
        boolean drawableQualified = false;
        if (previewDrawable != null && previewDrawable instanceof SketchDrawable && !(previewDrawable instanceof SketchLoadingDrawable)) {
            sketchDrawable = (SketchDrawable) previewDrawable;
            final int previewWidth = previewDrawable.getIntrinsicWidth();
            final int previewHeight = previewDrawable.getIntrinsicHeight();
            final int imageWidth = sketchDrawable.getOriginWidth();
            final int imageHeight = sketchDrawable.getOriginHeight();

            drawableQualified = previewWidth < imageWidth || previewHeight < imageHeight;
            drawableQualified &= SketchUtils.sdkSupportBitmapRegionDecoder();
            drawableQualified &= SketchUtils.formatSupportBitmapRegionDecoder(ImageType.valueOfMimeType(sketchDrawable.getMimeType()));

            if (drawableQualified) {
                if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_HUGE_IMAGE)) {
                    SLog.d(NAME, "Use huge image function. previewDrawableSize: %dx%d, imageSize: %dx%d, mimeType: %s. %s",
                            previewWidth, previewHeight, imageWidth, imageHeight, sketchDrawable.getMimeType(), sketchDrawable.getKey());
                }
            } else {
                if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_HUGE_IMAGE)) {
                    SLog.d(NAME, "Don't need to use huge image function. previewDrawableSize: %dx%d, imageSize: %dx%d, mimeType: %s. %s",
                            previewWidth, previewHeight, imageWidth, imageHeight, sketchDrawable.getMimeType(), sketchDrawable.getKey());
                }
            }
        }

        boolean correctImageOrientationDisabled = !(imageView instanceof FunctionPropertyView)
                || ((FunctionPropertyView) imageView).getOptions().isCorrectImageOrientationDisabled();
        if (drawableQualified) {
            clean("setImage");

            this.imageUri = sketchDrawable.getUri();
            this.running = !TextUtils.isEmpty(imageUri);
            this.tileDecoder.setImage(imageUri, correctImageOrientationDisabled);
        } else {
            clean("setImage");

            this.imageUri = null;
            this.running = false;
            this.tileDecoder.setImage(null, correctImageOrientationDisabled);
        }
    }

    /**
     * 回收资源（回收后需要重新setImage()才能使用）
     */
    public void recycle(String why) {
        if (!SketchUtils.sdkSupportBitmapRegionDecoder()) {
            return;
        }

        running = false;
        clean(why);
        tileExecutor.recycle(why);
        tileManager.recycle(why);
        tileDecoder.recycle(why);
    }

    /**
     * 清理资源（不影响继续使用）
     */
    private void clean(String why) {
        if (!SketchUtils.sdkSupportBitmapRegionDecoder()) {
            return;
        }

        tileExecutor.cleanDecode(why);

        matrix.reset();
        lastZoomScale = 0;
        zoomScale = 0;

        tileManager.clean(why);

        invalidateView();
    }


    /* -----------回调方法----------- */


    public void onDraw(Canvas canvas) {
        if (!SketchUtils.sdkSupportBitmapRegionDecoder() || !isReady()) {
            return;
        }

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

    public void onMatrixChanged() {
        if (!SketchUtils.sdkSupportBitmapRegionDecoder()) {
            return;
        }

        if (!isReady() && !isInitializing()) {
            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_HUGE_IMAGE)) {
                SLog.d(NAME, "hugeImageViewer not available. onMatrixChanged. %s", imageUri);
            }
            return;
        }

        if (imageZoomer.getRotateDegrees() % 90 != 0) {
            SLog.w(NAME, "rotate degrees must be in multiples of 90. %s", imageUri);
            return;
        }

        if (tempDrawMatrix == null) {
            tempDrawMatrix = new Matrix();
            tempVisibleRect = new Rect();
        }

        tempDrawMatrix.reset();
        tempVisibleRect.setEmpty();

        imageZoomer.getDrawMatrix(tempDrawMatrix);
        imageZoomer.getVisibleRect(tempVisibleRect);

        Matrix drawMatrix = tempDrawMatrix;
        Rect newVisibleRect = tempVisibleRect;
        Size drawableSize = imageZoomer.getDrawableSize();
        Size viewSize = imageZoomer.getViewSize();
        boolean zooming = imageZoomer.isZooming();

        // 没有准备好就不往下走了
        if (!isReady()) {
            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_HUGE_IMAGE)) {
                SLog.d(NAME, "not ready. %s", imageUri);
            }
            return;
        }

        // 暂停中也不走了
        if (paused) {
            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_HUGE_IMAGE)) {
                SLog.d(NAME, "paused. %s", imageUri);
            }
            return;
        }

        // 传进来的参数不能用就什么也不显示
        if (newVisibleRect.isEmpty() || drawableSize.isEmpty() || viewSize.isEmpty()) {
            SLog.w(NAME, "update params is empty. update. newVisibleRect=%s, drawableSize=%s, viewSize=%s. %s",
                    newVisibleRect.toShortString(), drawableSize.toString(), viewSize.toString(), imageUri);
            clean("update param is empty");
            return;
        }

        // 如果当前完整显示预览图的话就清空什么也不显示
        if (newVisibleRect.width() == drawableSize.getWidth() && newVisibleRect.height() == drawableSize.getHeight()) {
            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_HUGE_IMAGE)) {
                SLog.d(NAME, "full display. update. newVisibleRect=%s. %s",
                        newVisibleRect.toShortString(), imageUri);
            }
            clean("full display");
            return;
        }

        // 更新Matrix
        lastZoomScale = zoomScale;
        matrix.set(drawMatrix);
        zoomScale = SketchUtils.formatFloat(SketchUtils.getMatrixScale(matrix), 2);

        invalidateView();

        tileManager.update(newVisibleRect, drawableSize, viewSize, getImageSize(), zooming);
    }


    /* -----------其它方法----------- */


    public void invalidateView() {
        imageZoomer.getImageView().invalidate();
    }

    public TileDecoder getTileDecoder() {
        return tileDecoder;
    }

    public TileExecutor getTileExecutor() {
        return tileExecutor;
    }

    /**
     * 暂停
     */
    public void setPause(boolean pause) {
        if (!SketchUtils.sdkSupportBitmapRegionDecoder()) {
            return;
        }

        if (pause == paused) {
            return;
        }
        paused = pause;

        if (paused) {
            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_HUGE_IMAGE)) {
                SLog.d(NAME, "pause. %s", imageUri);
            }

            if (running) {
                clean("pause");
            }
        } else {
            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_HUGE_IMAGE)) {
                SLog.d(NAME, "resume. %s", imageUri);
            }

            if (running) {
                onMatrixChanged();
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
        invalidateView();
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
    public void setOnTileChangedListener(BlockDisplayer.OnTileChangedListener onTileChangedListener) {
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

    public interface OnTileChangedListener {
        void onTileChanged(BlockDisplayer blockDisplayer);
    }

    private class ExecutorCallback implements TileExecutor.Callback {

        @Override
        public Context getContext() {
            return context;
        }

        @Override
        public void onInitCompleted(String imageUri, ImageRegionDecoder decoder) {
            if (!running) {
                SLog.w(NAME, "stop running. initCompleted. %s", imageUri);
                return;
            }

            tileDecoder.initCompleted(imageUri, decoder);

            onMatrixChanged();
        }

        @Override
        public void onInitError(String imageUri, Exception e) {
            if (!running) {
                SLog.w(NAME, "stop running. initError. %s", imageUri);
                return;
            }

            tileDecoder.initError(imageUri, e);
        }

        @Override
        public void onDecodeCompleted(Tile tile, Bitmap bitmap, int useTime) {
            if (!running) {
                SLog.w(NAME, "stop running. decodeCompleted. tile=%s", tile.getInfo());
                BitmapPoolUtils.freeBitmapToPoolForRegionDecoder(bitmap, Sketch.with(context).getConfiguration().getBitmapPool());
                return;
            }

            tileManager.decodeCompleted(tile, bitmap, useTime);
        }

        @Override
        public void onDecodeError(Tile tile, TileDecodeHandler.DecodeErrorException exception) {
            if (!running) {
                SLog.w(NAME, "stop running. decodeError. tile=%s", tile.getInfo());
                return;
            }

            tileManager.decodeError(tile, exception);
        }
    }
}
