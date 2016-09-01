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
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
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
public class SuperLargeImageViewer {
    private static final String NAME = "SuperLargeImageViewer";

    private Context context;
    private Callback callback;

    private Rect visibleRect;
    private Rect srcRect;
    private Rect drawRect;
    private Matrix matrix;

    private boolean running;
    private Paint drawTilePaint;
    private ImageRegionDecodeExecutor executor;

    private UpdateParams waitUpdateParams;
    private UpdateParams updateParams;

    private int tiles;
    private List<Tile> drawTileList;
    private List<Tile> loadingTileList;
    private List<Rect> loadRectList;
    private ObjectPool<Tile> tilePool;
    private ObjectPool<Rect> rectPool;
    private OnTileChangedListener onTileChangedListener;

    public SuperLargeImageViewer(Context context, Callback callback) {
        this.context = context.getApplicationContext();
        this.callback = callback;
        this.executor = new ImageRegionDecodeExecutor(new ExecutorCallback());

        this.drawTileList = new LinkedList<Tile>();
        this.loadingTileList = new LinkedList<Tile>();
        this.loadRectList = new ArrayList<Rect>();
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
        drawRect = new Rect();
        srcRect = new Rect();
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

        for (Tile tile : drawTileList) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME + ". clean tile. " + why + ". tile=" + tile.getInfo());
            }

            int oldKey = tile.getKey();
            tile.refreshKey();
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME + ". refreshKey. clean tile. " + why + ". oldKey=" + oldKey + ". " + tile.getInfo());
            }

            tile.clean();
        }
        drawTileList.clear();

        for (Tile tile : loadingTileList) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME + ". clean task. " + why + ". tile=" + tile.getInfo());
            }

            int oldKey = tile.getKey();
            tile.refreshKey();
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME + ". refreshKey. clean task. " + why + ". oldKey=" + oldKey + ". " + tile.getInfo());
            }

            tile.clean();
        }
        loadingTileList.clear();

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
        matrix.set(updateParams.drawMatrix);
        callback.invalidate();

        splitLoad(updateParams);
    }

    // TODO: 16/8/30 缓存 drawRect，当哟足够的差别时再处理
    // TODO: 16/8/31 还有对不齐的情况（主要是垂直方向上）
    // TODO: 16/8/31 还是有部分没有显示出来
    // TODO: 16/8/31 对象池的对象的回收好好查一下
    // TODO: 16/8/31 有些许的卡顿感，看怎么优化比较好，例如控制刷新率，或者降低图片数量
    private void splitLoad(UpdateParams updateParams) {
        float scale = SketchUtils.getMatrixScale(matrix);
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
        this.drawRect.set(drawRect);

        // 计算显示区域在完整图片中对应的区域，重点是各用各的缩放比例（这很重要），因为宽或高的比例可能不一样
        Rect srcRect = new Rect(
                Math.max(0, Math.round(drawRect.left * originWidthScale)),
                Math.max(0, Math.round(drawRect.top * originHeightScale)),
                Math.min(originImageWidth, Math.round(drawRect.right * originWidthScale)),
                Math.min(originImageHeight, Math.round(drawRect.bottom * originHeightScale)));
        this.srcRect.set(srcRect);

        // 根据src区域大小计算缩放比例，由于绘制区域比显示区域大了一圈了，因此计算inSampleSize时targetSize也得大一圈
        float targetSizeScale = ((float) tiles / 10) + 1;
        int targetWidth = Math.round(updateParams.imageViewWidth * targetSizeScale);
        int targetHeight = Math.round(updateParams.imageViewHeight * targetSizeScale);
        ImageSizeCalculator imageSizeCalculator = Sketch.with(context).getConfiguration().getImageSizeCalculator();
        int inSampleSize = imageSizeCalculator.calculateInSampleSize(srcRect.width(), srcRect.height(), targetWidth, targetHeight, false);

        Log.i(Sketch.TAG, NAME + ". split start" +
                ". visibleRect=" + visibleRect.toShortString() +
                ", drawRect=" + drawRect.toShortString() +
                ", scale=" + scale +
                ", loadingTiles=" + loadingTileList.size() +
                ", drawTiles=" + drawTileList.size());

        // 回收那些已经完全不可见的碎片，并计算已绘制区域
        Tile haveDrawTile;
        Iterator<Tile> tileIterator = drawTileList.iterator();
        Rect haveDrawRect = rectPool.get();
        haveDrawRect.set(-1, -1, -1, -1);
        while (tileIterator.hasNext()) {
            haveDrawTile = tileIterator.next();

            // 缩放比例已经变了或者这个碎片已经跟当前显示区域毫无交集，那么就可以回收这个碎片了
            if (scale != haveDrawTile.scale || !SketchUtils.isCross(haveDrawTile.drawRect, drawRect)) {
                if (Sketch.isDebugMode()) {
                    Log.d(Sketch.TAG, NAME + ". recycle tile. tile=" + haveDrawTile.getInfo());
                }
                tileIterator.remove();
                haveDrawTile.clean();
                tilePool.put(haveDrawTile);
                continue;
            }

            // 接下来需要计算当前已经绘制的边界
            haveDrawRect.left = haveDrawRect.left != -1 ? Math.min(haveDrawRect.left, haveDrawTile.drawRect.left) : haveDrawTile.drawRect.left;
            haveDrawRect.top = haveDrawRect.top != -1 ? Math.min(haveDrawRect.top, haveDrawTile.drawRect.top) : haveDrawTile.drawRect.top;
            haveDrawRect.right = haveDrawRect.right != -1 ? Math.max(haveDrawRect.right, haveDrawTile.drawRect.right) : haveDrawTile.drawRect.right;
            haveDrawRect.bottom = haveDrawRect.bottom != -1 ? Math.max(haveDrawRect.bottom, haveDrawTile.drawRect.bottom) : haveDrawTile.drawRect.bottom;
        }
        if (Sketch.isDebugMode()) {
            Log.i(Sketch.TAG, NAME + ". recycle tiles. haveBeenDrawRect=" + haveDrawRect.toShortString() + ", drawTiles=" + loadingTileList.size());
        }

        // 删除没用的任务
        Tile loadingTile;
        Iterator<Tile> loadingTileIterator = loadingTileList.iterator();
        while (loadingTileIterator.hasNext()) {
            loadingTile = loadingTileIterator.next();

            // 缩放比例已经变了或者这个碎片已经跟当前显示区域毫无交集，那么就可以停止这个任务了
            if (scale != loadingTile.scale || !SketchUtils.isCross(loadingTile.drawRect, drawRect)) {
                if (Sketch.isDebugMode()) {
                    Log.d(Sketch.TAG, NAME + ". recycle task. tile=" + loadingTile.getInfo());
                }
                loadingTileIterator.remove();

                int oldKey = loadingTile.getKey();
                loadingTile.refreshKey();
                if (Sketch.isDebugMode()) {
                    Log.w(Sketch.TAG, NAME + ". refreshKey. recycle task. oldKey=" + oldKey + ". " + loadingTile.getInfo());
                }
                continue;
            }

            // 接下来需要计算当前已经绘制的边界
            haveDrawRect.left = haveDrawRect.left != -1 ? Math.min(haveDrawRect.left, loadingTile.drawRect.left) : loadingTile.drawRect.left;
            haveDrawRect.top = haveDrawRect.top != -1 ? Math.min(haveDrawRect.top, loadingTile.drawRect.top) : loadingTile.drawRect.top;
            haveDrawRect.right = haveDrawRect.right != -1 ? Math.max(haveDrawRect.right, loadingTile.drawRect.right) : loadingTile.drawRect.right;
            haveDrawRect.bottom = haveDrawRect.bottom != -1 ? Math.max(haveDrawRect.bottom, loadingTile.drawRect.bottom) : loadingTile.drawRect.bottom;
        }
        if (Sketch.isDebugMode()) {
            Log.i(Sketch.TAG, NAME + ". recycle tasks. loadingTiles=" + loadingTileList.size());
        }

        // 没有完全显示还需要加载新的碎片
        if (haveDrawRect.left > drawRect.left || haveDrawRect.top > drawRect.top || haveDrawRect.right < drawRect.right || haveDrawRect.bottom < drawRect.bottom) {
            // 先收集所有需要显示的区域
            loadRectList.clear();
            if (haveDrawRect.left == -1 && haveDrawRect.top == -1 && haveDrawRect.right == -1 && haveDrawRect.bottom == -1) {
                // 需要全部读取
                Rect fullVisibleRect = rectPool.get();
                fullVisibleRect.set(drawRect);
                loadRectList.add(fullVisibleRect);
            } else {
                // 只需要读取部分时，检查四周的空白是否大于增加宽度的80%，如果大于就加载一列或一行完整的块
                int leftAndRightEdge = Math.round(drawWidthAdd * 0.8f);
                int topAndBottomEdge = Math.round(drawHeightAdd * 0.8f);

                int leftSpace = Math.abs(drawRect.left - haveDrawRect.left);
                int topSpace = Math.abs(drawRect.top - haveDrawRect.top);
                int rightSpace = Math.abs(drawRect.right - haveDrawRect.right);
                int bottomSpace = Math.abs(drawRect.bottom - haveDrawRect.bottom);

                if (haveDrawRect.left > drawRect.left && leftSpace > leftAndRightEdge) {
                    Rect leftLoadRect = rectPool.get();
                    leftLoadRect.set(Math.round(haveDrawRect.left - visibleTileWidth), drawRect.top, haveDrawRect.left, drawRect.bottom);
                    loadRectList.add(leftLoadRect);

                    Log.d(Sketch.TAG, NAME + ". leftLoadRect=" + leftLoadRect.toShortString() + ", leftSpace=" + leftSpace + ", leftAndRightEdge=" + leftAndRightEdge);
                }

                if (haveDrawRect.top > drawRect.top && topSpace > topAndBottomEdge) {
                    Rect topLoadRect = rectPool.get();
                    topLoadRect.set(haveDrawRect.left, Math.round(haveDrawRect.top - visibleTileHeight), haveDrawRect.right, haveDrawRect.top);
                    loadRectList.add(topLoadRect);

                    Log.d(Sketch.TAG, NAME + ". topLoadRect=" + topLoadRect.toShortString() + ", topSpace=" + topSpace + ", topAndBottomEdge=" + topAndBottomEdge);
                }

                if (haveDrawRect.right < drawRect.right && rightSpace > leftAndRightEdge) {
                    Rect rightLoadRect = rectPool.get();
                    rightLoadRect.set(haveDrawRect.right, drawRect.top, Math.round(haveDrawRect.right + visibleTileWidth), drawRect.bottom);
                    loadRectList.add(rightLoadRect);

                    Log.d(Sketch.TAG, NAME + ". rightLoadRect=" + rightLoadRect.toShortString() + ", rightSpace=" + rightSpace + ", leftAndRightEdge=" + leftAndRightEdge);
                }

                if (haveDrawRect.bottom < drawRect.bottom && bottomSpace > topAndBottomEdge) {
                    Rect bottomLoadRect = rectPool.get();
                    bottomLoadRect.set(haveDrawRect.left, haveDrawRect.bottom, haveDrawRect.right, Math.round(haveDrawRect.bottom + visibleTileHeight));
                    loadRectList.add(bottomLoadRect);

                    Log.d(Sketch.TAG, NAME + ". bottomLoadRect=" + bottomLoadRect.toShortString() + ", bottomSpace=" + bottomSpace + ", topAndBottomEdge=" + topAndBottomEdge);
                }
            }

            // 然后分割所有的显示区域
            Tile loadTile;
            float drawTileWidth = (float) drawRect.width() / (tiles + 1);
            float drawTileHeight = (float) drawRect.height() / (tiles + 1);
            for (Rect loadBlockRect : loadRectList) {
                float tileLeft = loadBlockRect.left, tileTop = loadBlockRect.top;
                float tileRight = 0, tileBottom = 0;
                Log.i(Sketch.TAG, NAME + ". split loadBlockRect=" + loadBlockRect.toShortString());
                while (Math.round(tileRight) < loadBlockRect.right || Math.round(tileBottom) < loadBlockRect.bottom) {
                    loadTile = tilePool.get();

                    tileRight = Math.min(tileLeft + drawTileWidth, loadBlockRect.right);
                    tileBottom = Math.min(tileTop + drawTileHeight, loadBlockRect.bottom);

                    loadTile.drawRect.set(Math.round(tileLeft), Math.round(tileTop), Math.round(tileRight), Math.round(tileBottom));

                    if (!contais(loadTile.drawRect)) {
                        loadTile.srcRect.set(
                                Math.max(0, Math.round(tileLeft * originWidthScale)),
                                Math.max(0, Math.round(tileTop * originHeightScale)),
                                Math.min(originImageWidth, Math.round(tileRight * originWidthScale)),
                                Math.min(originImageHeight, Math.round(tileBottom * originHeightScale))
                        );
                        loadTile.inSampleSize = inSampleSize;
                        loadTile.scale = scale;

                        int oldKey = loadTile.getKey();
                        loadTile.refreshKey();
                        if (Sketch.isDebugMode()) {
                            Log.w(Sketch.TAG, NAME + ". refreshKey. post decode. oldKey=" + oldKey + ". " + loadTile.getInfo());
                        }

                        if (Sketch.isDebugMode()) {
                            Log.d(Sketch.TAG, NAME + ". submit. tile=" + loadTile.getInfo());
                        }

                        // 提交任务
                        loadingTileList.add(loadTile);
                        executor.submit(loadTile.getKey(), loadTile);
                    } else {
                        if (Sketch.isDebugMode()) {
                            Log.w(Sketch.TAG, NAME + ". repeated tile tileDrawRect=" + loadTile.drawRect.toShortString());
                        }
                        loadTile.clean();
                        tilePool.put(loadTile);
                    }

                    if (Math.round(tileRight) >= loadBlockRect.right) {
                        tileLeft = loadBlockRect.left;
                        tileTop = tileBottom;
                    } else {
                        tileLeft = tileRight;
                    }
                }
            }
        }

        Log.e(Sketch.TAG, NAME + ". split finished" +
                ". visibleRect=" + visibleRect.toShortString() +
                ", drawRect=" + drawRect.toShortString() +
                ", loadingTiles=" + loadingTileList.size() +
                ", drawTiles=" + drawTileList.size());

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

    public List<Tile> getLoadingTileList() {
        return loadingTileList;
    }

    public Rect getSrcRect() {
        return srcRect;
    }

    private boolean contais(Rect tileDrawRect) {
        Iterator<Tile> loadingTileIterator = loadingTileList.iterator();
        Tile destTile;
        while (loadingTileIterator.hasNext()) {
            destTile = loadingTileIterator.next();
            if (tileDrawRect.equals(destTile.drawRect)) {
                return true;
            }
        }

        return false;
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

            tile.bitmap = bitmap;
            tile.bitmapDrawSrcRect.set(0, 0, bitmap.getWidth(), bitmap.getHeight());

            loadingTileList.remove(tile);
            drawTileList.remove(tile);
            drawTileList.add(tile);

            if (Sketch.isDebugMode()) {
                String bitmapConfig = bitmap.getConfig() != null ? bitmap.getConfig().name() : null;
                Log.i(Sketch.TAG, NAME + ". decodeCompleted" +
                        ". tile=" + tile.getInfo() +
                        ", bitmap=" + bitmap.getWidth() + "x" + bitmap.getHeight() + "(" + bitmapConfig + ")" +
                        ", loadingTiles=" + loadingTileList.size() +
                        ", drawTiles=" + drawTileList.size());
            }

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

            loadingTileList.remove(tile);

            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME + ". decodeFailed. " + exception.getCauseMessage() + ". tile=" + tile.getInfo() + ", loadingTiles=" + loadingTileList.size());
            }

            tile.clean();
            tilePool.put(tile);

            if (onTileChangedListener != null) {
                onTileChangedListener.onTileChanged(SuperLargeImageViewer.this);
            }
        }
    }
}
