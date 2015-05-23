package me.xiaopan.sketch;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import java.lang.ref.WeakReference;

public class BindBitmapDrawable extends SketchBitmapDrawable {
    private WeakReference<DisplayRequest> displayRequestWeakReference;

    public BindBitmapDrawable(Bitmap bitmap, DisplayRequest displayRequest) {
        super(bitmap);
        displayRequestWeakReference = new WeakReference<DisplayRequest>(displayRequest);
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
            if (drawable != null && drawable instanceof BindBitmapDrawable) {
                return ((BindBitmapDrawable) drawable).getDisplayRequest();
            }
        }
        return null;
    }
}