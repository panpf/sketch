/*
 * Copyright (C) 2013 Peng fei Pan <sky@xiaopan.me>
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

package me.xiaopan.sketch;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;

import me.xiaopan.sketch.request.DisplayCache;
import me.xiaopan.sketch.request.DisplayListener;
import me.xiaopan.sketch.request.DisplayOptions;
import me.xiaopan.sketch.request.DisplayRequest;
import me.xiaopan.sketch.request.DownloadProgressListener;
import me.xiaopan.sketch.request.RedisplayListener;
import me.xiaopan.sketch.uri.UriModel;

public interface SketchView {

    @Nullable
    Drawable getDrawable();

    void setImageDrawable(@Nullable Drawable drawable);

    @Nullable
    ImageView.ScaleType getScaleType();

    void clearAnimation();

    void startAnimation(@Nullable Animation animation);

    @Nullable
    ViewGroup.LayoutParams getLayoutParams();

    @NonNull
    Resources getResources();

    int getPaddingLeft();

    int getPaddingTop();

    int getPaddingRight();

    int getPaddingBottom();


    /**
     * 显示图片
     *
     * @param uri 图片Uri，支持全部的 uri 类型，请参考 <a href="https://github.com/panpf/sketch/blob/master/docs/wiki/uri.md">https://github.com/panpf/sketch/blob/master/docs/wiki/uri.md</a>
     */
    @Nullable
    DisplayRequest displayImage(@NonNull String uri);

    /**
     * 显示Drawable资源里的图片
     *
     * @param drawableResId Drawable ID
     */
    @Nullable
    @SuppressWarnings("unused")
    DisplayRequest displayResourceImage(@DrawableRes int drawableResId);

    /**
     * 显示asset里的图片
     *
     * @param imageFileName ASSETS文件加下的图片文件的名称
     */
    @Nullable
    @SuppressWarnings("unused")
    DisplayRequest displayAssetImage(@NonNull String imageFileName);

    /**
     * 显示来自 ContentProvider 的图片
     *
     * @param uri 图片URI
     */
    @Nullable
    @SuppressWarnings("unused")
    DisplayRequest displayContentImage(@NonNull String uri);

    /**
     * 准备显示图片
     */
    void onReadyDisplay(@Nullable UriModel uriModel);

    /**
     * 获取显示参数
     */
    @NonNull
    DisplayOptions getOptions();

    /**
     * 批量设置显示参数
     */
    void setOptions(@Nullable DisplayOptions newDisplayOptions);

    /**
     * 获取显示监听器
     */
    @Nullable
    DisplayListener getDisplayListener();

    /**
     * 设置显示监听器
     */
    void setDisplayListener(@Nullable DisplayListener displayListener);

    /**
     * 获取下载进度监听器
     */
    @Nullable
    DownloadProgressListener getDownloadProgressListener();

    /**
     * 设置下载进度监听器
     */
    @SuppressWarnings("unused")
    void setDownloadProgressListener(@Nullable DownloadProgressListener downloadProgressListener);

    /**
     * 获取显示缓存，此方法由Sketch调用，无需理会
     */
    @Nullable
    DisplayCache getDisplayCache();

    /**
     * 设置显示缓存，此方法由Sketch调用，无需理会
     */
    void setDisplayCache(@NonNull DisplayCache displayCache);

    /**
     * 是否开启了手势缩放功能
     */
    boolean isZoomEnabled();

    /**
     * 重新显示
     *
     * @param listener 在重新显示之前你可以通过这个 listener，修改缓存的options
     * @return false：重新显示失败，之前没有显示过
     */
    boolean redisplay(@Nullable RedisplayListener listener);
}
