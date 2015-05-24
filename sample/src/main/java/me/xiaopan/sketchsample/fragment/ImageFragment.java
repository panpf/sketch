package me.xiaopan.sketchsample.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import me.xiaopan.android.inject.InjectContentView;
import me.xiaopan.android.inject.InjectExtra;
import me.xiaopan.android.inject.InjectView;
import me.xiaopan.sketch.CancelCause;
import me.xiaopan.sketch.DisplayListener;
import me.xiaopan.sketch.FailCause;
import me.xiaopan.sketch.ImageFrom;
import me.xiaopan.sketch.RequestLevel;
import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketchsample.MyFragment;
import me.xiaopan.sketchsample.OptionsType;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.activity.WindowBackgroundManager;
import me.xiaopan.sketchsample.widget.HintView;
import me.xiaopan.sketchsample.widget.MyImageView;
import uk.co.senab.photoview.PhotoViewAttacher;

@InjectContentView(R.layout.fragment_image)
public class ImageFragment extends MyFragment {
    public static final String PARAM_REQUIRED_IMAGE_URI = "PARAM_REQUIRED_IMAGE_URI";

    @InjectView(R.id.image_imageFragment_image) private MyImageView imageView;
    @InjectView(R.id.hint_imageFragment_hint) private HintView hintView;

    @InjectExtra(PARAM_REQUIRED_IMAGE_URI) private String imageUri;
    private boolean completedAfterUpdateBackground;

    private WindowBackgroundManager.WindowBackgroundLoader windowBackgroundLoader;
    private PhotoViewAttacher photoViewAttacher;

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

        photoViewAttacher = new PhotoViewAttacher(imageView);
        imageView.setDisplayOptions(OptionsType.DETAIL);
        imageView.setAutoApplyGlobalAttr(false);

        imageView.setDisplayListener(new DisplayListener() {
            @Override
            public void onStarted() {
                hintView.loading("正在加载图片，请稍后...");
            }

            @Override
            public void onCompleted(ImageFrom imageFrom, String mimeType) {
                hintView.hidden();
                photoViewAttacher.update();
                if(completedAfterUpdateBackground){
                    completedAfterUpdateBackground = false;
                    if(isResumed() && getUserVisibleHint()){
                        windowBackgroundLoader.load(imageUri);
                    }
                }
            }

            @Override
            public void onFailed(FailCause failCause) {
                hintView.hint(R.drawable.ic_failure, "图片显示失败", "重新显示", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        completedAfterUpdateBackground = true;
                        imageView.displayImage(imageUri);
                    }
                });
                photoViewAttacher.update();
            }

            @Override
            public void onCanceled(CancelCause cancelCause) {
                if(cancelCause == null){
                    return;
                }

                switch (cancelCause){
                    case LEVEL_IS_LOCAL:
                        hintView.hint(R.drawable.ic_failure, "level is local", "直接显示", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                completedAfterUpdateBackground = true;
                                Sketch.with(getActivity()).display(imageUri, imageView).requestLevel(RequestLevel.NET).commit();
                            }
                        });
                        break;
                    case LEVEL_IS_MEMORY:
                        hintView.hint(R.drawable.ic_failure, "level is memory", "直接显示", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                completedAfterUpdateBackground = true;
                                Sketch.with(getActivity()).display(imageUri, imageView).requestLevel(RequestLevel.NET).commit();
                            }
                        });
                        break;
                    case NORMAL:
                        break;
                    case PAUSE_DOWNLOAD:
                        hintView.hint(R.drawable.ic_failure, "为节省流量已暂停下载新图片", "不管了，直接下载", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                completedAfterUpdateBackground = true;
                                Sketch.with(getActivity()).display(imageUri, imageView).requestLevel(RequestLevel.NET).commit();
                            }
                        });
                        break;
                    case PAUSE_LOAD:
                        hintView.hint(R.drawable.ic_failure, "已暂停加载新图片", "直接加载", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                completedAfterUpdateBackground = true;
                                Sketch.with(getActivity()).display(imageUri, imageView).requestLevel(RequestLevel.NET).commit();
                            }
                        });
                        break;
                }

                if(cancelCause != CancelCause.NORMAL){
                    photoViewAttacher.update();
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