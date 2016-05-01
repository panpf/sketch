package me.xiaopan.sketch;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;

public class ShowImageFromFunction implements ImageViewFunction{
    private static final int FROM_FLAG_COLOR_MEMORY = 0x8800FF00;
    private static final int FROM_FLAG_COLOR_LOCAL = 0x880000FF;
    private static final int FROM_FLAG_COLOR_DISK_CACHE = 0x88FFFF00;
    private static final int FROM_FLAG_COLOR_NETWORK = 0x88FF0000;

    private View view;
    private RequestFunction requestFunction;

    private Path imageFromPath;
    private Paint imageFromPaint;
    private ImageFrom imageFrom;

    public ShowImageFromFunction(View view, RequestFunction requestFunction) {
        this.view = view;
        this.requestFunction = requestFunction;
    }

    @Override
    public void onAttachedToWindow() {

    }

    @Override
    public void onDisplay() {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    @Override
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (imageFromPath == null) {
            imageFromPath = new Path();
        } else {
            imageFromPath.reset();
        }
        int x = view.getWidth() / 10;
        int y = view.getWidth() / 10;
        int left2 = view.getPaddingLeft();
        int top2 = view.getPaddingTop();
        imageFromPath.moveTo(left2, top2);
        imageFromPath.lineTo(left2 + x, top2);
        imageFromPath.lineTo(left2, top2 + y);
        imageFromPath.close();
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (imageFrom == null) {
            return;
        }

        if (imageFromPath == null) {
            imageFromPath = new Path();
            int x = view.getWidth() / 10;
            int y = view.getWidth() / 10;
            int left2 = view.getPaddingLeft();
            int top2 = view.getPaddingTop();
            imageFromPath.moveTo(left2, top2);
            imageFromPath.lineTo(left2 + x, top2);
            imageFromPath.lineTo(left2, top2 + y);
            imageFromPath.close();
        }
        if (imageFromPaint == null) {
            imageFromPaint = new Paint();
            imageFromPaint.setAntiAlias(true);
        }
        switch (imageFrom) {
            case MEMORY_CACHE:
                imageFromPaint.setColor(FROM_FLAG_COLOR_MEMORY);
                break;
            case DISK_CACHE:
                imageFromPaint.setColor(FROM_FLAG_COLOR_DISK_CACHE);
                break;
            case NETWORK:
                imageFromPaint.setColor(FROM_FLAG_COLOR_NETWORK);
                break;
            case LOCAL:
                imageFromPaint.setColor(FROM_FLAG_COLOR_LOCAL);
                break;
            default:
                return;
        }
        canvas.drawPath(imageFromPath, imageFromPaint);
    }

    @Override
    public boolean onDetachedFromWindow() {
        // drawable都已经被清空了，图片来源标识当然要重置了
        imageFrom = null;
        return false;
    }

    @Override
    public boolean onDrawableChanged(String callPosition, Drawable oldDrawable, Drawable newDrawable) {
        if(!requestFunction.isNewDrawableFromSketch()){
            imageFrom = null;
            return true;
        }

        return false;
    }

    @Override
    public boolean onDisplayStarted() {
        imageFrom = null;
        return true;
    }

    @Override
    public boolean onUpdateDownloadProgress(int totalLength, int completedLength) {
        return false;
    }

    @Override
    public boolean onDisplayCompleted(ImageFrom imageFrom, String mimeType) {
        this.imageFrom = imageFrom;
        return true;
    }

    @Override
    public boolean onDisplayFailed(FailedCause failedCause) {
        imageFrom = null;
        return true;
    }

    @Override
    public boolean onCanceled(CancelCause cancelCause) {
        return false;
    }

    public ImageFrom getImageFrom() {
        return imageFrom;
    }

    public void setImageFrom(ImageFrom imageFrom) {
        this.imageFrom = imageFrom;
    }
}
