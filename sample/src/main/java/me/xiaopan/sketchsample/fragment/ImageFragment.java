package me.xiaopan.sketchsample.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.AssetFileDescriptor;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import me.xiaopan.androidinjector.InjectContentView;
import me.xiaopan.androidinjector.InjectExtra;
import me.xiaopan.androidinjector.InjectView;
import me.xiaopan.sketch.Sketch;
import me.xiaopan.sketch.cache.DiskCache;
import me.xiaopan.sketch.cache.MemoryCache;
import me.xiaopan.sketch.display.FadeInImageDisplayer;
import me.xiaopan.sketch.drawable.ImageAttrs;
import me.xiaopan.sketch.drawable.SketchDrawable;
import me.xiaopan.sketch.drawable.SketchGifDrawable;
import me.xiaopan.sketch.drawable.SketchLoadingDrawable;
import me.xiaopan.sketch.drawable.SketchRefBitmap;
import me.xiaopan.sketch.feature.ImageOrientationCorrector;
import me.xiaopan.sketch.feature.large.LargeImageViewer;
import me.xiaopan.sketch.feature.zoom.ImageZoomer;
import me.xiaopan.sketch.request.CancelCause;
import me.xiaopan.sketch.request.DisplayListener;
import me.xiaopan.sketch.request.DisplayOptions;
import me.xiaopan.sketch.request.ErrorCause;
import me.xiaopan.sketch.request.ImageFrom;
import me.xiaopan.sketch.request.RequestLevel;
import me.xiaopan.sketch.request.UriScheme;
import me.xiaopan.sketch.state.MemoryCacheStateImage;
import me.xiaopan.sketch.util.SketchUtils;
import me.xiaopan.sketchsample.MyFragment;
import me.xiaopan.sketchsample.R;
import me.xiaopan.sketchsample.activity.ApplyBackgroundCallback;
import me.xiaopan.sketchsample.event.AppConfigChangedEvent;
import me.xiaopan.sketchsample.util.ApplyWallpaperAsyncTask;
import me.xiaopan.sketchsample.util.SaveAssetImageAsyncTask;
import me.xiaopan.sketchsample.util.SaveContentImageAsyncTask;
import me.xiaopan.sketchsample.util.SaveImageAsyncTask;
import me.xiaopan.sketchsample.util.SaveResImageAsyncTask;
import me.xiaopan.sketchsample.util.AppConfig;
import me.xiaopan.sketchsample.widget.HintView;
import me.xiaopan.sketchsample.widget.MappingView;
import me.xiaopan.sketchsample.widget.MyImageView;

@InjectContentView(R.layout.fragment_image)
public class ImageFragment extends MyFragment {
    public static final String PARAM_REQUIRED_STRING_IMAGE_URI = "PARAM_REQUIRED_STRING_IMAGE_URI";
    public static final String PARAM_REQUIRED_STRING_LOADING_IMAGE_OPTIONS_KEY = "PARAM_REQUIRED_STRING_LOADING_IMAGE_OPTIONS_KEY";
    public static final String PARAM_REQUIRED_BOOLEAN_SHOW_TOOLS = "PARAM_REQUIRED_BOOLEAN_SHOW_TOOLS";

    @InjectView(R.id.image_imageFragment_image)
    private MyImageView imageView;

    @InjectView(R.id.mapping_imageFragment)
    private MappingView mappingView;

    @InjectView(R.id.hint_imageFragment_hint)
    private HintView hintView;

    @InjectExtra(PARAM_REQUIRED_STRING_IMAGE_URI)
    private String imageUri;

    @InjectExtra(PARAM_REQUIRED_STRING_LOADING_IMAGE_OPTIONS_KEY)
    private String loadingImageOptionsId;

    @InjectExtra(PARAM_REQUIRED_BOOLEAN_SHOW_TOOLS)
    private boolean showTools;

    private SetWindowBackground setWindowBackground = new SetWindowBackground();
    private GifPlayFollowPageVisible gifPlayFollowPageVisible = new GifPlayFollowPageVisible();
    private ShowImageHelper showImageHelper = new ShowImageHelper();
    private ImageZoomHelper imageZoomHelper = new ImageZoomHelper();
    private MappingHelper mappingHelper = new MappingHelper();
    private LargeImageHelper largeImageHelper = new LargeImageHelper();
    private SingleClickHelper singleClickHelper = new SingleClickHelper();
    private LongClickHelper longClickHelper = new LongClickHelper();

    public static ImageFragment build(String imageUri, String loadingImageOptionsId, boolean showTools) {
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_REQUIRED_STRING_IMAGE_URI, imageUri);
        bundle.putString(PARAM_REQUIRED_STRING_LOADING_IMAGE_OPTIONS_KEY, loadingImageOptionsId);
        bundle.putBoolean(PARAM_REQUIRED_BOOLEAN_SHOW_TOOLS, showTools);
        ImageFragment fragment = new ImageFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setWindowBackground.onCreate(getActivity());
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imageZoomHelper.onViewCreated();
        largeImageHelper.onViewCreated();
        mappingHelper.onViewCreated();
        longClickHelper.onViewCreated();
        singleClickHelper.onViewCreated();
        showImageHelper.onViewCreated();

        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    @Override
    public void onUserVisibleChanged(boolean isVisibleToUser) {
        largeImageHelper.onUserVisibleChanged();
        setWindowBackground.onUserVisibleChanged();
        gifPlayFollowPageVisible.onUserVisibleChanged();
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onEvent(AppConfigChangedEvent event) {
        if (AppConfig.Key.SUPPORT_ZOOM.equals(event.key)) {
            imageZoomHelper.onConfigChanged();
            mappingHelper.onViewCreated();
            longClickHelper.onViewCreated();
            singleClickHelper.onViewCreated();
        } else if (AppConfig.Key.READ_MODE.equals(event.key)) {
            imageZoomHelper.onReadModeConfigChanged();
        } else if (AppConfig.Key.SUPPORT_LARGE_IMAGE.equals(event.key)) {
            largeImageHelper.onConfigChanged();
            mappingHelper.onViewCreated();
        }
    }

    public static class PlayImageEvent {

    }

    private class ShowImageHelper implements DisplayListener {
        private void onViewCreated() {
            imageView.setDisabledLongClickShowImageInfo(true);

            imageView.setDisplayListener(this);

            initOptions();
            imageView.displayImage(imageUri);
        }

        private void initOptions() {
            DisplayOptions options = imageView.getOptions();

            // 允许播放GIF
            options.setDecodeGifImage(true);

            // 有占位图选项信息的话就使用内存缓存占位图但不使用任何显示器，否则就是用渐入显示器
            if (!TextUtils.isEmpty(loadingImageOptionsId)) {
                String loadingImageMemoryCacheKey = SketchUtils.makeRequestKey(imageUri, loadingImageOptionsId);
                MemoryCache memoryCache = Sketch.with(getActivity()).getConfiguration().getMemoryCache();
                SketchRefBitmap cachedRefBitmap = memoryCache.get(loadingImageMemoryCacheKey);
                if (cachedRefBitmap != null) {
                    options.setLoadingImage(new MemoryCacheStateImage(loadingImageMemoryCacheKey, null));
                } else {
                    options.setImageDisplayer(new FadeInImageDisplayer());
                }
            } else {
                options.setImageDisplayer(new FadeInImageDisplayer());
            }
        }

        @Override
        public void onStarted() {
            hintView.loading("正在加载图片，请稍后...");
        }

        @Override
        public void onCompleted(Drawable drawable, ImageFrom imageFrom, ImageAttrs imageAttrs) {
            hintView.hidden();

            setWindowBackground.onDisplayCompleted();
            gifPlayFollowPageVisible.onDisplayCompleted();
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
    }

    private class SetWindowBackground {
        private ApplyBackgroundCallback applyBackgroundCallback;

        private void onCreate(Activity activity) {
            if (activity instanceof ApplyBackgroundCallback) {
                setWindowBackground.applyBackgroundCallback = (ApplyBackgroundCallback) activity;
            }
        }

        private void onUserVisibleChanged() {
            if (applyBackgroundCallback != null && isVisibleToUser()) {
                applyBackgroundCallback.onApplyBackground(imageUri);
            }
        }

        private void onDisplayCompleted() {
            onUserVisibleChanged();
        }
    }

    private class GifPlayFollowPageVisible {
        private void onUserVisibleChanged() {
            Drawable drawable = imageView.getDrawable();
            Drawable lastDrawable = SketchUtils.getLastDrawable(drawable);
            if (lastDrawable != null && (lastDrawable instanceof SketchGifDrawable)) {
                ((SketchGifDrawable) lastDrawable).followPageVisible(isVisibleToUser(), false);
            }
        }

        private void onDisplayCompleted() {
            Drawable drawable = imageView.getDrawable();
            Drawable lastDrawable = SketchUtils.getLastDrawable(drawable);
            if (lastDrawable != null && (lastDrawable instanceof SketchGifDrawable)) {
                ((SketchGifDrawable) lastDrawable).followPageVisible(isVisibleToUser(), true);
            }
        }
    }

    private class ImageZoomHelper {
        private void onViewCreated() {
            imageView.setSupportZoom(AppConfig.getBoolean(imageView.getContext(), AppConfig.Key.SUPPORT_ZOOM));
            onReadModeConfigChanged();
        }

        private void onConfigChanged() {
            onViewCreated();
        }

        private void onReadModeConfigChanged() {
            if (imageView.isSupportZoom()) {
                boolean readMode = AppConfig.getBoolean(getActivity(), AppConfig.Key.READ_MODE);
                imageView.getImageZoomer().setReadMode(readMode);
            }
        }
    }

    private class LargeImageHelper {
        private void onViewCreated() {
            imageView.setSupportLargeImage(AppConfig.getBoolean(imageView.getContext(), AppConfig.Key.SUPPORT_LARGE_IMAGE));

            // 初始化超大图查看器的暂停状态，这一步很重要
            if (AppConfig.getBoolean(getActivity(), AppConfig.Key.PAGE_VISIBLE_TO_USER_DECODE_LARGE_IMAGE)
                    && imageView.isSupportLargeImage()) {
                imageView.getLargeImageViewer().setPause(!isVisibleToUser());
            }
        }

        private void onConfigChanged() {
            onViewCreated();
        }

        private void onUserVisibleChanged() {
            // 不可见的时候暂停超大图查看器，节省内存
            if (AppConfig.getBoolean(getActivity(), AppConfig.Key.PAGE_VISIBLE_TO_USER_DECODE_LARGE_IMAGE)) {
                if (imageView.isSupportLargeImage()) {
                    imageView.getLargeImageViewer().setPause(!isVisibleToUser());
                }
            } else {
                if (imageView.isSupportLargeImage()
                        && isVisibleToUser() && imageView.getLargeImageViewer().isPaused()) {
                    imageView.getLargeImageViewer().setPause(false);
                }
            }
        }
    }

    private class MappingHelper {
        private void onViewCreated() {
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

            // 点击MappingView定位到指定位置
            mappingView.setOnSingleClickListener(new MappingView.OnSingleClickListener() {
                @Override
                public boolean onSingleClick(float x, float y) {
                    Drawable drawable = imageView.getDrawable();
                    if (drawable == null) {
                        return false;
                    }

                    if (drawable.getIntrinsicWidth() == 0 || drawable.getIntrinsicHeight() == 0) {
                        return false;
                    }

                    if (mappingView.getWidth() == 0 || mappingView.getHeight() == 0) {
                        return false;
                    }

                    final float widthScale = (float) drawable.getIntrinsicWidth() / mappingView.getWidth();
                    final float heightScale = (float) drawable.getIntrinsicHeight() / mappingView.getHeight();
                    final float realX = x * widthScale;
                    final float realY = y * heightScale;

                    boolean showLocationAnimation = AppConfig.getBoolean(imageView.getContext(), AppConfig.Key.LOCATION_ANIMATE);
                    return location(realX, realY, showLocationAnimation);
                }
            });

            mappingView.getOptions().setImageDisplayer(new FadeInImageDisplayer());
            mappingView.getOptions().setMaxSize(600, 600);
            mappingView.displayImage(imageUri);

            mappingView.setVisibility(showTools ? View.VISIBLE : View.GONE);
        }

        private boolean location(float x, float y, boolean animate) {
            if (!imageView.isSupportZoom()) {
                return false;
            }

            imageView.getImageZoomer().location(x, y, animate);
            return true;
        }
    }

    private class SingleClickHelper {
        private void onViewCreated() {
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
        }
    }

    private class LongClickHelper {

        private void onViewCreated() {
            if (imageView.isSupportZoom()) {
                imageView.getImageZoomer().setOnViewLongPressListener(new ImageZoomer.OnViewLongPressListener() {
                    @Override
                    public void onViewLongPress(View view, float x, float y) {
                        show();
                    }
                });
            } else {
                imageView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        show();
                        return true;
                    }
                });
            }
        }

        private void show() {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            final boolean supportZoom = imageView.isSupportZoom();
            final ImageZoomer imageZoomer = supportZoom ? imageView.getImageZoomer() : null;
            final boolean supportLargeImage = imageView.isSupportLargeImage();
            final LargeImageViewer largeImageViewer = imageView.getLargeImageViewer();
            Drawable drawable = SketchUtils.getLastDrawable(imageView.getDrawable());

            final List<MenuItem> menuItemList = new LinkedList<MenuItem>();

            String imageInfo;
            if (drawable instanceof SketchLoadingDrawable) {
                imageInfo = "图片正在加载，请稍后";
            } else if (drawable instanceof SketchDrawable) {
                imageInfo = makeImageInfo(drawable, (SketchDrawable) drawable);
            } else {
                imageInfo = "未知来源图片";
            }
            menuItemList.add(new MenuItem(imageInfo, null));

            String scaleTypeTitle = "切换ScaleType（" + (supportZoom ? imageZoomer.getScaleType() : imageView.getScaleType()) + "）";
            menuItemList.add(new MenuItem(scaleTypeTitle, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    showScaleTypeMenu();
                }
            }));

            String largeImageTileTitle = supportLargeImage ? (largeImageViewer.isShowTileRect() ? "不显示分块区域" : "显示分块区域") : "分块区域（未开启大图功能）";
            menuItemList.add(new MenuItem(largeImageTileTitle, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    setShowTile();
                }
            }));

            String readModeTitle = supportZoom ? (imageZoomer.isReadMode() ? "关闭阅读模式" : "开启阅读模式") : ("阅读模式（未开启缩放功能）");
            menuItemList.add(new MenuItem(readModeTitle, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    setReadMode();
                }
            }));

            String moreTitle = "更多功能";
            menuItemList.add(new MenuItem(moreTitle, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    showMoreMenu();
                }
            }));

            String[] items = new String[menuItemList.size()];
            for (int w = 0, size = menuItemList.size(); w < size; w++) {
                items[w] = menuItemList.get(w).title;
            }

            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

                    DialogInterface.OnClickListener clickListener = menuItemList.get(which).clickListener;
                    if (clickListener != null) {
                        clickListener.onClick(dialog, which);
                    }
                }
            });

            builder.setNegativeButton("取消", null);
            builder.show();
        }

        private String makeImageInfo(Drawable drawable, SketchDrawable sketchDrawable) {
            StringBuilder messageBuilder = new StringBuilder();

            messageBuilder.append("\n");
            messageBuilder.append(sketchDrawable.getUri());

            long imageLength = 0;
            UriScheme uriScheme = UriScheme.valueOfUri(sketchDrawable.getUri());
            if (uriScheme == UriScheme.FILE) {
                imageLength = new File(UriScheme.FILE.crop(sketchDrawable.getUri())).length();
            } else if (uriScheme == UriScheme.NET) {
                DiskCache.Entry diskCacheEntry = Sketch.with(getContext()).getConfiguration().getDiskCache().get(sketchDrawable.getUri());
                if (diskCacheEntry != null) {
                    imageLength = diskCacheEntry.getFile().length();
                }
            } else if (uriScheme == UriScheme.ASSET) {
                AssetFileDescriptor assetFileDescriptor = null;
                try {
                    assetFileDescriptor = getContext().getAssets().openFd(UriScheme.ASSET.crop(sketchDrawable.getUri()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageLength = assetFileDescriptor != null ? assetFileDescriptor.getLength() : 0;
            } else if (uriScheme == UriScheme.DRAWABLE) {
                AssetFileDescriptor assetFileDescriptor = getContext().getResources().openRawResourceFd(Integer.valueOf(UriScheme.DRAWABLE.crop(sketchDrawable.getUri())));
                imageLength = assetFileDescriptor != null ? assetFileDescriptor.getLength() : 0;
            } else if (uriScheme == UriScheme.CONTENT) {
                AssetFileDescriptor assetFileDescriptor = null;
                try {
                    assetFileDescriptor = getContext().getContentResolver().openAssetFileDescriptor(Uri.parse(sketchDrawable.getUri()), "r");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                imageLength = assetFileDescriptor != null ? assetFileDescriptor.getLength() : 0;
            }

            String needDiskSpace = imageLength > 0 ? Formatter.formatFileSize(getContext(), imageLength) : "未知";

            int previewDrawableByteCount = sketchDrawable.getByteCount();
            int pixelByteCount = previewDrawableByteCount / drawable.getIntrinsicWidth() / drawable.getIntrinsicHeight();
            int originImageByteCount = sketchDrawable.getOriginWidth() * sketchDrawable.getOriginHeight() * pixelByteCount;
            String needMemory = Formatter.formatFileSize(getContext(), originImageByteCount);

            messageBuilder.append("\n");
            messageBuilder.append("\n");
            messageBuilder.append("原始图：")
                    .append(sketchDrawable.getOriginWidth()).append("x").append(sketchDrawable.getOriginHeight())
                    .append("/").append(sketchDrawable.getMimeType().substring(6))
                    .append("/").append(needDiskSpace);

            messageBuilder.append("\n");
            messageBuilder.append("方向/内存：").append(ImageOrientationCorrector.toName(sketchDrawable.getExifOrientation()))
                    .append("/").append(needMemory);

            messageBuilder.append("\n");
            messageBuilder.append("预览图：")
                    .append(drawable.getIntrinsicWidth()).append("x").append(drawable.getIntrinsicHeight())
                    .append("/").append(sketchDrawable.getBitmapConfig())
                    .append("/").append(Formatter.formatFileSize(getContext(), previewDrawableByteCount));

            if (imageView.isSupportZoom()) {
                ImageZoomer imageZoomer = imageView.getImageZoomer();

                messageBuilder.append("\n");
                messageBuilder.append("\n");
                messageBuilder.append("缩放：").append(SketchUtils.formatFloat(imageZoomer.getZoomScale(), 2));

                Rect visibleRect = new Rect();
                imageZoomer.getVisibleRect(visibleRect);
                messageBuilder.append("/").append(visibleRect.toShortString());
            }

            if (imageView.isSupportLargeImage()) {
                LargeImageViewer largeImageViewer = imageView.getLargeImageViewer();
                if (largeImageViewer.isReady()) {
                    String tilesNeedMemory = Formatter.formatFileSize(getContext(), largeImageViewer.getTilesAllocationByteCount());
                    messageBuilder.append("\n");
                    messageBuilder.append("碎片：")
                            .append(largeImageViewer.getTiles())
                            .append("/")
                            .append(largeImageViewer.getTileList().size())
                            .append("/")
                            .append(tilesNeedMemory);
                    messageBuilder.append("\n");
                    messageBuilder.append("碎片解码区域：").append(largeImageViewer.getDecodeRect().toShortString());
                    messageBuilder.append("\n");
                    messageBuilder.append("碎片SRC区域：").append(largeImageViewer.getDecodeSrcRect().toShortString());
                } else if (largeImageViewer.isInitializing()) {
                    messageBuilder.append("\n");
                    messageBuilder.append("大图功能正在初始化...");
                }
            }

            messageBuilder.append("\n");

            return messageBuilder.toString();
        }

        private void showScaleTypeMenu() {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setTitle("修改ScaleType");

            String[] items = new String[7];
            items[0] = "CENTER";
            items[1] = "CENTER_CROP";
            items[2] = "CENTER_INSIDE";
            items[3] = "FIT_START";
            items[4] = "FIT_CENTER";
            items[5] = "FIT_END";
            items[6] = "FIT_XY";

            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

                    switch (which) {
                        case 0:
                            imageView.setScaleType(ImageView.ScaleType.CENTER);
                            break;
                        case 1:
                            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            break;
                        case 2:
                            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                            break;
                        case 3:
                            imageView.setScaleType(ImageView.ScaleType.FIT_START);
                            break;
                        case 4:
                            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                            break;
                        case 5:
                            imageView.setScaleType(ImageView.ScaleType.FIT_END);
                            break;
                        case 6:
                            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                            break;
                    }
                }
            });

            builder.setNegativeButton("取消", null);
            builder.show();
        }

        private void showMoreMenu() {
            final List<MenuItem> menuItemList = new LinkedList<MenuItem>();

            String rotateTitle = imageView.isSupportZoom() ? ("顺时针旋转90度（" + imageView.getImageZoomer().getRotateDegrees() + "）") : "旋转图片（未开启缩放功能）";
            menuItemList.add(new MenuItem(rotateTitle, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    rotate();
                }
            }));

            menuItemList.add(new MenuItem("幻灯片播放", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    play();
                }
            }));

            menuItemList.add(new MenuItem("分享图片", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    share();
                }
            }));

            menuItemList.add(new MenuItem("保存图片", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    save();
                }
            }));

            menuItemList.add(new MenuItem("设为壁纸", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    setWallpaper();
                }
            }));

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            String[] items = new String[menuItemList.size()];
            for (int w = 0, size = menuItemList.size(); w < size; w++) {
                items[w] = menuItemList.get(w).title;
            }

            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

                    DialogInterface.OnClickListener clickListener = menuItemList.get(which).clickListener;
                    if (clickListener != null) {
                        clickListener.onClick(dialog, which);
                    }
                }
            });

            builder.setNegativeButton("取消", null);
            builder.show();
        }

        private void setShowTile() {
            if (imageView.isSupportLargeImage()) {
                LargeImageViewer largeImageViewer = imageView.getLargeImageViewer();
                boolean newShowTileRect = !largeImageViewer.isShowTileRect();
                largeImageViewer.setShowTileRect(newShowTileRect);
            } else {
                Toast.makeText(getContext(), "请先到首页左侧菜单开启大图功能", Toast.LENGTH_SHORT).show();
            }
        }

        private void setReadMode() {
            if (imageView.isSupportZoom()) {
                ImageZoomer imageZoomer = imageView.getImageZoomer();
                boolean newReadMode = !imageZoomer.isReadMode();
                imageZoomer.setReadMode(newReadMode);
            } else {
                Toast.makeText(getContext(), "请先到首页左侧菜单开启缩放功能", Toast.LENGTH_SHORT).show();
            }
        }

        private void rotate() {
            if (imageView.isSupportZoom()) {
                if (!imageView.getImageZoomer().rotateBy(90)) {
                    Toast.makeText(getContext(), "旋转角度必须是90的倍数或开启大图功能后无法使用旋转功能", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getContext(), "请先到首页左侧菜单开启缩放功能", Toast.LENGTH_SHORT).show();
            }
        }

        private File getImageFile(String imageUri) {
            if (TextUtils.isEmpty(imageUri)) {
                return null;
            }

            UriScheme uriScheme = !TextUtils.isEmpty(imageUri) ? UriScheme.valueOfUri(imageUri) : null;
            if (uriScheme == null) {
                return null;
            }

            if (uriScheme == UriScheme.NET) {
                DiskCache.Entry diskCacheEntry = Sketch.with(getActivity()).getConfiguration().getDiskCache().get(imageUri);
                if (diskCacheEntry != null) {
                    return diskCacheEntry.getFile();
                } else {
                    Toast.makeText(getActivity(), "图片还没有下载好哦，再等一会儿吧！", Toast.LENGTH_LONG).show();
                    return null;
                }
            } else if (uriScheme == UriScheme.FILE) {
                return new File(UriScheme.FILE.crop(imageUri));
            } else {
                Toast.makeText(getActivity(), "我去，怎么会有这样的URL " + imageUri, Toast.LENGTH_LONG).show();
                return null;
            }
        }

        private void share() {
            Drawable drawable = imageView.getDrawable();
            String imageUri = drawable != null && drawable instanceof SketchDrawable ? ((SketchDrawable) drawable).getUri() : null;
            if (TextUtils.isEmpty(imageUri)) {
                Toast.makeText(getActivity(), "稍等一会儿", Toast.LENGTH_LONG).show();
                return;
            }

            File imageFile = getImageFile(imageUri);
            if (imageFile == null) {
                return;
            }

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(imageFile));
            intent.setType("image/" + parseFileType(imageFile.getName()));

            List<ResolveInfo> infoList = getActivity().getPackageManager().queryIntentActivities(intent, 0);
            if (infoList == null || infoList.isEmpty()) {
                Toast.makeText(getActivity(), "您的设备上没有能够分享的APP", Toast.LENGTH_LONG).show();
                return;
            }

            startActivity(intent);
        }

        private void setWallpaper() {
            Drawable drawable = imageView.getDrawable();
            String imageUri = drawable != null && drawable instanceof SketchDrawable ? ((SketchDrawable) drawable).getUri() : null;
            if (TextUtils.isEmpty(imageUri)) {
                Toast.makeText(getActivity(), "稍等一会儿", Toast.LENGTH_LONG).show();
                return;
            }

            File imageFile = getImageFile(imageUri);
            if (imageFile == null) {
                return;
            }

            new ApplyWallpaperAsyncTask(getActivity(), imageFile) {
                @Override
                protected void onPostExecute(Boolean aBoolean) {
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), aBoolean ? "设置壁纸成功" : "设置壁纸失败", Toast.LENGTH_LONG).show();
                    }
                }
            }.execute(0);
        }

        private void play() {
            EventBus.getDefault().post(new PlayImageEvent());
        }

        private void save() {
            Drawable drawable = imageView.getDrawable();
            String imageUri = drawable != null && drawable instanceof SketchDrawable ? ((SketchDrawable) drawable).getUri() : null;
            if (TextUtils.isEmpty(imageUri)) {
                Toast.makeText(getActivity(), "稍等一会儿", Toast.LENGTH_LONG).show();
                return;
            }

            UriScheme uriScheme = UriScheme.valueOfUri(imageUri);
            if (uriScheme == UriScheme.NET) {
                DiskCache.Entry imageFile3DiskCacheEntry = Sketch.with(getActivity()).getConfiguration().getDiskCache().get(imageUri);
                if (imageFile3DiskCacheEntry != null) {
                    new SaveImageAsyncTask(getActivity(), imageFile3DiskCacheEntry.getFile(), imageUri).execute("");
                } else {
                    Toast.makeText(getActivity(), "图片还没有下载好哦，再等一会儿吧！", Toast.LENGTH_LONG).show();
                }
            } else if (uriScheme == UriScheme.ASSET) {
                new SaveAssetImageAsyncTask(getActivity(), UriScheme.ASSET.crop(imageUri)).execute("");
            } else if (uriScheme == UriScheme.CONTENT) {
                new SaveContentImageAsyncTask(getActivity(), Uri.parse(imageUri)).execute("");
            } else if (uriScheme == UriScheme.DRAWABLE) {
                new SaveResImageAsyncTask(getActivity(), Integer.valueOf(UriScheme.DRAWABLE.crop(imageUri))).execute("");
            } else if (uriScheme == UriScheme.FILE) {
                Toast.makeText(getActivity(), "当前图片本就是本地的无需保存", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getActivity(), "我去，怎么会有这样的URL " + imageUri, Toast.LENGTH_LONG).show();
            }
        }

        private String parseFileType(String fileName) {
            int lastIndexOf = fileName.lastIndexOf(".");
            if (lastIndexOf < 0) {
                return null;
            }
            String fileType = fileName.substring(lastIndexOf + 1);
            if ("".equals(fileType.trim())) {
                return null;
            }
            return fileType;
        }

        private class MenuItem {
            private String title;
            private AlertDialog.OnClickListener clickListener;

            public MenuItem(String title, AlertDialog.OnClickListener clickListener) {
                this.title = title;
                this.clickListener = clickListener;
            }
        }
    }
}