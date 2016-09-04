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

import java.util.List;

import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.util.SketchUtils;

/**
 * 大图片查看器
 */
// TODO: 16/8/29 加上旋转之后，不知道会有什么异常问题
// TODO: 16/9/1 缩放中的时候不变
public class LargeImageViewer {
    private static final String NAME = "LargeImageViewer";

    private Context context;
    private Callback callback;

    private boolean showDrawRect;

    private float lastScale;
    private float scale;
    private Paint drawTilePaint;
    private Paint drawTileRectPaint;
    private Paint loadingTileRectPaint;
    private Matrix matrix;

    private boolean running;
    private Rect visibleRect = new Rect();
    private UpdateParams waitUpdateParams;
    private UpdateParams updateParams;
    private TileManager tileManager;
    private TileDecodeExecutor executor;

    public LargeImageViewer(Context context, Callback callback) {
        this.context = context.getApplicationContext();
        this.callback = callback;

        this.matrix = new Matrix();
        this.executor = new TileDecodeExecutor(new ExecutorCallback());
        this.updateParams = new UpdateParams();
        this.drawTilePaint = new Paint();
        this.tileManager = new TileManager(context.getApplicationContext(), this);
    }

    public void draw(Canvas canvas) {
        List<Tile> tileList = tileManager.getTileList();
        if (tileList != null && tileList.size() > 0) {
            int saveCount = canvas.save();
            canvas.concat(matrix);

            for (Tile tile : tileList) {
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

    /**
     * 设置新的图片
     */
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

    /**
     * 更新
     */
    public void update(UpdateParams updateParams) {
        // 不可用，也没有初始化就直接结束
        if (!isAvailable() && !isInitializing()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME + ". unavailable");
            }
            return;
        }

        // 传进来的参数不能用就什么也不显示
        if (updateParams == null || updateParams.isEmpty()) {
            if (Sketch.isDebugMode()) {
                Log.w(Sketch.TAG, NAME + ". update params is empty. update");
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

        tileManager.update(updateParams.visibleRect,
                updateParams.imageViewWidth, updateParams.imageViewHeight,
                executor.getDecoder().getImageWidth(), executor.getDecoder().getImageHeight(),
                updateParams.previewDrawableWidth, updateParams.previewDrawableHeight);
    }

    private void clean(String why) {
        executor.clean(why);

        if (waitUpdateParams != null) {
            waitUpdateParams.reset();
        }
        matrix.reset();
        lastScale = 0;
        scale = 0;

        tileManager.clean(why);

        callback.invalidate();
    }

    public void recycle(String why) {
        running = false;
        clean(why);
        executor.recycle(why);
        tileManager.recycle(why);
    }

    public void invalidateView(){
        callback.invalidate();
    }

    public boolean isAvailable() {
        return running && executor.isReady();
    }

    public boolean isInitializing() {
        return running && executor.isInitializing();
    }


    @SuppressWarnings("unused")
    public void setShowDrawRect(boolean showDrawRect) {
        this.showDrawRect = showDrawRect;
        callback.invalidate();
    }



    public UpdateParams getUpdateParams() {
        return updateParams;
    }

    public TileDecodeExecutor getExecutor() {
        return executor;
    }

    public float getScale() {
        return scale;
    }

    public float getLastScale() {
        return lastScale;
    }

    public TileManager getTileManager() {
        return tileManager;
    }

    public interface Callback {
        void invalidate();
    }

    public interface OnTileChangedListener {
        void onTileChanged(LargeImageViewer largeImageViewer);
    }

    private class ExecutorCallback implements TileDecodeExecutor.Callback {

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

            tileManager.decodeCompleted(tile, bitmap);
        }

        @Override
        public void onDecodeFailed(Tile tile, DecodeHandler.DecodeFailedException exception) {
            if (!running) {
                if (Sketch.isDebugMode()) {
                    Log.w(Sketch.TAG, NAME + ". stop running. decodeFailed. tile=" + tile.getInfo());
                }
                return;
            }

            tileManager.decodeFailed(tile, exception);
        }
    }
}
