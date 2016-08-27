package me.xiaopan.sketchsample.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewGroup;

import me.xiaopan.sketch.SketchImageView;
import me.xiaopan.sketchsample.util.DeviceUtils;

public class MappingView extends SketchImageView {
    private RectF srcRect;
    private Paint paint;

    private int cacheSourceWidth;
    private RectF cacheSourceSrcRect;

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
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (srcRect.isEmpty()) {
            return;
        }

        canvas.drawRect(srcRect, paint);
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

    public void update(int sourceWidth, RectF sourceSrcRect) {
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
                cacheSourceSrcRect = new RectF();
            }
            cacheSourceSrcRect.set(sourceSrcRect);
            return;
        }

        int selfWidth = getWidth();
        float scale = (float) selfWidth / sourceWidth;
        this.srcRect.set(sourceSrcRect.left * scale, sourceSrcRect.top * scale, sourceSrcRect.right * scale, sourceSrcRect.bottom * scale);
        invalidate();
    }
}
