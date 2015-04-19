package me.xiaopan.spear.sample.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import me.xiaopan.android.inject.InjectContentView;
import me.xiaopan.android.inject.InjectExtra;
import me.xiaopan.android.inject.InjectView;
import me.xiaopan.spear.CancelCause;
import me.xiaopan.spear.DisplayListener;
import me.xiaopan.spear.FailCause;
import me.xiaopan.spear.ImageFrom;
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

    private WindowBackgroundManager.WindowBackgroundLoader windowBackgroundLoader;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity != null && activity instanceof WindowBackgroundManager.OnSetWindowBackgroundListener){
            windowBackgroundLoader = new WindowBackgroundManager.WindowBackgroundLoader(activity.getBaseContext(), (WindowBackgroundManager.OnSetWindowBackgroundListener) activity);
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
                if (cancelCause != null && cancelCause == CancelCause.PAUSE_DOWNLOAD) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
        imageView.displayImage(imageUri);
    }

    @Override
    public void onDetach() {
        if(windowBackgroundLoader != null){
            windowBackgroundLoader.detach();
        }
        super.onDetach();
    }

    @Override
    public void onUserVisibleChanged(boolean isVisibleToUser){
        if(windowBackgroundLoader != null){
            windowBackgroundLoader.setUserVisible(isVisibleToUser);
            if(isVisibleToUser){
                windowBackgroundLoader.load(imageUri);
            }else{
                windowBackgroundLoader.cancel();
            }
        }
    }
}