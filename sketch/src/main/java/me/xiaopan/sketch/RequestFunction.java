package me.xiaopan.sketch;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.MotionEvent;

public class RequestFunction implements ImageViewFunction{
    private ImageViewInterface imageViewInterface;

    private DisplayOptions displayOptions = new DisplayOptions();
    private DisplayParams displayParams;

    private boolean oldDrawableFromSketch;
    private boolean newDrawableFromSketch;

    public RequestFunction(ImageViewInterface imageViewInterface) {
        this.imageViewInterface = imageViewInterface;
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

    }

    @Override
    public void onDraw(Canvas canvas) {

    }

    @Override
    public boolean onDetachedFromWindow() {
        DisplayRequest potentialRequest = BindFixedRecycleBitmapDrawable.findDisplayRequest(imageViewInterface);
        if (potentialRequest != null && !potentialRequest.isFinished()) {
            potentialRequest.cancel();
        }

        final Drawable oldDrawable = imageViewInterface.getDrawable();
        return oldDrawable != null && notifyDrawable("onDetachedFromWindow", oldDrawable, false);
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

    @Override
    public boolean onDrawableChanged(String callPosition, Drawable oldDrawable, Drawable newDrawable){
        oldDrawableFromSketch = notifyDrawable(callPosition + ":oldDrawable", oldDrawable, false);
        newDrawableFromSketch = notifyDrawable(callPosition + ":newDrawable", newDrawable, true);

        if(!newDrawableFromSketch){
            displayParams = null;
        }

        return false;
    }

    public DisplayParams getDisplayParams() {
        return displayParams;
    }

    public void setDisplayParams(DisplayParams displayParams) {
        this.displayParams = displayParams;
    }

    public boolean isOldDrawableFromSketch() {
        return oldDrawableFromSketch;
    }

    public boolean isNewDrawableFromSketch() {
        return newDrawableFromSketch;
    }

    public DisplayOptions getDisplayOptions() {
        return displayOptions;
    }

    /**
     * 修改Drawable显示状态
     *
     * @param callingStation 调用位置
     * @param drawable       Drawable
     * @param isDisplayed    是否已显示
     * @return true：drawable或其子Drawable是RecycleDrawable
     */
    private static boolean notifyDrawable(String callingStation, Drawable drawable, final boolean isDisplayed) {
        if (drawable == null) {
            return false;
        } else if (drawable instanceof BindFixedRecycleBitmapDrawable) {
            BindFixedRecycleBitmapDrawable bindFixedRecycleBitmapDrawable = (BindFixedRecycleBitmapDrawable) drawable;
            DisplayRequest displayRequest = bindFixedRecycleBitmapDrawable.getDisplayRequest();
            if (displayRequest != null && !displayRequest.isFinished()) {
                displayRequest.cancel();
            }
            bindFixedRecycleBitmapDrawable.setIsDisplayed(callingStation, isDisplayed);
            return true;
        } else if (drawable instanceof RecycleDrawable) {
            ((RecycleDrawable) drawable).setIsDisplayed(callingStation, isDisplayed);
            return true;
        } else if (drawable instanceof LayerDrawable) {
            LayerDrawable layerDrawable = (LayerDrawable) drawable;
            boolean result = false;
            for (int i = 0, z = layerDrawable.getNumberOfLayers(); i < z; i++) {
                result |= notifyDrawable(callingStation, layerDrawable.getDrawable(i), isDisplayed);
            }
            return result;
        } else {
            return false;
        }
    }
}
