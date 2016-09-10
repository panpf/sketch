package me.xiaopan.sketchsample.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import me.xiaopan.androidinjector.InjectContentView;
import me.xiaopan.androidinjector.InjectExtra;
import me.xiaopan.androidinjector.InjectView;
import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.feature.zoom.ImageZoomer;
import me.xiaopan.sketch.request.CancelCause;
import me.xiaopan.sketch.request.DisplayListener;
import me.xiaopan.sketch.request.FailedCause;
import me.xiaopan.sketch.request.ImageFrom;
import me.xiaopan.sketch.request.RequestLevel;
import me.xiaopan.sketchsample.MyFragment;
import me.xiaopan.sketchsample.OptionsType;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.activity.WindowBackgroundManager;
import me.xiaopan.sketchsample.menu.ImageMenu;
import me.xiaopan.sketchsample.widget.HintView;
import me.xiaopan.sketchsample.widget.MyImageView;

@InjectContentView(R.layout.fragment_image)
public class ImageFragment extends MyFragment {
    public static final String PARAM_REQUIRED_IMAGE_URI = "PARAM_REQUIRED_IMAGE_URI";

    @InjectView(R.id.image_imageFragment_image)
    private MyImageView imageView;
    @InjectView(R.id.hint_imageFragment_hint)
    private HintView hintView;

    @InjectExtra(PARAM_REQUIRED_IMAGE_URI)
    private String imageUri;
    private boolean completedAfterUpdateBackground;

    private WindowBackgroundManager.Loader loader;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity != null && activity instanceof WindowBackgroundManager.OnSetListener) {
            loader = new WindowBackgroundManager.Loader(activity.getBaseContext(), (WindowBackgroundManager.OnSetListener) activity);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imageView.setOptionsByName(OptionsType.DETAIL);
        imageView.setAutoApplyGlobalAttr(false);
        imageView.setSupportZoom(true);
        imageView.setSupportLargeImage(true);
        imageView.getImageZoomFunction().getImageZoomer().setReadMode(true);

        imageView.setDisplayListener(new DisplayListener() {
            @Override
            public void onStarted() {
                hintView.loading("正在加载图片，请稍后...");
            }

            @Override
            public void onCompleted(ImageFrom imageFrom, String mimeType) {
                hintView.hidden();
                if (completedAfterUpdateBackground) {
                    completedAfterUpdateBackground = false;
                    if (isResumed() && getUserVisibleHint()) {
                        loader.load(imageUri);
                    }
                }
            }

            @Override
            public void onFailed(FailedCause failedCause) {
                hintView.hint(R.drawable.ic_failed, "图片显示失败", "重新显示", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        completedAfterUpdateBackground = true;
                        imageView.displayImage(imageUri);
                    }
                });
            }

            @Override
            public void onCanceled(CancelCause cancelCause) {
                if (cancelCause == null) {
                    return;
                }

                switch (cancelCause) {
                    case REQUEST_LEVEL_IS_LOCAL:
                        hintView.hint(R.drawable.ic_failed, "level is local", "直接显示", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                completedAfterUpdateBackground = true;
                                Sketch.with(getActivity()).display(imageUri, imageView).requestLevel(RequestLevel.NET).commit();
                            }
                        });
                        break;
                    case REQUEST_LEVEL_IS_MEMORY:
                        hintView.hint(R.drawable.ic_failed, "level is memory", "直接显示", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                completedAfterUpdateBackground = true;
                                Sketch.with(getActivity()).display(imageUri, imageView).requestLevel(RequestLevel.NET).commit();
                            }
                        });
                        break;
                    case BE_CANCELLED:
                        break;
                    case PAUSE_DOWNLOAD:
                        hintView.hint(R.drawable.ic_failed, "为节省流量已暂停下载新图片", "不管了，直接下载", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                completedAfterUpdateBackground = true;
                                Sketch.with(getActivity()).display(imageUri, imageView).requestLevel(RequestLevel.NET).commit();
                            }
                        });
                        break;
                    case PAUSE_LOAD:
                        hintView.hint(R.drawable.ic_failed, "已暂停加载新图片", "直接加载", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                completedAfterUpdateBackground = true;
                                Sketch.with(getActivity()).display(imageUri, imageView).requestLevel(RequestLevel.NET).commit();
                            }
                        });
                        break;
                }
            }
        });
        imageView.displayImage(imageUri);

        imageView.getImageZoomFunction().getImageZoomer().setOnViewTapListener(new ImageZoomer.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                Fragment parentFragment = getParentFragment();
                if (parentFragment != null && parentFragment instanceof ImageZoomer.OnViewTapListener) {
                    ((ImageZoomer.OnViewTapListener) parentFragment).onViewTap(view, x, y);
                }
            }
        });

        imageView.getImageZoomFunction().getImageZoomer().setOnViewLongPressListener(new ImageZoomer.OnViewLongPressListener() {
            @Override
            public void onViewLongPress(View view, float x, float y) {
                new ImageMenu(getActivity(), imageView).show();
            }
        });
    }

    @Override
    public void onDetach() {
        if (loader != null) {
            loader.detach();
        }
        super.onDetach();
    }

    @Override
    public void onUserVisibleChanged(boolean isVisibleToUser) {
        if (loader != null) {
            loader.setUserVisible(isVisibleToUser);
            if (isVisibleToUser) {
                loader.load(imageUri);
            } else {
                loader.cancel(CancelCause.USERS_NOT_VISIBLE);
            }
        }
    }

    public MyImageView getImageView() {
        return imageView;
    }
}