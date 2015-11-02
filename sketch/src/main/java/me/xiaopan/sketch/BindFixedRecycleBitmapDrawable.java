package me.xiaopan.sketch;

import android.graphics.drawable.Drawable;

import java.lang.ref.WeakReference;

public class BindFixedRecycleBitmapDrawable extends FixedRecycleBitmapDrawable{
    private WeakReference<DisplayRequest> displayRequestWeakReference;

    public BindFixedRecycleBitmapDrawable(RecycleBitmapDrawable recycleBitmapDrawable, DisplayRequest displayRequest) {
        super(recycleBitmapDrawable, null);
        this.displayRequestWeakReference = new WeakReference<DisplayRequest>(displayRequest);
    }

    public BindFixedRecycleBitmapDrawable(RecycleBitmapDrawable recycleBitmapDrawable, FixedSize fixedSize, DisplayRequest displayRequest) {
        super(recycleBitmapDrawable, fixedSize);
        this.displayRequestWeakReference = new WeakReference<DisplayRequest>(displayRequest);
    }

    /**
     * 获取显示请求
     * @return 显示请求
     */
    public DisplayRequest getDisplayRequest() {
        return displayRequestWeakReference.get();
    }

    /**
     * 获取与给定SketchImageViewInterface所持有的DisplayRequest
     * @param sketchImageViewInterface SketchImageViewInterface
     * @return 给定ImageView所持有的DisplayRequest
     */
    public static DisplayRequest getDisplayRequestBySketchImageInterface(SketchImageViewInterface sketchImageViewInterface) {
        if (sketchImageViewInterface != null) {
            final Drawable drawable = sketchImageViewInterface.getDrawable();
            if (drawable != null && drawable instanceof BindFixedRecycleBitmapDrawable) {
                return ((BindFixedRecycleBitmapDrawable) drawable).getDisplayRequest();
            }
        }
        return null;
    }
}