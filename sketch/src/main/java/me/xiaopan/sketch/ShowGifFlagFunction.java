package me.xiaopan.sketch;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;

public class ShowGifFlagFunction implements ImageViewFunction{
    private View view;

    protected boolean isGifDrawable;
    protected float gifDrawableLeft = -1;
    protected float gifDrawableTop = -1;
    protected Drawable gifFlagDrawable;

    public ShowGifFlagFunction(View view, Drawable gifFlagDrawable) {
        this.view = view;

        this.gifFlagDrawable = gifFlagDrawable;
        this.gifFlagDrawable.setBounds(0, 0, this.gifFlagDrawable.getIntrinsicWidth(), this.gifFlagDrawable.getIntrinsicHeight());
    }

    @Override
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        initLeftAndTop();
    }

    @Override
    public void draw(Canvas canvas) {
        if (!isGifDrawable) {
            return;
        }

        if(gifDrawableLeft == -1 || gifDrawableTop == -1){
            initLeftAndTop();
        }

        canvas.save();
        canvas.translate(gifDrawableLeft, gifDrawableTop);
        gifFlagDrawable.draw(canvas);
        canvas.restore();
    }

    @Override
    public boolean onDisplayStarted() {
        return false;
    }

    @Override
    public boolean onDisplayCompleted(ImageFrom imageFrom, String mimeType) {
        return false;
    }

    @Override
    public boolean onDisplayFailed(FailedCause failedCause) {
        return false;
    }

    public void setIsGifDrawable(boolean gifDrawable) {
        isGifDrawable = gifDrawable;
    }

    public Drawable getGifFlagDrawable() {
        return gifFlagDrawable;
    }

    private void initLeftAndTop(){
        gifDrawableLeft = view.getWidth() - view.getPaddingRight() - gifFlagDrawable.getIntrinsicWidth();
        gifDrawableTop = view.getHeight() - view.getPaddingBottom() - gifFlagDrawable.getIntrinsicHeight();
    }
}
