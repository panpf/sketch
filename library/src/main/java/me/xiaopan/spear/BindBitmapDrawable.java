package me.xiaopan.spear;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

public class BindBitmapDrawable extends SrcBitmapDrawable {
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
     * 获取与给定ImageView所持有的DisplayRequst
     * @param imageView ImageView
     * @return 给定ImageView所持有的DisplayRequst
     */
    public static DisplayRequest getDisplayRequestByImageView(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable != null && drawable instanceof BindBitmapDrawable) {
                return ((BindBitmapDrawable) drawable).getDisplayRequest();
            }
        }
        return null;
    }
}