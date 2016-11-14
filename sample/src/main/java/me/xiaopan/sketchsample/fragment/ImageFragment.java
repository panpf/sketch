package me.xiaopan.sketchsample.fragment;

import android.app.Activity;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import me.xiaopan.androidinjector.InjectContentView;
import me.xiaopan.androidinjector.InjectExtra;
import me.xiaopan.androidinjector.InjectView;
import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.cache.MemoryCache;
import me.xiaopan.sketch.display.FadeInImageDisplayer;
import me.xiaopan.sketch.drawable.RefBitmap;
import me.xiaopan.sketch.feature.large.LargeImageViewer;
import me.xiaopan.sketch.feature.zoom.ImageZoomer;
import me.xiaopan.sketch.request.CancelCause;
import me.xiaopan.sketch.request.DisplayListener;
import me.xiaopan.sketch.request.DisplayOptions;
import me.xiaopan.sketch.request.ErrorCause;
import me.xiaopan.sketch.request.ImageFrom;
import me.xiaopan.sketch.request.RequestLevel;
import me.xiaopan.sketch.state.MemoryCacheStateImage;
import me.xiaopan.sketch.util.SketchUtils;
import me.xiaopan.sketchsample.MyFragment;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.activity.ApplyBackgroundCallback;
import me.xiaopan.sketchsample.menu.ImageMenu;
import me.xiaopan.sketchsample.util.Settings;
import me.xiaopan.sketchsample.widget.HintView;
import me.xiaopan.sketchsample.widget.MappingView;
import me.xiaopan.sketchsample.widget.MyImageView;

@InjectContentView(R.layout.fragment_image)
public class ImageFragment extends MyFragment {
    public static final String PARAM_REQUIRED_IMAGE_URI = "PARAM_REQUIRED_IMAGE_URI";
    public static final String PARAM_REQUIRED_LOADING_IMAGE_OPTIONS_ID = "PARAM_REQUIRED_LOADING_IMAGE_OPTIONS_ID";

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

    @InjectExtra(PARAM_REQUIRED_LOADING_IMAGE_OPTIONS_ID)
    private String loadingImageOptionsId;

    private ApplyBackgroundCallback applyBackgroundCallback;

    private ImageMenu imageMenu;

    public static ImageFragment build(String imageUri, String loadingImageOptionsId) {
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_REQUIRED_IMAGE_URI, imageUri);
        bundle.putString(PARAM_REQUIRED_LOADING_IMAGE_OPTIONS_ID, loadingImageOptionsId);
        ImageFragment fragment = new ImageFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof ApplyBackgroundCallback) {
            applyBackgroundCallback = (ApplyBackgroundCallback) activity;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imageView.setAutoApplyGlobalAttr(false);

        setupZoomAndLargeImage();

        // 配置选项，有占位图选项信息的话就使用内存缓存占位图但不使用任何显示器，否则就是用渐入显示器
        DisplayOptions options = imageView.getOptions();
        if (!TextUtils.isEmpty(loadingImageOptionsId)) {
            String loadingImageMemoryCacheKey = SketchUtils.makeRequestId(imageUri, loadingImageOptionsId);
            MemoryCache memoryCache = Sketch.with(getActivity()).getConfiguration().getMemoryCache();
            RefBitmap cachedRefBitmap = memoryCache.get(loadingImageMemoryCacheKey);
            if (cachedRefBitmap != null) {
                options.setLoadingImage(new MemoryCacheStateImage(loadingImageMemoryCacheKey, null));
            } else {
                options.setImageDisplayer(new FadeInImageDisplayer());
            }
        } else {
            options.setImageDisplayer(new FadeInImageDisplayer());
        }

        // 设置可以显示GIF图
        imageView.getOptions().setDecodeGifImage(true);

        imageView.setDisplayListener(new DisplayListener() {
            @Override
            public void onStarted() {
                hintView.loading("正在加载图片，请稍后...");
            }

            @Override
            public void onCompleted(ImageFrom imageFrom, String mimeType) {
                hintView.hidden();

                if (applyBackgroundCallback != null && isVisibleToUser()) {
                    applyBackgroundCallback.onApplyBackground(imageUri);
                }
            }

            @Override
            public void onError(ErrorCause errorCause) {
                hintView.hint(R.drawable.ic_error, "图片显示失败", "重新显示", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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
                        hintView.hint(R.drawable.ic_error, "level is local", "直接显示", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                RequestLevel requestLevel = imageView.getOptions().getRequestLevel();
                                imageView.getOptions().setRequestLevel(RequestLevel.NET);
                                imageView.displayImage(imageUri);
                                imageView.getOptions().setRequestLevel(requestLevel);
                            }
                        });
                        break;
                    case REQUEST_LEVEL_IS_MEMORY:
                        hintView.hint(R.drawable.ic_error, "level is memory", "直接显示", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                RequestLevel requestLevel = imageView.getOptions().getRequestLevel();
                                imageView.getOptions().setRequestLevel(RequestLevel.NET);
                                imageView.displayImage(imageUri);
                                imageView.getOptions().setRequestLevel(requestLevel);
                            }
                        });
                        break;
                    case BE_CANCELLED:
                        break;
                    case PAUSE_DOWNLOAD:
                        hintView.hint(R.drawable.ic_error, "为节省流量已暂停下载新图片", "不管了，直接下载", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                RequestLevel requestLevel = imageView.getOptions().getRequestLevel();
                                imageView.getOptions().setRequestLevel(RequestLevel.NET);
                                imageView.displayImage(imageUri);
                                imageView.getOptions().setRequestLevel(requestLevel);
                            }
                        });
                        break;
                    case PAUSE_LOAD:
                        hintView.hint(R.drawable.ic_error, "已暂停加载新图片", "直接加载", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                RequestLevel requestLevel = imageView.getOptions().getRequestLevel();
                                imageView.getOptions().setRequestLevel(RequestLevel.NET);
                                imageView.displayImage(imageUri);
                                imageView.getOptions().setRequestLevel(requestLevel);
                            }
                        });
                        break;
                }
            }
        });

        imageView.displayImage(imageUri);

        // 点击MappingView定位到指定位置
        mappingView.setOnSingleClickListener(new MappingView.OnSingleClickListener() {
            @Override
            public boolean onSingleClick(float x, float y) {
                return location(x, y);
            }
        });

        mappingView.getOptions().setImageDisplayer(new FadeInImageDisplayer());
        mappingView.getOptions().setMaxSize(600, 600);
        mappingView.displayImage(imageUri);

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
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    @Override
    public void onUserVisibleChanged(boolean isVisibleToUser) {
        if (applyBackgroundCallback != null && isVisibleToUser) {
            applyBackgroundCallback.onApplyBackground(imageUri);
        }

        // 不可见的时候暂停超大图查看器，节省内存
        if (Settings.getBoolean(getActivity(), Settings.PREFERENCE_PAGE_VISIBLE_TO_USER_DECODE_LARGE_IMAGE)) {
            if (imageView != null && imageView.isSupportLargeImage()) {
                imageView.getLargeImageViewer().setPause(!isVisibleToUser);
            }
        } else {
            if (imageView != null && imageView.isSupportLargeImage()
                    && isVisibleToUser && imageView.getLargeImageViewer().isPaused()) {
                imageView.getLargeImageViewer().setPause(false);
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

        imageView.getImageZoomer().location(x * widthScale, y * heightScale, Settings.getBoolean(imageView.getContext(), Settings.PREFERENCE_LOCATION_ANIMATE));
        return true;
    }

    @SuppressWarnings("unused")
    @Subscribe
    void onGlobalAttrChanged(String key) {
        if (Settings.PREFERENCE_SUPPORT_ZOOM.equals(key)) {
            setupZoomAndLargeImage();
        } else if (Settings.PREFERENCE_READ_MODE.equals(key)) {
            if (imageView.isSupportZoom()) {
                imageView.getImageZoomer().setReadMode(Settings.getBoolean(getActivity(), Settings.PREFERENCE_READ_MODE));
            }
        } else if (Settings.PREFERENCE_SUPPORT_LARGE_IMAGE.equals(key)) {
            setupZoomAndLargeImage();
        }
    }

    private void setupZoomAndLargeImage(){
        imageView.setSupportZoom(Settings.getBoolean(imageView.getContext(), Settings.PREFERENCE_SUPPORT_ZOOM));
        imageView.setSupportLargeImage(Settings.getBoolean(imageView.getContext(), Settings.PREFERENCE_SUPPORT_LARGE_IMAGE));

        // 开启阅读模式
        if (imageView.isSupportZoom()) {
            imageView.getImageZoomer().setReadMode(Settings.getBoolean(imageView.getContext(), Settings.PREFERENCE_READ_MODE));
        }

        // 实时显示缩放比例
        if (imageView.isSupportZoom()) {
            imageView.getImageZoomer().addOnMatrixChangeListener(new ImageZoomer.OnMatrixChangeListener() {
                @Override
                public void onMatrixChanged(ImageZoomer imageZoomer) {
                    String scale = String.format(" %s ·", SketchUtils.formatFloat(imageZoomer.getZoomScale(), 2));
                    scaleTextView.setText(scale);
                    scaleTextView.requestLayout();
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

        // 初始化超大图查看器的暂停状态，这一步很重要
        if (Settings.getBoolean(getActivity(), Settings.PREFERENCE_PAGE_VISIBLE_TO_USER_DECODE_LARGE_IMAGE)
                && imageView.isSupportLargeImage()) {
            imageView.getLargeImageViewer().setPause(!isVisibleToUser());
        }

        // MappingView跟随碎片变化刷新碎片区域
        if (imageView.isSupportLargeImage()) {
            imageView.getLargeImageViewer().setOnTileChangedListener(new LargeImageViewer.OnTileChangedListener() {
                @Override
                public void onTileChanged(LargeImageViewer largeImageViewer) {
                    mappingView.tileChanged(largeImageViewer);
                }
            });
        }

        // MappingView跟随Matrix变化刷新显示区域
        if (imageView.isSupportZoom()) {
            imageView.getImageZoomer().addOnMatrixChangeListener(new ImageZoomer.OnMatrixChangeListener() {
                Rect visibleRect = new Rect();

                @Override
                public void onMatrixChanged(ImageZoomer imageZoomer) {
                    imageZoomer.getVisibleRect(visibleRect);
                    mappingView.update(imageZoomer.getDrawableSize(), visibleRect);
                }
            });
        }
    }
}