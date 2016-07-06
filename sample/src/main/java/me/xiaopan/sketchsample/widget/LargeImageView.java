package me.xiaopan.sketchsample.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import me.xiaopan.sketch.SketchImageView;
import me.xiaopan.sketchsample.largeimage.LargeImageController;

public class LargeImageView extends SketchImageView {
    private LargeImageController largeImageController;

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

    private void init(){
        largeImageController = new LargeImageController(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (largeImageController != null) {
            largeImageController.onAttachedToWindow();
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (largeImageController != null) {
            largeImageController.onLayout(changed, left, top, right, bottom);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (largeImageController != null) {
            largeImageController.onDraw(canvas);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (largeImageController != null) {
            largeImageController.onDetachedFromWindow();
        }
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        if (largeImageController != null) {
            largeImageController.setImage(getDisplayParams().attrs.getUri());
        }
    }

    public void update(Matrix drawMatrix, RectF visibleRect, int drawableWidth){
        if(largeImageController != null){
            largeImageController.update(drawMatrix, visibleRect, drawableWidth);
        }
    }
}
