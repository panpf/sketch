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
        // 主动取消请求
        DisplayRequest potentialRequest = BindFixedRecycleBitmapDrawable.findDisplayRequest(imageViewInterface);
        if (potentialRequest != null && !potentialRequest.isFinished()) {
            potentialRequest.cancel();
        }

        // 如果当前图片是来自Sketch，那么就有可能在这里被主动回收，因此要主动设置ImageView的drawable为null
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
        // 当Drawable改变的时候旧Drawable的显示引用计数减1，新Drawable的显示引用计数加1
        oldDrawableFromSketch = notifyDrawable(callPosition + ":oldDrawable", oldDrawable, false);
        newDrawableFromSketch = notifyDrawable(callPosition + ":newDrawable", newDrawable, true);

        // 如果新Drawable不是来自Sketch，那么就要清空显示参数，防止被ResumeDisplayFunction在onAttachedToWindow的时候错误的恢复成上一张图片
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

    @SuppressWarnings("unused")
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
