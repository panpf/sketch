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
import android.net.Uri;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;

import me.xiaopan.sketch.request.DisplayCache;
import me.xiaopan.sketch.request.DisplayListener;
import me.xiaopan.sketch.request.DisplayOptions;
import me.xiaopan.sketch.request.DisplayRequest;
import me.xiaopan.sketch.request.DownloadProgressListener;
import me.xiaopan.sketch.request.RedisplayListener;
import me.xiaopan.sketch.request.UriScheme;

public interface SketchView {

    Drawable getDrawable();

    void setImageDrawable(Drawable drawable);

    ImageView.ScaleType getScaleType();

    void clearAnimation();

    void startAnimation(Animation animation);

    ViewGroup.LayoutParams getLayoutParams();

    Resources getResources();

    int getPaddingLeft();

    int getPaddingTop();

    int getPaddingRight();

    int getPaddingBottom();


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
     * 显示来自ContentProvider的图片
     *
     * @param uri 图片URI
     */
    @SuppressWarnings("unused")
    DisplayRequest displayContentImage(Uri uri);

    /**
     * 显示已安装APP的图标
     *
     * @param packageName APP包名
     * @param versionCode APP版本号
     */
    DisplayRequest displayInstalledAppIcon(String packageName, int versionCode);

    /**
     * 准备显示图片
     */
    void onReadyDisplay(UriScheme uriScheme);

    /**
     * 获取显示参数
     */
    DisplayOptions getOptions();

    /**
     * 批量设置显示参数
     */
    void setOptions(DisplayOptions newDisplayOptions);

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
     * 获取显示缓存，此方法由Sketch调用，无需理会
     */
    DisplayCache getDisplayCache();

    /**
     * 设置显示缓存，此方法由Sketch调用，无需理会
     */
    void setDisplayCache(DisplayCache displayCache);

    /**
     * 是否开启了分块显示超大图功能
     */
    boolean isBlockDisplayLargeImageEnabled();

    /**
     * 重新显示
     *
     * @param listener 在重新显示之前你可以通过这个listener，修改缓存的options
     * @return false：重新显示失败，之前没有显示过
     */
    boolean redisplay(RedisplayListener listener);
}
