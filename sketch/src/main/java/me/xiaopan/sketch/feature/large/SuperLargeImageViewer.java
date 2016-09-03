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
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.Log;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.feature.ImageSizeCalculator;
import me.xiaopan.sketch.util.SketchUtils;

/**
 * 超大图片查看器
 */
// TODO: 16/8/16 再细分成一个一个的小方块
// TODO: 16/8/29 加上旋转之后，不知道会有什么异常问题
// TODO: 16/9/1 缩放中的时候不变
public class SuperLargeImageViewer {
    private static final String NAME = "SuperLargeImageViewer";

    private Context context;
    private Callback callback;

    private Rect visibleRect;
    private Rect cacheSrcRect;
    private Rect cacheDrawRect;
    private Matrix matrix;
    private float lastScale;
    private float scale;

    private boolean running;
    private Paint drawTilePaint;
    private Paint drawTileRectPaint;
    private Paint loadingTileRectPaint;
    private ImageRegionDecodeExecutor executor;
    private boolean showDrawRect;

    private UpdateParams waitUpdateParams;
    private UpdateParams updateParams;

    private int tiles;
    private List<Tile> drawTileList;
    private ObjectPool<Tile> tilePool;
    private ObjectPool<Rect> rectPool;
    private OnTileChangedListener onTileChangedListener;

    public SuperLargeImageViewer(Context context, Callback callback) {
        this.context = context.getApplicationContext();
        this.callback = callback;
        this.executor = new ImageRegionDecodeExecutor(new ExecutorCallback());

        this.drawTileList = new LinkedList<Tile>();
        this.tilePool = new ObjectPool<Tile>(new ObjectPool.NewItemCallback<Tile>() {
            @Override
            public Tile newItem() {
                return new Tile();
            }
        });
        this.rectPool = new ObjectPool<Rect>(new ObjectPool.NewItemCallback<Rect>() {
            @Override
            public Rect newItem() {
                return new Rect();
            }
        }, 10);
        this.tiles = 3;

        visibleRect = new Rect();
        cacheDrawRect = new Rect();
        cacheSrcRect = new Rect();
        matrix = new Matrix();
        updateParams = new UpdateParams();

        drawTilePaint = new Paint();
    }

    public void draw(Canvas canvas) {
        if (drawTileList != null && drawTileList.size() > 0) {
            int saveCount = canvas.save();
            canvas.concat(matrix);

            for (Tile tile : drawTileList) {
                if (!tile.isEmpty()) {
                    canvas.drawBitmap(tile.bitmap, tile.bitmapDrawSrcRect, tile.drawRect, drawTilePaint);
                    if (showDrawRect) {
                        if (drawTileRectPaint == null) {
                            drawTileRectPaint = new Paint();
                            drawTileRectPaint.setColor(Color.parseColor("#88FF0000"));
                        }
                        canvas.drawRect(tile.drawRect, drawTileRectPaint);
                    }
                } else if (!tile.isDecodeParamEmpty()) {
                    if (showDrawRect) {
                        if (loadingTileRectPaint == null) {
                            loadingTileRectPaint = new Paint();
                            loadingTileRectPaint.setColor(Color.parseColor("#880000FF"));
                        }
                        canvas.drawRect(tile.drawRect, loadingTileRectPaint);
                    }
                }
            }

            canvas.restoreToCount(saveCount);
        }
    }

    public void setImage(String imageUri) {
        clean("setImage");

        if (!TextUtils.isEmpty(imageUri)) {
            running = true;
            executor.initDecoder(imageUri);
        } else {
            running = false;
            executor.initDecoder(null);
        }
    }

    private void clean(String why) {
        executor.clean(why);

        if (waitUpdateParams != null) {
            waitUpdateParams.reset();
        }
        matrix.reset();
        lastScale = 0;
        scale = 0;

        for (Tile tile : drawTileList) {
            tile.refreshKey();
            tile.clean();
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME + ". clean tile and refresh key. " + why + ". tile=" + tile.getInfo());
            }
        }
        drawTileList.clear();

        callback.invalidate();
    }

    public void recycle(String why) {
        running = false;
        clean(why);
        executor.recycle(why);
        tilePool.clear();
        rectPool.clear();
    }

    public void update(UpdateParams updateParams) {
        // 不可用，也没有初始化就直接结束
        if (!isAvailable() && !isInitializing()) {
            return;
        }

        // 传进来的参数不能用就什么也不显示
        if (updateParams == null || updateParams.isEmpty()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME + ". params is empty. update");
            }
            clean("update param is empty");
            return;
        }

        // 如果正在初始化就就缓存当前更新参数
        if (!isAvailable() && isInitializing()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME + ". initializing. update");
            }
            if (waitUpdateParams == null) {
                waitUpdateParams = new UpdateParams();
            }
            waitUpdateParams.set(updateParams);
            return;
        }

        // 过滤掉重复的刷新
        if (visibleRect.equals(updateParams.visibleRect)) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME + ". visible rect no changed. update. visibleRect=" + updateParams.visibleRect.toShortString() + ", oldVisibleRect=" + visibleRect.toShortString());
            }
            return;
        }
        visibleRect.set(updateParams.visibleRect);

        // 如果当前完整显示预览图的话就清空什么也不显示
        int visibleWidth = updateParams.visibleRect.width();
        int visibleHeight = updateParams.visibleRect.height();
        if (visibleWidth == updateParams.previewDrawableWidth && visibleHeight == updateParams.previewDrawableHeight) {
            if (Sketch.isDebugMode()) {
                Log.d(Sketch.TAG, NAME + ". full display. update");
            }
            clean("full display");
            return;
        }

        // 取消旧的任务并更新Matrix
        lastScale = scale;
        matrix.set(updateParams.drawMatrix);
        scale = SketchUtils.formatFloat(SketchUtils.getMatrixScale(matrix), 2);

        callback.invalidate();

        splitLoad(updateParams);
    }

    // TODO: 16/8/30 缓存 drawRect，当哟足够的差别时再处理
    // TODO: 16/8/31 还有对不齐的情况（主要是垂直方向上）
    // TODO: 16/8/31 对象池的对象的回收好好查一下
    // TODO: 16/8/31 有些许的卡顿感，看怎么优化比较好，例如控制刷新率，或者降低图片数量
    // TODO: 16/9/3 还有一点儿稍稍的错位
    private void splitLoad(UpdateParams updateParams) {
        Rect visibleRect = updateParams.visibleRect;

        // 可见区域碎片的宽高
        float visibleTileWidth = (float) visibleRect.width() / tiles;
        float visibleTileHeight = (float) visibleRect.height() / tiles;

        // 原始图片的宽高
        int originImageWidth = executor.getDecoder().getImageWidth();
        int originImageHeight = executor.getDecoder().getImageHeight();

        // 原始图和预览图对比的缩放比例
        float originWidthScale = (float) originImageWidth / updateParams.previewDrawableWidth;
        float originHeightScale = (float) originImageHeight / updateParams.previewDrawableHeight;

        // 计算绘制区域时，每边应该增加的量
        float drawWidthAdd = visibleTileWidth / 2;
        float drawHeightAdd = visibleTileHeight / 2;

        // 将显示区域加大一圈，计算出绘制区域，宽高各增加一个平均值，为的是提前将四周加载出来，用户缓慢滑动的时候可以提前看到四周的图像
        Rect drawRect = rectPool.get();
        drawRect.left = Math.max(0, Math.round(visibleRect.left - drawWidthAdd));
        drawRect.top = Math.max(0, Math.round(visibleRect.top - drawHeightAdd));
        drawRect.right = Math.min(updateParams.previewDrawableWidth, Math.round(visibleRect.right + drawWidthAdd));
        drawRect.bottom = Math.min(updateParams.previewDrawableHeight, Math.round(visibleRect.bottom + drawHeightAdd));

        // 计算碎片的尺寸
        int finalTiles = tiles + 1;
        int drawTileWidth = drawRect.width() / finalTiles;
        int drawTileHeight = drawRect.height() / finalTiles;

        // 根据碎片尺寸修剪drawRect，使其正好能整除碎片
        drawRect.right = drawRect.left + (finalTiles * drawTileWidth);
        drawRect.bottom = drawRect.top + (finalTiles * drawTileHeight);

        // 计算显示区域在完整图片中对应的区域，重点是各用各的缩放比例（这很重要），因为宽或高的比例可能不一样
        Rect srcRect = new Rect(
                Math.max(0, Math.round(drawRect.left * originWidthScale)),
                Math.max(0, Math.round(drawRect.top * originHeightScale)),
                Math.min(originImageWidth, Math.round(drawRect.right * originWidthScale)),
                Math.min(originImageHeight, Math.round(drawRect.bottom * originHeightScale)));

        // 根据src区域大小计算缩放比例，由于绘制区域比显示区域大了一圈了，因此计算inSampleSize时targetSize也得大一圈
        float targetSizeScale = ((float) tiles / 10) + 1;
        int targetWidth = Math.round(updateParams.imageViewWidth * targetSizeScale);
        int targetHeight = Math.round(updateParams.imageViewHeight * targetSizeScale);
        ImageSizeCalculator imageSizeCalculator = Sketch.with(context).getConfiguration().getImageSizeCalculator();
        int inSampleSize = imageSizeCalculator.calculateInSampleSize(srcRect.width(), srcRect.height(), targetWidth, targetHeight, false);

        if (Sketch.isDebugMode()) {
            Log.i(Sketch.TAG, NAME + ". split start" +
                    ". visibleRect=" + visibleRect.toShortString() +
                    ", lastScale=" + lastScale +
                    ", scale=" + scale +
                    ". drawRect=" + drawRect.toShortString() +
                    ". cacheDrawRect=" + cacheDrawRect.toShortString() +
                    ". inSampleSize=" + inSampleSize +
                    ", drawTiles=" + drawTileList.size());
        }

        // 哪边可以扩展了就扩大哪边
        boolean needLoad = false;
        if (scale != lastScale) {
            cacheDrawRect.setEmpty();
        }
        if (!cacheDrawRect.isEmpty()) {
            // TODO: 16/9/3 到不了最边上
            int leftAndRightEdge = Math.round(drawWidthAdd * 0.8f);
            int topAndBottomEdge = Math.round(drawHeightAdd * 0.8f);
            int leftSpace = Math.abs(drawRect.left - cacheDrawRect.left);
            int topSpace = Math.abs(drawRect.top - cacheDrawRect.top);
            int rightSpace = Math.abs(drawRect.right - cacheDrawRect.right);
            int bottomSpace = Math.abs(drawRect.bottom - cacheDrawRect.bottom);

            Rect newDrawRect = rectPool.get();
            newDrawRect.set(cacheDrawRect);
            if (drawRect.left < cacheDrawRect.left && leftSpace > leftAndRightEdge) {
                newDrawRect.left = Math.max(0, cacheDrawRect.left - drawTileWidth);
                int newDrawRight = cacheDrawRect.right;
                while (drawRect.right <= newDrawRight - drawTileWidth) {
                    newDrawRight = newDrawRight - drawTileWidth;
                }
                newDrawRect.right = newDrawRight;

                if (Sketch.isDebugMode()) {
                    Log.d(Sketch.TAG, NAME + ". draw rect left expand. newDrawRect=" + newDrawRect.toShortString());
                }
                needLoad = true;
            }

            if (drawRect.top < cacheDrawRect.top && topSpace > topAndBottomEdge) {
                newDrawRect.top = Math.max(0, cacheDrawRect.top - drawTileHeight);
                int newDrawBottom = cacheDrawRect.bottom;
                while (drawRect.bottom <= Math.round(newDrawBottom - drawTileHeight)) {
                    newDrawBottom = newDrawBottom - drawTileHeight;
                }
                newDrawRect.bottom = newDrawBottom;

                if (Sketch.isDebugMode()) {
                    Log.d(Sketch.TAG, NAME + ". draw rect top expand. newDrawRect=" + newDrawRect.toShortString());
                }
                needLoad = true;
            }

            if (rightSpace > leftAndRightEdge && drawRect.right > cacheDrawRect.right) {
                int newDrawLeft = cacheDrawRect.left;
                while (drawRect.left >= newDrawLeft + drawTileWidth) {
                    newDrawLeft = newDrawLeft + drawTileWidth;
                }
                newDrawRect.left = newDrawLeft;
                newDrawRect.right = Math.min(updateParams.previewDrawableWidth, cacheDrawRect.right + drawTileWidth);

                if (Sketch.isDebugMode()) {
                    Log.d(Sketch.TAG, NAME + ". draw rect right expand. newDrawRect=" + newDrawRect.toShortString());
                }
                needLoad = true;
            }

            if (bottomSpace > topAndBottomEdge && drawRect.bottom > cacheDrawRect.bottom) {
                int newDrawTop = cacheDrawRect.top;
                while (drawRect.top >= newDrawTop + drawTileHeight) {
                    newDrawTop = newDrawTop + drawTileHeight;
                }
                newDrawRect.top = newDrawTop;
                newDrawRect.bottom = Math.min(updateParams.previewDrawableHeight, cacheDrawRect.bottom + drawTileHeight);

                if (Sketch.isDebugMode()) {
                    Log.d(Sketch.TAG, NAME + ". draw rect bottom expand. newDrawRect=" + newDrawRect.toShortString());
                }
                needLoad = true;
            }

            drawRect.set(newDrawRect);
            newDrawRect.setEmpty();
            rectPool.put(newDrawRect);
        } else {
            needLoad = true;
        }

        // 不需要扩展说明，当前已加载的区域够用，那就结束吧
        if (!needLoad) {
            Log.e(Sketch.TAG, NAME + ". split finished draw rect no change" +
                    ". visibleRect=" + visibleRect.toShortString() +
                    ". drawRect=" + drawRect.toShortString() +
                    ". cacheDrawRect=" + cacheDrawRect.toShortString() +
                    ", drawTiles=" + drawTileList.size());
            return;
        }

        this.cacheDrawRect.set(drawRect);
        srcRect = new Rect(
                Math.max(0, Math.round(drawRect.left * originWidthScale)),
                Math.max(0, Math.round(drawRect.top * originHeightScale)),
                Math.min(originImageWidth, Math.round(drawRect.right * originWidthScale)),
                Math.min(originImageHeight, Math.round(drawRect.bottom * originHeightScale)));
        this.cacheSrcRect.set(srcRect);

        // 回收那些已经完全不可见的碎片
        Tile tile;
        Iterator<Tile> tileIterator = drawTileList.iterator();
        while (tileIterator.hasNext()) {
            tile = tileIterator.next();

            // 缩放比例已经变了或者这个碎片已经跟当前显示区域毫无交集，那么就可以回收这个碎片了
            if (scale != tile.scale || !SketchUtils.isCross(tile.drawRect, drawRect)) {
                if (!tile.isEmpty()) {
                    if (Sketch.isDebugMode()) {
                        Log.d(Sketch.TAG, NAME + ". recycle tile. tile=" + tile.getInfo());
                    }
                    tileIterator.remove();
                    tile.clean();
                    tilePool.put(tile);
                } else {
                    tile.refreshKey();
                    tileIterator.remove();
                    if (Sketch.isDebugMode()) {
                        Log.d(Sketch.TAG, NAME + ". recycle loading tile and refresh key. tile=" + tile.getInfo());
                    }
                }
            }
        }
        if (Sketch.isDebugMode()) {
            Log.d(Sketch.TAG, NAME + ". recycle tiles. drawTiles=" + drawTileList.size());
        }

        // 找出所有的空白区域，然后一个一个加载
        List<Rect> emptyRectList = findEmptyRect(drawRect, drawTileList);
        if (emptyRectList != null && emptyRectList.size() > 0) {
            for(Rect emptyRect : emptyRectList){
                int tileLeft = emptyRect.left, tileTop = emptyRect.top;
                int tileRight = 0, tileBottom = 0;
                if (Sketch.isDebugMode()) {
                    Log.i(Sketch.TAG, NAME + ". load emptyRect=" + emptyRect.toShortString());
                }
                while (Math.round(tileRight) < emptyRect.right || Math.round(tileBottom) < emptyRect.bottom) {
                    tileRight = Math.min(tileLeft + drawTileWidth, emptyRect.right);
                    tileBottom = Math.min(tileTop + drawTileHeight, emptyRect.bottom);

                    if (canLoad(Math.round(tileLeft), Math.round(tileTop), Math.round(tileRight), Math.round(tileBottom))) {
                        Tile loadTile = tilePool.get();
                        loadTile.drawRect.set(Math.round(tileLeft), Math.round(tileTop), Math.round(tileRight), Math.round(tileBottom));
                        loadTile.srcRect.set(
                                Math.max(0, Math.round(tileLeft * originWidthScale)),
                                Math.max(0, Math.round(tileTop * originHeightScale)),
                                Math.min(originImageWidth, Math.round(tileRight * originWidthScale)),
                                Math.min(originImageHeight, Math.round(tileBottom * originHeightScale))
                        );
                        loadTile.inSampleSize = inSampleSize;
                        loadTile.scale = scale;

                        // 提交任务
                        loadTile.refreshKey();
                        drawTileList.add(loadTile);
                        if (Sketch.isDebugMode()) {
                            Log.d(Sketch.TAG, NAME + ". submit and refresh key" +
                                    ". drawRect=" + drawRect.toShortString() + ", tile=" + loadTile.getInfo());
                        }
                        executor.submit(loadTile.getKey(), loadTile);
                    } else {
                        if (Sketch.isDebugMode()) {
                            Log.w(Sketch.TAG, NAME + ". repeated tile tileDrawRect=" +
                                    Math.round(tileLeft) + ", " + Math.round(tileTop) + ", " +
                                    Math.round(tileRight) + ", " + Math.round(tileBottom));
                        }
                    }

                    if (Math.round(tileRight) >= emptyRect.right) {
                        tileLeft = emptyRect.left;
                        tileTop = tileBottom;
                    } else {
                        tileLeft = tileRight;
                    }
                }
            }
        }

        if (Sketch.isDebugMode()) {
            Log.e(Sketch.TAG, NAME + ". split finished" +
                    ". visibleRect=" + visibleRect.toShortString() +
                    ", drawRect=" + drawRect.toShortString() +
                    ", drawTiles=" + drawTileList.size());
        }

        if (onTileChangedListener != null) {
            onTileChangedListener.onTileChanged(this);
        }
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

    public void setOnTileChangedListener(OnTileChangedListener onTileChangedListener) {
        this.onTileChangedListener = onTileChangedListener;
    }

    public ImageRegionDecodeExecutor getExecutor() {
        return executor;
    }

    public List<Tile> getDrawTileList() {
        return drawTileList;
    }

    public Rect getCacheSrcRect() {
        return cacheSrcRect;
    }

    @SuppressWarnings("unused")
    public void setShowDrawRect(boolean showDrawRect) {
        this.showDrawRect = showDrawRect;
        callback.invalidate();
    }

    private boolean canLoad(int left, int top, int right, int bottom) {
        for (Tile drawTile : drawTileList) {
            if (drawTile.drawRect.left == left &&
                    drawTile.drawRect.top == top &&
                    drawTile.drawRect.right == right &&
                    drawTile.drawRect.bottom == bottom) {
                return false;
            }
        }

        return true;
    }

    public interface Callback {
        void invalidate();
    }

    public interface OnTileChangedListener {
        void onTileChanged(SuperLargeImageViewer superLargeImageViewer);
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
        public void onDecodeCompleted(Tile tile, Bitmap bitmap) {
            if (!running) {
                if (Sketch.isDebugMode()) {
                    Log.w(Sketch.TAG, NAME + ". stop running. decodeCompleted. tile=" + tile.getInfo());
                }
                bitmap.recycle();
                return;
            }

            if (Sketch.isDebugMode()) {
                String bitmapConfig = bitmap.getConfig() != null ? bitmap.getConfig().name() : null;
                Log.i(Sketch.TAG, NAME + ". decodeCompleted" +
                        ". tile=" + tile.getInfo() +
                        ", bitmap=" + bitmap.getWidth() + "x" + bitmap.getHeight() + "(" + bitmapConfig + ")" +
                        ", drawTiles=" + drawTileList.size());
            }

            tile.bitmap = bitmap;
            tile.bitmapDrawSrcRect.set(0, 0, bitmap.getWidth(), bitmap.getHeight());
            callback.invalidate();

            if (onTileChangedListener != null) {
                onTileChangedListener.onTileChanged(SuperLargeImageViewer.this);
            }
        }

        @Override
        public void onDecodeFailed(Tile tile, DecodeHandler.DecodeFailedException exception) {
            if (!running) {
                if (Sketch.isDebugMode()) {
                    Log.w(Sketch.TAG, NAME + ". stop running. decodeFailed. tile=" + tile.getInfo());
                }
                return;
            }

            drawTileList.remove(tile);

            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME + ". decodeFailed. " + exception.getCauseMessage() + ". tile=" + tile.getInfo() + ", drawTiles=" + drawTileList.size());
            }

            tile.clean();
            tilePool.put(tile);

            if (onTileChangedListener != null) {
                onTileChangedListener.onTileChanged(SuperLargeImageViewer.this);
            }
        }
    }

    /**
     * 假如有一个矩形，并且已知这个矩形中的N个碎片，那么要找出所有的空白碎片（不可用的碎片会从已知列表中删除）
     * @param rect 那个矩形
     * @param tileList 已知碎片
     * @return 所有空白的碎片
     */
    public static List<Rect> findEmptyRect(Rect rect, List<Tile> tileList) {
        if (rect.isEmpty()) {
            return null;
        }

        List<Rect> emptyRectList = null;
        if (tileList == null || tileList.size() == 0) {
            emptyRectList = new LinkedList<Rect>();
            emptyRectList.add(rect);
            return emptyRectList;
        }

        // 按离左上角的距离排序
        Collections.sort(tileList, new Comparator<Tile>() {
            @Override
            public int compare(Tile o1, Tile o2) {
                if (o1.drawRect.top >= o2.drawRect.bottom || o2.drawRect.top >= o1.drawRect.bottom) {
                    return o1.drawRect.top - o2.drawRect.top;
                } else {
                    return o1.drawRect.left - o2.drawRect.left;
                }
            }
        });

        int left = rect.left, top = rect.top, right = 0, bottom = -1;
        Tile lastRect = null;
        Tile childRect;
        Iterator<Tile> rectIterator = tileList.iterator();
        while (rectIterator.hasNext()) {
            childRect = rectIterator.next();

            boolean newLine = lastRect == null || (childRect.drawRect.top >= bottom);
            if (newLine) {
                // 首先要处理上一行的最后一个
                if (lastRect != null) {
                    if (lastRect.drawRect.right < rect.right) {
                        Rect rightEmptyRect = new Rect(lastRect.drawRect.right, top, rect.right, bottom);
                        if (emptyRectList == null) {
                            emptyRectList = new LinkedList<Rect>();
                        }
                        emptyRectList.add(rightEmptyRect);
                    }
                }

                // 然后要更新top和bottom
                top = bottom != -1 ? bottom : top;
                bottom = childRect.drawRect.bottom;

                // 左边有空隙
                if (childRect.drawRect.left > left) {
                    Rect leftEmptyRect = new Rect(left, childRect.drawRect.top, childRect.drawRect.left, childRect.drawRect.bottom);
                    if (emptyRectList == null) {
                        emptyRectList = new LinkedList<Rect>();
                    }
                    emptyRectList.add(leftEmptyRect);
                }

                // 顶部有空隙
                if (childRect.drawRect.top > top) {
                    Rect topEmptyRect = new Rect(left, top, childRect.drawRect.right, childRect.drawRect.top);
                    if (emptyRectList == null) {
                        emptyRectList = new LinkedList<Rect>();
                    }
                    emptyRectList.add(topEmptyRect);
                }

                right = childRect.drawRect.right;
                lastRect = childRect;
            } else {
                boolean available = childRect.drawRect.bottom == lastRect.drawRect.bottom;
                if (available) {
                    // 左边有空隙
                    if (childRect.drawRect.left > right) {
                        Rect leftEmptyRect = new Rect(right, top, childRect.drawRect.left, bottom);
                        if (emptyRectList == null) {
                            emptyRectList = new LinkedList<Rect>();
                        }
                        emptyRectList.add(leftEmptyRect);
                    }

                    // 顶部有空隙
                    if (childRect.drawRect.top > top) {
                        Rect topEmptyRect = new Rect(childRect.drawRect.left, top, childRect.drawRect.right, childRect.drawRect.top);
                        if (emptyRectList == null) {
                            emptyRectList = new LinkedList<Rect>();
                        }
                        emptyRectList.add(topEmptyRect);
                    }

                    right = childRect.drawRect.right;
                    lastRect = childRect;
                } else {
                    rectIterator.remove();
                }
            }
        }

        // 最后的结尾处理
        if (right < rect.right) {
            Rect rightEmptyRect = new Rect(right, top, rect.right, bottom);
            if (emptyRectList == null) {
                emptyRectList = new LinkedList<Rect>();
            }
            emptyRectList.add(rightEmptyRect);
        }

        if (bottom < rect.bottom) {
            Rect bottomEmptyRect = new Rect(rect.left, bottom, rect.right, rect.bottom);
            if (emptyRectList == null) {
                emptyRectList = new LinkedList<Rect>();
            }
            emptyRectList.add(bottomEmptyRect);
        }

        return emptyRectList;
    }
}
