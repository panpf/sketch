/*
 * Copyright (C) 2016 Peng fei Pan <sky@panpf.me>
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

package me.panpf.sketch.zoom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.widget.ImageView;

import java.util.List;

import me.panpf.sketch.SLog;
import me.panpf.sketch.Sketch;
import me.panpf.sketch.cache.BitmapPoolUtils;
import me.panpf.sketch.decode.ImageType;
import me.panpf.sketch.drawable.SketchDrawable;
import me.panpf.sketch.drawable.SketchLoadingDrawable;
import me.panpf.sketch.util.SketchUtils;
import me.panpf.sketch.viewfun.FunctionPropertyView;
import me.panpf.sketch.zoom.block.Block;
import me.panpf.sketch.zoom.block.BlockDecoder;
import me.panpf.sketch.zoom.block.BlockExecutor;
import me.panpf.sketch.zoom.block.BlockManager;
import me.panpf.sketch.zoom.block.DecodeHandler;
import me.panpf.sketch.zoom.block.ImageRegionDecoder;

/**
 * 对于超大图片，分块显示可见区域
 */
// TODO: 2017/5/8 重新规划设计大图查看器的实现，感觉现在的有些乱（初始化，解码，显示分离）
public class BlockDisplayer {
    private static final String NAME = "BlockDisplayer";

    private Context context;
    private ImageZoomer imageZoomer;

    private Matrix tempDrawMatrix;
    private Rect tempVisibleRect;

    private BlockExecutor blockExecutor;
    private BlockDecoder blockDecoder;
    private BlockManager blockManager;

    private float zoomScale;
    private float lastZoomScale;
    private Paint drawBlockPaint;
    private Paint drawBlockRectPaint;
    private Paint drawLoadingBlockRectPaint;
    private Matrix matrix;

    private boolean running;
    private boolean paused;
    private String imageUri;

    private boolean showBlockBounds;
    private BlockDisplayer.OnBlockChangedListener onBlockChangedListener;

    public BlockDisplayer(Context context, ImageZoomer imageZoomer) {
        context = context.getApplicationContext();
        this.context = context;
        this.imageZoomer = imageZoomer;

        this.blockExecutor = new BlockExecutor(new ExecutorCallback());
        this.blockManager = new BlockManager(context, this);
        this.blockDecoder = new BlockDecoder(this);

        this.matrix = new Matrix();
        this.drawBlockPaint = new Paint();
    }


    /* -----------主要方法----------- */


    public void reset() {
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
            drawableQualified &= SketchUtils.formatSupportBitmapRegionDecoder(ImageType.valueOfMimeType(sketchDrawable.getMimeType()));

            if (drawableQualified) {
                if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_ZOOM_BLOCK_DISPLAY)) {
                    SLog.d(NAME, "Use BlockDisplayer. previewDrawableSize: %dx%d, imageSize: %dx%d, mimeType: %s. %s",
                            previewWidth, previewHeight, imageWidth, imageHeight, sketchDrawable.getMimeType(), sketchDrawable.getKey());
                }
            } else {
                if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_ZOOM_BLOCK_DISPLAY)) {
                    SLog.d(NAME, "Don't need to use BlockDisplayer. previewDrawableSize: %dx%d, imageSize: %dx%d, mimeType: %s. %s",
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
            this.blockDecoder.setImage(imageUri, correctImageOrientationDisabled);
        } else {
            clean("setImage");

            this.imageUri = null;
            this.running = false;
            this.blockDecoder.setImage(null, correctImageOrientationDisabled);
        }
    }

    /**
     * 回收资源，回收后需要重新执行 {@link #reset()} 才能使用
     */
    public void recycle(String why) {
        running = false;
        clean(why);
        blockExecutor.recycle(why);
        blockManager.recycle(why);
        blockDecoder.recycle(why);
    }

    /**
     * 清理资源，不影响继续使用
     */
    private void clean(String why) {
        blockExecutor.cleanDecode(why);

        matrix.reset();
        lastZoomScale = 0;
        zoomScale = 0;

        blockManager.clean(why);

        invalidateView();
    }


    /* -----------回调方法----------- */


    public void onDraw(Canvas canvas) {
        if (blockManager.blockList != null && blockManager.blockList.size() > 0) {
            int saveCount = canvas.save();
            canvas.concat(matrix);

            for (Block block : blockManager.blockList) {
                if (!block.isEmpty()) {
                    canvas.drawBitmap(block.bitmap, block.bitmapDrawSrcRect, block.drawRect, drawBlockPaint);
                    if (showBlockBounds) {
                        if (drawBlockRectPaint == null) {
                            drawBlockRectPaint = new Paint();
                            drawBlockRectPaint.setColor(Color.parseColor("#88FF0000"));
                        }
                        canvas.drawRect(block.drawRect, drawBlockRectPaint);
                    }
                } else if (!block.isDecodeParamEmpty()) {
                    if (showBlockBounds) {
                        if (drawLoadingBlockRectPaint == null) {
                            drawLoadingBlockRectPaint = new Paint();
                            drawLoadingBlockRectPaint.setColor(Color.parseColor("#880000FF"));
                        }
                        canvas.drawRect(block.drawRect, drawLoadingBlockRectPaint);
                    }
                }
            }

            canvas.restoreToCount(saveCount);
        }
    }

    public void onMatrixChanged() {
        if (!isReady() && !isInitializing()) {
            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_ZOOM_BLOCK_DISPLAY)) {
                SLog.d(NAME, "BlockDisplayer not available. onMatrixChanged. %s", imageUri);
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
            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_ZOOM_BLOCK_DISPLAY)) {
                SLog.d(NAME, "not ready. %s", imageUri);
            }
            return;
        }

        // 暂停中也不走了
        if (paused) {
            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_ZOOM_BLOCK_DISPLAY)) {
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
            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_ZOOM_BLOCK_DISPLAY)) {
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

        blockManager.update(newVisibleRect, drawableSize, viewSize, getImageSize(), zooming);
    }


    /* -----------其它方法----------- */


    public void invalidateView() {
        imageZoomer.getImageView().invalidate();
    }

    public BlockDecoder getBlockDecoder() {
        return blockDecoder;
    }

    public BlockExecutor getBlockExecutor() {
        return blockExecutor;
    }

    /**
     * 设置是否暂停，暂停后会清除所有的碎片，并不会再解码新的碎片
     */
    @SuppressWarnings("unused")
    public void setPause(boolean pause) {
        if (pause == paused) {
            return;
        }
        paused = pause;

        if (paused) {
            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_ZOOM_BLOCK_DISPLAY)) {
                SLog.d(NAME, "pause. %s", imageUri);
            }

            if (running) {
                clean("pause");
            }
        } else {
            if (SLog.isLoggable(SLog.LEVEL_DEBUG | SLog.TYPE_ZOOM_BLOCK_DISPLAY)) {
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
        return running && blockDecoder.isReady();
    }

    /**
     * 初始化中？
     */
    public boolean isInitializing() {
        return running && blockDecoder.isInitializing();
    }

    /**
     * 是否显示碎片的范围（红色表示已加载，蓝色表示正在加载）
     */
    public boolean isShowBlockBounds() {
        return showBlockBounds;
    }

    /**
     * 设置是否显示碎片的范围（红色表示已加载，蓝色表示正在加载）
     */
    @SuppressWarnings("unused")
    public void setShowBlockBounds(boolean showBlockBounds) {
        this.showBlockBounds = showBlockBounds;
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
        return blockDecoder.isReady() ? blockDecoder.getDecoder().getImageSize() : null;
    }

    /**
     * 获取图片的类型
     */
    @SuppressWarnings("unused")
    public ImageType getImageType() {
        return blockDecoder.isReady() ? blockDecoder.getDecoder().getImageType() : null;
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
        return blockManager.drawRect;
    }

    /**
     * 获取绘制区域在原图中对应的位置
     */
    public Rect getDrawSrcRect() {
        return blockManager.drawSrcRect;
    }

    /**
     * 获取解码区域
     */
    public Rect getDecodeRect() {
        return blockManager.decodeRect;
    }

    /**
     * 获取解码区域在原图中对应的位置
     */
    public Rect getDecodeSrcRect() {
        return blockManager.decodeSrcRect;
    }

    /**
     * 获取碎片列表
     */
    public List<Block> getBlockList() {
        return blockManager.blockList;
    }

    /**
     * 获取碎片数量
     */
    @SuppressWarnings("unused")
    public int getBlockSize() {
        return blockManager.blockList.size();
    }

    /**
     * 获取碎片占用的内存，单位字节
     */
    @SuppressWarnings("unused")
    public long getAllocationByteCount() {
        return blockManager.getAllocationByteCount();
    }

    /**
     * 获取碎片基数，例如碎片基数是3时，就将绘制区域分割成一个 (3+1)x(3+1)=16 个方块
     */
    public int getBlockBaseNumber() {
        return blockManager.blockBaseNumber;
    }

    /**
     * 获取碎片变化监听器
     */
    @SuppressWarnings("unused")
    public OnBlockChangedListener getOnBlockChangedListener() {
        return onBlockChangedListener;
    }

    /**
     * 获取碎片变化监听器
     */
    public void setOnBlockChangedListener(OnBlockChangedListener onBlockChangedListener) {
        this.onBlockChangedListener = onBlockChangedListener;
    }

    @Nullable
    public Block getBlockByDrawablePoint(int drawableX, int drawableY) {
        for (Block block : blockManager.blockList) {
            if(block.drawRect.contains(drawableX, drawableY)){
                return block;
            }
        }
        return null;
    }

    @Nullable
    public Block getBlockByImagePoint(int imageX, int imageY) {
        for (Block block : blockManager.blockList) {
            if(block.srcRect.contains(imageX, imageY)){
                return block;
            }
        }
        return null;
    }

    public interface OnBlockChangedListener {
        void onBlockChanged(BlockDisplayer blockDisplayer);
    }

    private class ExecutorCallback implements BlockExecutor.Callback {

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

            blockDecoder.initCompleted(imageUri, decoder);

            onMatrixChanged();
        }

        @Override
        public void onInitError(String imageUri, Exception e) {
            if (!running) {
                SLog.w(NAME, "stop running. initError. %s", imageUri);
                return;
            }

            blockDecoder.initError(imageUri, e);
        }

        @Override
        public void onDecodeCompleted(Block block, Bitmap bitmap, int useTime) {
            if (!running) {
                SLog.w(NAME, "stop running. decodeCompleted. block=%s", block.getInfo());
                BitmapPoolUtils.freeBitmapToPoolForRegionDecoder(bitmap, Sketch.with(context).getConfiguration().getBitmapPool());
                return;
            }

            blockManager.decodeCompleted(block, bitmap, useTime);
        }

        @Override
        public void onDecodeError(Block block, DecodeHandler.DecodeErrorException exception) {
            if (!running) {
                SLog.w(NAME, "stop running. decodeError. block=%s", block.getInfo());
                return;
            }

            blockManager.decodeError(block, exception);
        }
    }
}
