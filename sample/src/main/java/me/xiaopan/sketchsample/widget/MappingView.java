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

package me.xiaopan.sketchsample.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewGroup;

import me.xiaopan.sketch.SLog;
import me.xiaopan.sketch.SLogType;
import me.xiaopan.sketch.SketchImageView;
import me.xiaopan.sketch.drawable.SketchLoadingDrawable;
import me.xiaopan.sketch.request.DisplayCache;
import me.xiaopan.sketch.util.SketchUtils;
import me.xiaopan.sketch.viewfun.large.LargeImageViewer;
import me.xiaopan.sketch.viewfun.large.Tile;

public class MappingView extends SketchImageView {

    private LargeImageViewer largeImageViewer;

    private Rect visibleMappingRect;
    private Paint visiblePaint;
    private Paint drawTilesPaint;
    private Paint realSrcRectPaint;
    private Paint originSrcRectPaint;
    private Paint loadingTilePaint;

    private Point drawableSize = new Point();
    private Rect visibleRect = new Rect();

    private GestureDetector detector;
    private OnSingleClickListener onSingleClickListener;

    public MappingView(Context context) {
        super(context);
        init(context);
    }

    public MappingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MappingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        visibleMappingRect = new Rect();
        visiblePaint = new Paint();
        visiblePaint.setColor(Color.RED);
        visiblePaint.setStyle(Paint.Style.STROKE);
        visiblePaint.setStrokeWidth(SketchUtils.dp2px(context, 1));

        drawTilesPaint = new Paint();
        drawTilesPaint.setColor(Color.parseColor("#88A020F0"));
        drawTilesPaint.setStrokeWidth(SketchUtils.dp2px(context, 1));
        drawTilesPaint.setStyle(Paint.Style.STROKE);

        loadingTilePaint = new Paint();
        loadingTilePaint.setColor(Color.parseColor("#880000CD"));
        loadingTilePaint.setStrokeWidth(SketchUtils.dp2px(context, 1));
        loadingTilePaint.setStyle(Paint.Style.STROKE);

        realSrcRectPaint = new Paint();
        realSrcRectPaint.setColor(Color.parseColor("#8800CD00"));
        realSrcRectPaint.setStrokeWidth(SketchUtils.dp2px(context, 1));
        realSrcRectPaint.setStyle(Paint.Style.STROKE);

        originSrcRectPaint = new Paint();
        originSrcRectPaint.setColor(Color.parseColor("#88FF7F24"));
        originSrcRectPaint.setStrokeWidth(SketchUtils.dp2px(context, 1));
        originSrcRectPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (largeImageViewer != null && largeImageViewer.isReady()) {
            float widthScale = (float) largeImageViewer.getImageSize().x / getWidth();
            float heightScale = (float) largeImageViewer.getImageSize().y / getHeight();

            for (Tile tile : largeImageViewer.getTileList()) {
                if (!tile.isEmpty()) {
                    canvas.drawRect((tile.srcRect.left + 1) / widthScale,
                            (tile.srcRect.top + 1) / heightScale,
                            (tile.srcRect.right - 1) / widthScale,
                            (tile.srcRect.bottom - 1) / heightScale, drawTilesPaint);
                } else if (!tile.isDecodeParamEmpty()) {
                    canvas.drawRect((tile.srcRect.left + 1) / widthScale,
                            (tile.srcRect.top + 1) / heightScale,
                            (tile.srcRect.right - 1) / widthScale,
                            (tile.srcRect.bottom - 1) / heightScale, loadingTilePaint);
                }
            }

            Rect drawSrcRect = largeImageViewer.getDrawSrcRect();
            if (!drawSrcRect.isEmpty()) {
                canvas.drawRect((drawSrcRect.left) / widthScale,
                        (drawSrcRect.top) / heightScale,
                        (drawSrcRect.right) / widthScale,
                        (drawSrcRect.bottom) / heightScale, originSrcRectPaint);
            }

            Rect decodeSrcRect = largeImageViewer.getDecodeSrcRect();
            if (!decodeSrcRect.isEmpty()) {
                canvas.drawRect((decodeSrcRect.left) / widthScale,
                        (decodeSrcRect.top) / heightScale,
                        (decodeSrcRect.right) / widthScale,
                        (decodeSrcRect.bottom) / heightScale, realSrcRectPaint);
            }
        }

        if (!visibleMappingRect.isEmpty()) {
            canvas.drawRect(visibleMappingRect, visiblePaint);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        recover();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        resetViewSize();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (detector == null) {
            return super.onTouchEvent(event);
        }

        detector.onTouchEvent(event);
        return true;
    }

    private String getImageUri() {
        DisplayCache displayCache = getDisplayCache();
        return displayCache != null ? displayCache.uri : null;
    }

    public void update(Point newDrawableSize, Rect newVisibleRect) {
        if (newDrawableSize.x == 0 || newDrawableSize.y == 0 || newVisibleRect.isEmpty()) {
            if (SLogType.ZOOM.isEnabled()) {
                SLog.fw(SLogType.ZOOM, "MappingView. update. drawableWidth is 0 or newVisibleRect is empty. %s. drawableSize=%s, newVisibleRect=%s",
                        getImageUri(), newDrawableSize.toString(), newVisibleRect.toShortString());
            }

            drawableSize.set(0, 0);
            visibleRect.setEmpty();

            if (!visibleMappingRect.isEmpty()) {
                visibleMappingRect.setEmpty();
                invalidate();
            }
            return;
        }

        drawableSize.set(newDrawableSize.x, newDrawableSize.y);
        visibleRect.set(newVisibleRect);

        if (!isUsableDrawable() || getWidth() == 0 || getHeight() == 0) {
            if (SLogType.ZOOM.isEnabled()) {
                SLog.w(SLogType.ZOOM, "MappingView. update. view size is 0 or getDrawable() is null. %s", getImageUri());
            }

            if (!visibleMappingRect.isEmpty()) {
                visibleMappingRect.setEmpty();
                invalidate();
            }
            return;
        }

        if (resetViewSize()) {
            return;
        }
        resetVisibleMappingRect();
        invalidate();
    }

    public void tileChanged(LargeImageViewer largeImageViewer) {
        this.largeImageViewer = largeImageViewer;
        invalidate();
    }

    public void setOnSingleClickListener(OnSingleClickListener onSingleClickListener) {
        this.onSingleClickListener = onSingleClickListener;
        setClickable(onSingleClickListener != null);
        if (detector == null) {
            detector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return MappingView.this.onSingleClickListener.onSingleClick(e.getX(), e.getY());
                }
            });
        }
    }

    private void recover() {
        if (!visibleRect.isEmpty()) {
            update(drawableSize, visibleRect);
        }
    }

    public boolean isUsableDrawable() {
        Drawable drawable = getDrawable();
        return drawable != null && !(drawable instanceof SketchLoadingDrawable);
    }

    private boolean resetViewSize() {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return true;
        }

        final int drawableWidth = drawable.getIntrinsicWidth();
        final int drawableHeight = drawable.getIntrinsicHeight();
        int maxWidth;
        int maxHeight;
        if ((float) Math.max(drawableWidth, drawableHeight) / Math.min(drawableWidth, drawableHeight) >= 4) {
            maxWidth = Math.round(getResources().getDisplayMetrics().widthPixels / 2f);
            maxHeight = Math.round(getResources().getDisplayMetrics().heightPixels / 2f);
        } else {
            maxWidth = Math.round(getResources().getDisplayMetrics().widthPixels / 4f);
            maxHeight = Math.round(getResources().getDisplayMetrics().heightPixels / 4f);
        }
        int newViewWidth;
        int newViewHeight;
        if (drawableWidth > maxWidth || drawableHeight > maxHeight) {
            float finalScale = Math.min((float) maxWidth / drawableWidth, (float) maxHeight / drawableHeight);
            newViewWidth = Math.round(drawableWidth * finalScale);
            newViewHeight = Math.round(drawableHeight * finalScale);
        } else {
            newViewWidth = drawableWidth;
            newViewHeight = drawableHeight;
        }

        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if (newViewWidth != layoutParams.width || newViewHeight != layoutParams.height) {
            layoutParams.width = newViewWidth;
            layoutParams.height = newViewHeight;
            setLayoutParams(layoutParams);

            return true;
        } else {
            return false;
        }
    }

    private void resetVisibleMappingRect() {
        int selfWidth = getWidth();
        int selfHeight = getHeight();
        final float widthScale = (float) selfWidth / drawableSize.x;
        final float heightScale = (float) selfHeight / drawableSize.y;
        this.visibleMappingRect.set(
                Math.round(visibleRect.left * widthScale),
                Math.round(visibleRect.top * heightScale),
                Math.round(visibleRect.right * widthScale),
                Math.round(visibleRect.bottom * heightScale));
    }

    public interface OnSingleClickListener {
        boolean onSingleClick(float x, float y);
    }
}
