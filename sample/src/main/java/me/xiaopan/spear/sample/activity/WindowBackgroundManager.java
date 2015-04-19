package me.xiaopan.spear.sample.activity;

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
import android.widget.ImageView;

import me.xiaopan.spear.CancelCause;
import me.xiaopan.spear.FailCause;
import me.xiaopan.spear.ImageFrom;
import me.xiaopan.spear.LoadListener;
import me.xiaopan.spear.Request;
import me.xiaopan.spear.Spear;
import me.xiaopan.spear.process.BlurImageProcessor;
import me.xiaopan.spear.sample.R;

public class WindowBackgroundManager {
    private Activity activity;
    private Drawable oneDrawable;
    private Drawable twoDrawable;

    public WindowBackgroundManager(Activity activity) {
        this.activity = activity;
        this.activity.getWindow().setFormat(PixelFormat.TRANSLUCENT); // 要先将Window的格式设为透明的，如果不这么做的话第一次改变Window的背景的话屏幕会快速的闪一下（黑色的）
    }

    public void setBackground(Drawable newDrawable) {
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

    public void destroy() {
        recycleDrawable(oneDrawable);
        recycleDrawable(twoDrawable);
    }

    private void recycleDrawable(Drawable drawable){
        if(drawable != null && drawable instanceof BitmapDrawable){
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            if(bitmap != null && !bitmap.isRecycled()){
                Log.d(Spear.TAG, "old window bitmap recycled@" + Integer.toHexString(bitmap.hashCode()));
                bitmap.recycle();
            }
        }
    }

    public interface OnSetWindowBackgroundListener {
        void onSetWindowBackground(Drawable drawable);
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
        }

        public void load(String imageUri){
            if(imageUri == null){
                return;
            }
            this.windowBackgroundImageUri = imageUri;
            if(loadBackgroundRequest != null && !loadBackgroundRequest.isFinished()){
                loadBackgroundRequest.cancel();
            }
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            loadBackgroundRequest = Spear.with(context).load(imageUri, new LoadListener() {
                @Override
                public void onStarted() {

                }

                @Override
                public void onCompleted(final Bitmap bitmap, ImageFrom imageFrom) {
                    if(onSetWindowBackgroundListener != null){
                        if(userVisible){
                            onSetWindowBackgroundListener.onSetWindowBackground(new BitmapDrawable(context.getResources(), bitmap));
                        }else{
                            bitmap.recycle();
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
                    .scaleType(ImageView.ScaleType.CENTER_CROP)
                    .processor(new BlurImageProcessor(15, true))
                    .fire();
        }
    }
}
