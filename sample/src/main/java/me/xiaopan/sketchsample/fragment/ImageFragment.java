package me.xiaopan.sketchsample.fragment;

import android.app.Activity;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import me.xiaopan.androidinjector.InjectContentView;
import me.xiaopan.androidinjector.InjectExtra;
import me.xiaopan.androidinjector.InjectView;
import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.display.TransitionImageDisplayer;
import me.xiaopan.sketch.feature.large.LargeImageViewer;
import me.xiaopan.sketch.feature.zoom.ImageZoomer;
import me.xiaopan.sketch.request.CancelCause;
import me.xiaopan.sketch.request.DisplayListener;
import me.xiaopan.sketch.request.FailedCause;
import me.xiaopan.sketch.request.ImageFrom;
import me.xiaopan.sketch.request.RequestLevel;
import me.xiaopan.sketch.util.SketchUtils;
import me.xiaopan.sketchsample.MyFragment;
import me.xiaopan.sketchsample.OptionsType;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.activity.WindowBackgroundManager;
import me.xiaopan.sketchsample.menu.ImageMenu;
import me.xiaopan.sketchsample.util.Settings;
import me.xiaopan.sketchsample.widget.HintView;
import me.xiaopan.sketchsample.widget.MappingView;
import me.xiaopan.sketchsample.widget.MyImageView;

@InjectContentView(R.layout.fragment_image)
public class ImageFragment extends MyFragment {
    public static final String PARAM_REQUIRED_IMAGE_URI = "PARAM_REQUIRED_IMAGE_URI";

    @InjectView(R.id.image_imageFragment_image)
    private MyImageView imageView;

    @InjectView(R.id.mapping_imageFragment)
    private MappingView mappingView;

    @InjectView(R.id.text_imageFragment_scale)
    private TextView scaleTextView;

    @InjectView(R.id.hint_imageFragment_hint)
    private HintView hintView;

    @InjectView(R.id.layout_imageFragment_settings)
    private View settingsView;

    @InjectExtra(PARAM_REQUIRED_IMAGE_URI)
    private String imageUri;
    private boolean completedAfterUpdateBackground;

    private WindowBackgroundManager.Loader loader;

    private ImageMenu imageMenu;

    public static ImageFragment build(String imageUri) {
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_REQUIRED_IMAGE_URI, imageUri);
        ImageFragment fragment = new ImageFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

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

        imageView.setAutoApplyGlobalAttr(false);

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

        Settings settings = Settings.with(getActivity());
        imageView.setSupportZoom(settings.isSupportZoom());
        imageView.setSupportLargeImage(settings.isSupportLargeImage());

        if (imageView.isSupportZoom()) {
            ImageZoomer imageZoomer = imageView.getImageZoomer();

            // 开启阅读模式
            imageZoomer.setReadMode(settings.isReadMode());

            // MappingView跟随Matrix变化刷新各种区域
            imageZoomer.addOnMatrixChangeListener(new ImageZoomer.OnMatrixChangeListener() {
                Rect visibleRect = new Rect();

                @Override
                public void onMatrixChanged(ImageZoomer imageZoomer) {
                    imageZoomer.getVisibleRect(visibleRect);
                    mappingView.update(imageZoomer.getDrawableSize(), visibleRect);
                    String scale = String.format(" %s ·", SketchUtils.formatFloat(imageZoomer.getZoomScale(), 2));
                    scaleTextView.setText(scale);
                }
            });
        }

        if (imageView.isSupportLargeImage()) {
            // MappingView跟随碎片变化刷新碎片区域
            imageView.getLargeImageViewer().setOnTileChangedListener(new LargeImageViewer.OnTileChangedListener() {
                @Override
                public void onTileChanged(LargeImageViewer largeImageViewer) {
                    mappingView.tileChanged(largeImageViewer);
                }
            });
        }

        // 单击显示操作选项
        if (imageView.isSupportZoom()) {
            imageView.getImageZoomer().setOnViewTapListener(new ImageZoomer.OnViewTapListener() {
                @Override
                public void onViewTap(View view, float x, float y) {
                    Fragment parentFragment = getParentFragment();
                    if (parentFragment != null && parentFragment instanceof ImageZoomer.OnViewTapListener) {
                        ((ImageZoomer.OnViewTapListener) parentFragment).onViewTap(view, x, y);
                    }
                }
            });
        } else {
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment parentFragment = getParentFragment();
                    if (parentFragment != null && parentFragment instanceof ImageZoomer.OnViewTapListener) {
                        ((ImageZoomer.OnViewTapListener) parentFragment).onViewTap(v, 0, 0);
                    }
                }
            });
        }

        mappingView.setOnSingleClickListener(new MappingView.OnSingleClickListener() {
            @Override
            public boolean onSingleClick(float x, float y) {
                return location(x, y);
            }
        });

        mappingView.getOptions().setImageDisplayer(new TransitionImageDisplayer());
        mappingView.getOptions().setMaxSize(600, 600);
        mappingView.displayImage(imageUri);

        imageView.setOptionsByName(OptionsType.DETAIL);
        imageView.displayImage(imageUri);

        imageMenu = new ImageMenu(getActivity(), imageView);

        settingsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageMenu != null) {
                    imageMenu.show();
                }
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

    private boolean location(float x, float y) {
        if (!imageView.isSupportZoom()) {
            return false;
        }

        Drawable drawable = imageView.getDrawable();
        if (drawable == null ||
                drawable.getIntrinsicWidth() == 0 || drawable.getIntrinsicHeight() == 0 ||
                mappingView.getWidth() == 0 || mappingView.getHeight() == 0) {
            return false;
        }

        final float widthScale = (float) drawable.getIntrinsicWidth() / mappingView.getWidth();
        final float heightScale = (float) drawable.getIntrinsicHeight() / mappingView.getHeight();

        imageView.getImageZoomer().location(x * widthScale, y * heightScale);
        return true;
    }
}