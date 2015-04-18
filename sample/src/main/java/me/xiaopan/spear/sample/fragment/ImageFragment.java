package me.xiaopan.spear.sample.fragment;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import me.xiaopan.android.inject.InjectContentView;
import me.xiaopan.android.inject.InjectExtra;
import me.xiaopan.android.inject.InjectView;
import me.xiaopan.spear.CancelCause;
import me.xiaopan.spear.DisplayListener;
import me.xiaopan.spear.FailCause;
import me.xiaopan.spear.ImageFrom;
import me.xiaopan.spear.LoadListener;
import me.xiaopan.spear.Request;
import me.xiaopan.spear.Spear;
import me.xiaopan.spear.process.BlurImageProcessor;
import me.xiaopan.spear.sample.DisplayOptionsType;
import me.xiaopan.spear.sample.MyFragment;
import me.xiaopan.spear.sample.R;
import me.xiaopan.spear.sample.activity.WindowBackgroundManager;
import me.xiaopan.spear.sample.widget.MyImageView;
import uk.co.senab.photoview.PhotoViewAttacher;

@InjectContentView(R.layout.fragment_image)
public class ImageFragment extends MyFragment {
    public static final String PARAM_REQUIRED_IMAGE_URI = "PARAM_REQUIRED_IMAGE_URI";

    @InjectView(R.id.image_imageFragment_image) private MyImageView imageView;
    @InjectView(R.id.progress_imageFragment_progress) private ProgressBar progressBar;

    @InjectExtra(PARAM_REQUIRED_IMAGE_URI) private String imageUri;
    private WindowBackgroundManager.OnSetWindowBackgroundListener onSetWindowBackgroundListener;
    private Request loadBackgroundRequest;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity != null && activity instanceof WindowBackgroundManager.OnSetWindowBackgroundListener){
            onSetWindowBackgroundListener = (WindowBackgroundManager.OnSetWindowBackgroundListener) activity;
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imageView.setDisplayOptions(DisplayOptionsType.Detail);
        imageView.setAutoApplyGlobalAttr(false);
        imageView.setDisplayListener(new DisplayListener() {
            @Override
            public void onStarted() {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCompleted(ImageFrom imageFrom) {
                progressBar.setVisibility(View.GONE);
                new PhotoViewAttacher(imageView);
            }

            @Override
            public void onFailed(FailCause failCause) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCanceled(CancelCause cancelCause) {
                if(cancelCause != null && cancelCause == CancelCause.PAUSE_DOWNLOAD){
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
        imageView.displayImage(imageUri);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onSetWindowBackgroundListener = null;
    }

    @Override
    public void onUserVisibleChanged(boolean isVisibleToUser){
        if(isVisibleToUser){
            loadBackgroundRequest = applyWindowBackground(imageUri);
        }else{
            if(loadBackgroundRequest != null && !loadBackgroundRequest.isFinished()){
                loadBackgroundRequest.cancel();
                loadBackgroundRequest = null;
            }
        }
    }

    private Request applyWindowBackground(String imageUri){
        if(imageUri == null || getActivity() == null){
            return null;
        }
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

        return Spear.with(getActivity()).load(
                imageUri,
                new LoadListener() {
                    @Override
                    public void onStarted() {

                    }

                    @Override
                    public void onCompleted(final Bitmap bitmap, ImageFrom imageFrom) {
                        if(onSetWindowBackgroundListener != null && getActivity() != null){
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    if(isResumed() && getUserVisibleHint() && getActivity() != null && onSetWindowBackgroundListener != null){
                                        onSetWindowBackgroundListener.onSetWindowBackground(new BitmapDrawable(getResources(), bitmap));
                                    }else{
                                        bitmap.recycle();
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailed(FailCause failCause) {

                    }

                    @Override
                    public void onCanceled(CancelCause cancelCause) {

                    }
                }
        ).resize(displayMetrics.widthPixels, displayMetrics.heightPixels)
                .scaleType(ImageView.ScaleType.CENTER_CROP)
                .processor(new BlurImageProcessor(15, true))
                .fire();
    }
}