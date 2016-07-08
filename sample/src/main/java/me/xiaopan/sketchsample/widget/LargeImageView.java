package me.xiaopan.sketchsample.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import me.xiaopan.sketch.SketchImageView;
import me.xiaopan.sketchsample.large.LargeImageDisplay;

public class LargeImageView extends SketchImageView {
    private LargeImageDisplay largeImageDisplay;

    public LargeImageView(Context context) {
        super(context);
        init();
    }

    public LargeImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LargeImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        largeImageDisplay = new LargeImageDisplay(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (largeImageDisplay != null) {
            largeImageDisplay.onAttachedToWindow();
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (largeImageDisplay != null) {
            largeImageDisplay.onLayout(changed, left, top, right, bottom);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (largeImageDisplay != null) {
            largeImageDisplay.onDraw(canvas);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (largeImageDisplay != null) {
            largeImageDisplay.onDetachedFromWindow();
        }
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        if (largeImageDisplay != null) {
            largeImageDisplay.setImage(getDisplayParams().attrs.getUri());
        }
    }

    public void update(Matrix drawMatrix, RectF visibleRect, int drawableWidth, int drawableHeight) {
        if (largeImageDisplay != null) {
            largeImageDisplay.update(drawMatrix, visibleRect, drawableWidth, drawableHeight);
        }
    }
}
