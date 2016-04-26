package me.xiaopan.sketch;

import android.graphics.drawable.Drawable;

import java.lang.ref.WeakReference;

public class BindFixedRecycleBitmapDrawable extends FixedRecycleBitmapDrawable {
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
     *
     * @return 显示请求
     */
    public DisplayRequest getDisplayRequest() {
        return displayRequestWeakReference.get();
    }

    /**
     * 从ImageViewInterface上查找DisplayRequest
     */
    public static DisplayRequest findDisplayRequest(ImageViewInterface imageViewInterface) {
        if (imageViewInterface != null) {
            final Drawable drawable = imageViewInterface.getDrawable();
            if (drawable != null && drawable instanceof BindFixedRecycleBitmapDrawable) {
                return ((BindFixedRecycleBitmapDrawable) drawable).getDisplayRequest();
            }
        }
        return null;
    }
}