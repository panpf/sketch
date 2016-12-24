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

package me.xiaopan.sketch.request;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;

public interface ImageViewInterface {
    /**
     * 准备显示图片
     */
    void onReadyDisplay(UriScheme uriScheme);

    /**
     * 获取Drawable
     */
    Drawable getDrawable();

    /**
     * 设置Drawable
     */
    void setImageDrawable(Drawable drawable);

    /**
     * 获取自己
     */
    View getSelf();

    /**
     * 获取缩放方式
     */
    ImageView.ScaleType getScaleType();

    /**
     * 清除动画
     */
    void clearAnimation();

    /**
     * 执行动画
     */
    void startAnimation(Animation animation);

    /**
     * 显示图片
     *
     * @param uri 图片Uri
     */
    DisplayRequest displayImage(String uri);

    /**
     * 显示Drawable资源里的图片
     *
     * @param drawableResId Drawable ID
     */
    @SuppressWarnings("unused")
    DisplayRequest displayResourceImage(int drawableResId);

    /**
     * 显示asset里的图片
     *
     * @param imageFileName ASSETS文件加下的图片文件的名称
     */
    @SuppressWarnings("unused")
    DisplayRequest displayAssetImage(String imageFileName);

    /**
     * 显示URI指向的图片
     *
     * @param uri 图片URI
     */
    @SuppressWarnings("unused")
    DisplayRequest displayURIImage(Uri uri);

    /**
     * 显示已安装APP的图标
     *
     * @param packageName APP包名
     * @param versionCode APP版本号
     */
    DisplayRequest displayInstalledAppIcon(String packageName, int versionCode);

    /**
     * 获取显示参数
     */
    DisplayOptions getOptions();

    /**
     * 批量设置显示参数
     */
    void setOptions(DisplayOptions newDisplayOptions);

    /**
     * 批量设置显示参数，你只需要提前将DisplayOptions通过Sketch.putOptions()方法存起来，然后在这里指定其名称即可
     */
    void setOptionsByName(Enum<?> optionsName);

    /**
     * 获取显示监听器
     */
    DisplayListener getDisplayListener();

    /**
     * 设置显示监听器
     */
    void setDisplayListener(DisplayListener displayListener);

    /**
     * 获取下载进度监听器
     */
    DownloadProgressListener getDownloadProgressListener();

    /**
     * 设置下载进度监听器
     */
    @SuppressWarnings("unused")
    void setDownloadProgressListener(DownloadProgressListener downloadProgressListener);

    /**
     * 获取显示参数，此方法由Sketch调用，你无需理会即可
     */
    DisplayParams getDisplayParams();

    /**
     * 设置显示参数集，此方法由Sketch调用，你无需理会即可
     */
    void setDisplayParams(DisplayParams displayParams);

    /**
     * 是否支持大图片
     */
    boolean isSupportLargeImage();
}
