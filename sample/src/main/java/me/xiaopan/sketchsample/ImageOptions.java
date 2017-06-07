/*
 * Copyright (C) 2017 Peng fei Pan <sky@xiaopan.me>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.xiaopan.sketchsample;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.IntDef;
import android.util.DisplayMetrics;
import android.util.SparseArray;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import me.xiaopan.sketch.display.TransitionImageDisplayer;
import me.xiaopan.sketch.process.GaussianBlurImageProcessor;
import me.xiaopan.sketch.request.DisplayOptions;
import me.xiaopan.sketch.request.DownloadOptions;
import me.xiaopan.sketch.request.LoadOptions;
import me.xiaopan.sketch.shaper.CircleImageShaper;
import me.xiaopan.sketch.shaper.RoundRectImageShaper;
import me.xiaopan.sketch.state.DrawableStateImage;
import me.xiaopan.sketch.state.OldStateImage;
import me.xiaopan.sketch.util.SketchUtils;

public class ImageOptions {
    /**
     * 通用矩形
     */
    public static final int RECT = 101;

    /**
     * 带描边的圆形
     */
    public static final int CIRCULAR_STROKE = 102;

    /**
     * 窗口背景
     */
    public static final int WINDOW_BACKGROUND = 103;

    /**
     * 圆角矩形
     */
    public static final int ROUND_RECT = 104;

    /**
     * 充满列表
     */
    public static final int LIST_FULL = 105;

    private static final SparseArray<OptionsHolder> OPTIONS_HOLDER_SPARSE_ARRAY = new SparseArray<OptionsHolder>();

    static {
        final TransitionImageDisplayer transitionImageDisplayer = new TransitionImageDisplayer();

        OPTIONS_HOLDER_SPARSE_ARRAY.append(ImageOptions.RECT, new OptionsHolder() {
            @Override
            protected DownloadOptions onCreateOptions(Context context) {
                return new DisplayOptions()
                        .setLoadingImage(R.drawable.image_loading)
                        .setErrorImage(R.drawable.image_error)
                        .setPauseDownloadImage(R.drawable.image_pause_download)
                        .setImageDisplayer(transitionImageDisplayer)
                        .setShapeSizeByFixedSize(true);
            }
        });

        OPTIONS_HOLDER_SPARSE_ARRAY.append(ImageOptions.CIRCULAR_STROKE, new OptionsHolder() {
            @Override
            protected DownloadOptions onCreateOptions(Context context) {
                return new DisplayOptions()
                        .setLoadingImage(R.drawable.image_loading)
                        .setErrorImage(R.drawable.image_error)
                        .setPauseDownloadImage(R.drawable.image_pause_download)
                        .setImageDisplayer(transitionImageDisplayer)
                        .setImageShaper(new CircleImageShaper().setStroke(Color.WHITE, SketchUtils.dp2px(context, 1)))
                        .setShapeSizeByFixedSize(true);
            }
        });

        OPTIONS_HOLDER_SPARSE_ARRAY.append(ImageOptions.WINDOW_BACKGROUND, new OptionsHolder() {
            @Override
            protected DownloadOptions onCreateOptions(Context context) {
                return new DisplayOptions()
                        .setLoadingImage(new OldStateImage(new DrawableStateImage(R.drawable.shape_window_background)))
                        .setImageProcessor(GaussianBlurImageProcessor.makeLayerColor(Color.parseColor("#66000000")))
                        .setCacheProcessedImageInDisk(true)
                        .setBitmapConfig(Bitmap.Config.ARGB_8888)   // 效果比较重要
                        .setShapeSizeByFixedSize(true)
                        .setMaxSize(context.getResources().getDisplayMetrics().widthPixels / 4,
                                context.getResources().getDisplayMetrics().heightPixels / 4)
                        .setImageDisplayer(new TransitionImageDisplayer(true));
            }
        });

        OPTIONS_HOLDER_SPARSE_ARRAY.append(ImageOptions.ROUND_RECT, new OptionsHolder() {
            @Override
            protected DownloadOptions onCreateOptions(Context context) {
                return new DisplayOptions()
                        .setLoadingImage(R.drawable.image_loading)
                        .setErrorImage(R.drawable.image_error)
                        .setPauseDownloadImage(R.drawable.image_pause_download)
                        .setImageShaper(new RoundRectImageShaper(SketchUtils.dp2px(context, 6)))
                        .setImageDisplayer(transitionImageDisplayer)
                        .setShapeSizeByFixedSize(true);
            }
        });

        OPTIONS_HOLDER_SPARSE_ARRAY.append(ImageOptions.LIST_FULL, new OptionsHolder() {
            @Override
            protected DownloadOptions onCreateOptions(Context context) {
                DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
                return new DisplayOptions()
                        .setLoadingImage(R.drawable.image_loading)
                        .setErrorImage(R.drawable.image_error)
                        .setPauseDownloadImage(R.drawable.image_pause_download)
                        .setMaxSize(displayMetrics.widthPixels, displayMetrics.heightPixels)
                        .setImageDisplayer(transitionImageDisplayer);
            }
        });
    }

    private ImageOptions() {
    }

    @SuppressWarnings("unused")
    public static DisplayOptions getDisplayOptions(Context context, @Type int optionsId) {
        return (DisplayOptions) OPTIONS_HOLDER_SPARSE_ARRAY.get(optionsId).getOptions(context);
    }

    @SuppressWarnings("unused")
    public static LoadOptions getLoadOptions(Context context, @Type int optionsId) {
        return (LoadOptions) OPTIONS_HOLDER_SPARSE_ARRAY.get(optionsId).getOptions(context);
    }

    @SuppressWarnings("unused")
    public static DownloadOptions getDownloadOptions(Context context, @Type int optionsId) {
        return OPTIONS_HOLDER_SPARSE_ARRAY.get(optionsId).getOptions(context);
    }

    private static abstract class OptionsHolder {
        private DownloadOptions options;

        public DownloadOptions getOptions(Context context) {
            if (options == null) {
                synchronized (this) {
                    if (options == null) {
                        options = onCreateOptions(context);
                    }
                }
            }
            return options;
        }

        protected abstract DownloadOptions onCreateOptions(Context context);
    }

    @IntDef({
            RECT,
            CIRCULAR_STROKE,
            WINDOW_BACKGROUND,
            ROUND_RECT,
            LIST_FULL,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type{

    }
}
