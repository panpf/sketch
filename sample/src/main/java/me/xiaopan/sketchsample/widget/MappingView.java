package me.xiaopan.sketchsample.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewGroup;

import me.xiaopan.sketch.SketchImageView;
import me.xiaopan.sketch.feature.large.SuperLargeImageViewer;
import me.xiaopan.sketch.feature.large.Tile;
import me.xiaopan.sketchsample.util.DeviceUtils;

public class MappingView extends SketchImageView implements SuperLargeImageViewer.OnTileChangedListener{
    private RectF srcRect;
    private Paint paint;

    private int cacheSourceWidth;
    private Rect cacheSourceSrcRect;

    private Paint drawTilesPaint;
    private Paint drawRectPaint;
    private Paint loadingStrokePaint;
    private SuperLargeImageViewer superLargeImageViewer;

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
        srcRect = new RectF();
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(DeviceUtils.dp2px(context, 1));

        setScaleType(ScaleType.FIT_XY);


        drawTilesPaint = new Paint();
        drawTilesPaint.setColor(Color.parseColor("#88A020F0"));
        drawTilesPaint.setStrokeWidth(DeviceUtils.dp2px(context, 1));
        drawTilesPaint.setStyle(Paint.Style.STROKE);

        loadingStrokePaint = new Paint();
        loadingStrokePaint.setColor(Color.parseColor("#880000CD"));
        loadingStrokePaint.setStrokeWidth(DeviceUtils.dp2px(context, 1));
        loadingStrokePaint.setStyle(Paint.Style.STROKE);

        drawRectPaint = new Paint();
        drawRectPaint.setColor(Color.parseColor("#8800CD00"));
        drawRectPaint.setStrokeWidth(DeviceUtils.dp2px(context, 1));
        drawRectPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        if (superLargeImageViewer != null) {
            float scale = (float) superLargeImageViewer.getExecutor().getDecoder().getImageWidth() / getWidth();

            for (Tile drawTile : superLargeImageViewer.getDrawTileList()) {
                if (!drawTile.isEmpty()) {
                    canvas.drawRect((drawTile.srcRect.left + 1) / scale,
                            (drawTile.srcRect.top + 1) / scale,
                            (drawTile.srcRect.right - 1) / scale,
                            (drawTile.srcRect.bottom - 1) / scale, drawTilesPaint);
                }
            }

            for (Tile loadingTile : superLargeImageViewer.getLoadingTileList()) {
                if (!loadingTile.isDecodeParamEmpty()) {
                    canvas.drawRect((loadingTile.srcRect.left + 1) / scale,
                            (loadingTile.srcRect.top + 1) / scale,
                            (loadingTile.srcRect.right - 1) / scale,
                            (loadingTile.srcRect.bottom - 1) / scale, loadingStrokePaint);
                }
            }

            Rect drawRect = superLargeImageViewer.getSrcRect();
            canvas.drawRect((drawRect.left) / scale,
                    (drawRect.top) / scale,
                    (drawRect.right) / scale,
                    (drawRect.bottom) / scale, drawRectPaint);
        }

        if (!srcRect.isEmpty()) {
            canvas.drawRect(srcRect, paint);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (cacheSourceWidth != 0 && cacheSourceSrcRect != null && !cacheSourceSrcRect.isEmpty()) {
            update(cacheSourceWidth, cacheSourceSrcRect);
            cacheSourceWidth = 0;
            cacheSourceSrcRect.setEmpty();
        }
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        int maxWidth = getResources().getDisplayMetrics().widthPixels / 2;
        int maxHeight = getResources().getDisplayMetrics().heightPixels / 2;
        int drawableWidth = drawable.getIntrinsicWidth();
        int drawableHeight = drawable.getIntrinsicHeight();
        if (drawableWidth > maxWidth || drawableHeight > maxHeight) {
            float finalScale = Math.min((float) maxWidth / drawableWidth, (float) maxHeight / drawableHeight);
            ViewGroup.LayoutParams layoutParams = getLayoutParams();
            layoutParams.width = (int) (drawableWidth * finalScale);
            layoutParams.height = (int) (drawableHeight * finalScale);
            setLayoutParams(layoutParams);
        }

        super.setImageDrawable(drawable);
    }

    public void update(int sourceWidth, Rect sourceSrcRect) {
        if (sourceWidth == 0 || sourceSrcRect.isEmpty()) {
            if (!srcRect.isEmpty()) {
                srcRect.setEmpty();
                invalidate();
            }
            return;
        }

        if (getWidth() == 0 || getDrawable() == null) {
            if (!srcRect.isEmpty()) {
                srcRect.setEmpty();
                invalidate();
            }

            cacheSourceWidth = sourceWidth;
            if (cacheSourceSrcRect == null) {
                cacheSourceSrcRect = new Rect();
            }
            cacheSourceSrcRect.set(sourceSrcRect);
            return;
        }

        int selfWidth = getWidth();
        float scale = (float) selfWidth / sourceWidth;
        this.srcRect.set(sourceSrcRect.left * scale, sourceSrcRect.top * scale, sourceSrcRect.right * scale, sourceSrcRect.bottom * scale);
        invalidate();
    }

    @Override
    public void onTileChanged(SuperLargeImageViewer superLargeImageViewer) {
        this.superLargeImageViewer = superLargeImageViewer;
        invalidate();
    }
}
