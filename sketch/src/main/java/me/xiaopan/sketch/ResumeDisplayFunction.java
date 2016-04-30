package me.xiaopan.sketch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;

import me.xiaopan.sketch.util.SketchUtils;

public class ResumeDisplayFunction implements ImageViewFunction{
    private Context context;
    private RequestFunction requestFunction;
    private ImageViewInterface imageViewInterface;

    private boolean isSetImage;

    public ResumeDisplayFunction(Context context, ImageViewInterface imageViewInterface, RequestFunction requestFunction) {
        this.context = context;
        this.imageViewInterface = imageViewInterface;
        this.requestFunction = requestFunction;
    }

    @Override
    public void onAttachedToWindow() {
        if (isSetImage) {
            return;
        }

        DisplayParams displayParams = requestFunction.getDisplayParams();
        if(displayParams == null){
            return;
        }

        if (Sketch.isDebugMode()) {
            Log.w(Sketch.TAG, SketchUtils.concat(SketchImageView.NAME, "：", "restore image on attached to window", " - ", displayParams.uri));
        }
        Sketch.with(context).display(displayParams, imageViewInterface).commit();
    }

    @Override
    public void onDisplay() {
        isSetImage = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    @Override
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {

    }

    @Override
    public void onDraw(Canvas canvas) {

    }

    @Override
    public boolean onDetachedFromWindow() {
        this.isSetImage = false;
        return false;
    }

    @Override
    public boolean onDrawableChanged(String callPosition, Drawable oldDrawable, Drawable newDrawable) {
        return false;
    }

    @Override
    public boolean onDisplayStarted() {
        return false;
    }

    @Override
    public boolean onUpdateDownloadProgress(int totalLength, int completedLength) {
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

    @Override
    public boolean onCanceled(CancelCause cancelCause) {
        return false;
    }
}
