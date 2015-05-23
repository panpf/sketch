package me.xiaopan.sketchsample.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;

import me.xiaopan.sketch.CancelCause;
import me.xiaopan.sketch.FailCause;
import me.xiaopan.sketch.ImageFrom;
import me.xiaopan.sketch.LoadListener;
import me.xiaopan.sketch.RecycleBitmapDrawable;
import me.xiaopan.sketch.RecycleDrawableInterface;
import me.xiaopan.sketch.Request;
import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketchsample.OptionsType;
import me.xiaopan.sketchsample.R;

public class WindowBackgroundManager {
    private Activity activity;
    private Drawable oneDrawable;
    private Drawable twoDrawable;
    private String currentBackgroundUri;

    public WindowBackgroundManager(Activity activity) {
        this.activity = activity;
        this.activity.getWindow().setFormat(PixelFormat.TRANSLUCENT); // 要先将Window的格式设为透明的，如果不这么做的话第一次改变Window的背景的话屏幕会快速的闪一下（黑色的）
    }

    public void setBackground(String currentBackgroundUri, Drawable newDrawable) {
        this.currentBackgroundUri = currentBackgroundUri;
        Drawable oldOneDrawable = oneDrawable;
        Window window = activity.getWindow();
        Drawable oneDrawable = twoDrawable!=null?twoDrawable:activity.getResources().getDrawable(R.drawable.shape_window_background);
        Drawable[] drawables = new Drawable[]{oneDrawable, newDrawable};
        TransitionDrawable transitionDrawable = new TransitionDrawable(drawables);
        transitionDrawable.setCrossFadeEnabled(true);
        window.setBackgroundDrawable(transitionDrawable);
        transitionDrawable.startTransition(800);
        this.oneDrawable = oneDrawable;
        this.twoDrawable = newDrawable;
        recycleDrawable(oldOneDrawable);
    }

    public String getCurrentBackgroundUri() {
        return currentBackgroundUri;
    }

    public void destroy() {
        recycleDrawable(oneDrawable);
        recycleDrawable(twoDrawable);
    }

    private void recycleDrawable(Drawable drawable){
        if(drawable == null){
            return;
        }

        if(drawable instanceof RecycleDrawableInterface){
            Log.d(Sketch.TAG, "old window bitmap recycled - " + ((RecycleDrawableInterface) drawable).getInfo());
            ((RecycleDrawableInterface) drawable).recycle();
        }else if(drawable instanceof BitmapDrawable){
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            if(bitmap != null && !bitmap.isRecycled()){
                Log.d(Sketch.TAG, "old window bitmap recycled - " + RecycleBitmapDrawable.getInfo(bitmap, null));
                bitmap.recycle();
            }
        }
    }

    public interface OnSetWindowBackgroundListener {
        void onSetWindowBackground(String uri, Drawable drawable);
        String getCurrentBackgroundUri();
    }

    public static class WindowBackgroundLoader {
        private Context context;
        private String windowBackgroundImageUri;
        private Request loadBackgroundRequest;
        private OnSetWindowBackgroundListener onSetWindowBackgroundListener;
        private boolean userVisible;

        public WindowBackgroundLoader(Context context, OnSetWindowBackgroundListener onSetWindowBackgroundListener) {
            this.context = context;
            this.onSetWindowBackgroundListener = onSetWindowBackgroundListener;
        }

        public void restore(){
            if(onSetWindowBackgroundListener != null && windowBackgroundImageUri != null){
                load(windowBackgroundImageUri);
            }
        }

        public void detach(){
            cancel();
            onSetWindowBackgroundListener = null;
        }

        public void cancel(){
            if(loadBackgroundRequest != null && !loadBackgroundRequest.isFinished()){
                loadBackgroundRequest.cancel();
            }
        }

        public void setUserVisible(boolean userVisible) {
            this.userVisible = userVisible;
            if(userVisible && windowBackgroundImageUri != null){
                load(windowBackgroundImageUri);
            }
        }

        public void load(final String imageUri){
            if(imageUri == null || imageUri.equals(onSetWindowBackgroundListener.getCurrentBackgroundUri())){
                return;
            }
            this.windowBackgroundImageUri = imageUri;
            if(loadBackgroundRequest != null && !loadBackgroundRequest.isFinished()){
                loadBackgroundRequest.cancel();
            }
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            loadBackgroundRequest = Sketch.with(context).load(imageUri, new LoadListener() {
                @Override
                public void onStarted() {

                }

                @Override
                public void onCompleted(Drawable gifDrawable, ImageFrom imageFrom, String mimeType) {
                    if(onSetWindowBackgroundListener != null){
                        if(userVisible){
                            onSetWindowBackgroundListener.onSetWindowBackground(imageUri, gifDrawable);
                        }else{
                            ((RecycleDrawableInterface) gifDrawable).recycle();
                        }
                    }
                }

                @Override
                public void onFailed(FailCause failCause) {

                }

                @Override
                public void onCanceled(CancelCause cancelCause) {

                }
            }).resize(displayMetrics.widthPixels/2, displayMetrics.heightPixels/2)
                    .options(OptionsType.WINDOW_BACKGROUND)
                    .commit();
        }
    }
}
