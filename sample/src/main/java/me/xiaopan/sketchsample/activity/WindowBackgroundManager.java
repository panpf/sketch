package me.xiaopan.sketchsample.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.drawable.RecyclerDrawable;
import me.xiaopan.sketch.request.CancelCause;
import me.xiaopan.sketch.request.FailedCause;
import me.xiaopan.sketch.request.LoadListener;
import me.xiaopan.sketch.request.LoadRequest;
import me.xiaopan.sketch.request.LoadResult;
import me.xiaopan.sketch.util.SketchUtils;
import me.xiaopan.sketchsample.OptionsType;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.util.DeviceUtils;

public class WindowBackgroundManager {
    private Activity activity;
    private Drawable oneDrawable;
    private Drawable twoDrawable;
    private String currentBackgroundUri;
    private ImageView backgroundImageView;

    public WindowBackgroundManager(Activity activity) {
        this.activity = activity;
        this.activity.getWindow().setFormat(PixelFormat.TRANSLUCENT); // 要先将Window的格式设为透明的，如果不这么做的话第一次改变Window的背景的话屏幕会快速的闪一下（黑色的）
        backgroundImageView = (ImageView) activity.findViewById(R.id.image_mainLeftMenu_background);
        if (backgroundImageView != null) {
            ViewGroup.LayoutParams layoutParams = backgroundImageView.getLayoutParams();
            layoutParams.width = activity.getResources().getDisplayMetrics().widthPixels;
            layoutParams.height = activity.getResources().getDisplayMetrics().heightPixels;
            backgroundImageView.setLayoutParams(layoutParams);
        }
    }

    public void setBackground(String currentBackgroundUri, Bitmap bitmap) {
        this.currentBackgroundUri = currentBackgroundUri;
        Drawable newDrawable = new BitmapDrawable(bitmap);
        Drawable oldOneDrawable = oneDrawable;
        Window window = activity.getWindow();
        Drawable oneDrawable = twoDrawable != null ? twoDrawable : activity.getResources().getDrawable(R.drawable.shape_window_background);
        Drawable[] drawables = new Drawable[]{oneDrawable, newDrawable};
        TransitionDrawable transitionDrawable = new TransitionDrawable(drawables);
        transitionDrawable.setCrossFadeEnabled(true);
        window.setBackgroundDrawable(transitionDrawable);
        transitionDrawable.startTransition(800);
        this.oneDrawable = oneDrawable;
        this.twoDrawable = newDrawable;
        recycleDrawable(oldOneDrawable);
        if (backgroundImageView != null) {
            backgroundImageView.setImageBitmap(bitmap);
        }
    }

    public String getCurrentBackgroundUri() {
        return currentBackgroundUri;
    }

    public void destroy() {
        recycleDrawable(oneDrawable);
        recycleDrawable(twoDrawable);
    }

    private void recycleDrawable(Drawable drawable) {
        if (drawable == null) {
            return;
        }

        if (drawable instanceof RecyclerDrawable) {
            Log.d(Sketch.TAG, "old window bitmap recycled - " + ((RecyclerDrawable) drawable).getInfo());
            ((RecyclerDrawable) drawable).recycle();
        } else if (drawable instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            if (bitmap != null && !bitmap.isRecycled()) {
                Log.d(Sketch.TAG, "old window bitmap recycled - " + SketchUtils.getInfo(null, bitmap, null));
                bitmap.recycle();
            }
        }
    }

    public interface OnSetWindowBackgroundListener {
        void onSetWindowBackground(String uri, Bitmap bitmap);

        String getCurrentBackgroundUri();
    }

    public static class WindowBackgroundLoader {
        private Context context;
        private String windowBackgroundImageUri;
        private LoadRequest loadBackgroundRequest;
        private OnSetWindowBackgroundListener onSetWindowBackgroundListener;
        private boolean userVisible;

        public WindowBackgroundLoader(Context context, OnSetWindowBackgroundListener onSetWindowBackgroundListener) {
            this.context = context;
            this.onSetWindowBackgroundListener = onSetWindowBackgroundListener;
        }

        public void restore() {
            if (onSetWindowBackgroundListener != null && windowBackgroundImageUri != null) {
                load(windowBackgroundImageUri);
            }
        }

        public void detach() {
            cancel(CancelCause.ON_DETACHED_FROM_WINDOW);
            onSetWindowBackgroundListener = null;
        }

        public void cancel(CancelCause cancelCause) {
            if (loadBackgroundRequest != null && !loadBackgroundRequest.isFinished()) {
                loadBackgroundRequest.cancel(cancelCause);
            }
        }

        public void setUserVisible(boolean userVisible) {
            this.userVisible = userVisible;
            if (userVisible && windowBackgroundImageUri != null) {
                load(windowBackgroundImageUri);
            }
        }

        public void load(final String imageUri) {
            if (imageUri == null || imageUri.equals(onSetWindowBackgroundListener.getCurrentBackgroundUri())) {
                return;
            }
            this.windowBackgroundImageUri = imageUri;
            if (loadBackgroundRequest != null && !loadBackgroundRequest.isFinished()) {
                loadBackgroundRequest.cancel(CancelCause.BE_REPLACED);
            }
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            int resizeWidth = displayMetrics.widthPixels;
            int resizeHeight = displayMetrics.heightPixels;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                resizeHeight -= DeviceUtils.getStatusBarHeight(context.getResources());
            }
            resizeWidth /= 4;
            resizeHeight /= 4;
            loadBackgroundRequest = Sketch.with(context).load(imageUri, new LoadListener() {
                @Override
                public void onStarted() {

                }

                @Override
                public void onCompleted(LoadResult loadResult) {
                    if (loadResult.getBitmap() != null) {
                        if (onSetWindowBackgroundListener != null) {
                            if (userVisible) {
                                onSetWindowBackgroundListener.onSetWindowBackground(imageUri, loadResult.getBitmap());
                            } else {
                                loadResult.getBitmap().recycle();
                            }
                        }
                    }
                }

                @Override
                public void onFailed(FailedCause failedCause) {

                }

                @Override
                public void onCanceled(CancelCause cancelCause) {

                }
            }).maxSize(resizeWidth, resizeHeight).resize(resizeWidth, resizeHeight).optionsByName(OptionsType.WINDOW_BACKGROUND).commit();
        }
    }
}
