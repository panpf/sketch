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

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;

public interface SketchImageViewInterface {
    /**
     * 显示图片
     */
    void onDisplay();

    /**
     * 获取Drawable
     * @return Drawable
     */
    Drawable getDrawable();

    /**
     * 设置Drawable
     * @param drawable Drawable
     */
    void setImageDrawable(Drawable drawable);

    /**
     * 获取自己
     * @return View
     */
    View getSelf();

    /**
     * 获取缩放方式
     * @return 缩放方式
     */
    ImageView.ScaleType getScaleType();

    /**
     * 清除动画
     */
    void clearAnimation();

    /**
     * 开始执行动画
     * @param animation 动画
     */
    void startAnimation(Animation animation);

    /**
     * 显示图片
     * @param uri 图片Uri，支持以下几种
     * <blockquote>"http://site.com/image.png"; // from Web
     * <br>"https://site.com/image.png"; // from Web
     * <br>"/mnt/sdcard/image.png"; // from SD card
     * <br>"/mnt/sdcard/app.apk"; // from SD card apk file
     * <br>"content://media/external/audio/albumart/13"; // from content provider
     * <br>"asset://image.png"; // from assets
     * <br>"drawable://" + R.drawable.image; // from drawables (only images, non-9patch)
     * </blockquote>
     * @return Request 你可以通过Request查看请求是否完成或主动取消请求
     */
    Request displayImage(String uri);

    /**
     * 显示Drawable资源里的图片
     * @param drawableResId Drawable ID
     * @return Request 你可以通过Request查看请求是否完成或主动取消请求
     */
    Request displayResourceImage(int drawableResId);

    /**
     * 显示asset里的图片
     * @param imageFileName ASSETS文件加下的图片文件的名称
     * @return Request 你可以通过Request查看请求是否完成或主动取消请求
     */
    Request displayAssetImage(String imageFileName);

    /**
     * 显示URI指向的图片
     * @param uri 图片URI
     * @return Request 你可以通过Request查看请求是否完成或主动取消请求
     */
    Request displayURIImage(Uri uri);

    /**
     * 获取显示参数
     * @return 显示参数
     */
    DisplayOptions getDisplayOptions();

    /**
     * 设置显示参数
     * @param displayOptions 显示参数
     */
    void setDisplayOptions(DisplayOptions displayOptions);

    /**
     * 设置显示参数的名称
     * @param optionsName 显示参数的名称
     */
    void setDisplayOptions(Enum<?> optionsName);

    /**
     * 获取显示监听器
     * @return 显示监听器
     */
    DisplayListener getDisplayListener(boolean isPauseDownload);

    /**
     * 设置显示监听器
     * @param displayListener 显示监听器
     */
    void setDisplayListener(DisplayListener displayListener);

    /**
     * 获取进度监听器
     * @return 进度监听器
     */
    ProgressListener getProgressListener();

    /**
     * 设置显示进度监听器
     * @param progressListener 进度监听器
     */
    void setProgressListener(ProgressListener progressListener);

    /**
     * 获取显示请求，你可通过这个对象来查看状态或主动取消请求
     * @return 显示请求
     */
    Request getDisplayRequest();

    /**
     * 设置显示请求，此方法由Sketch调用，你无需理会即可
     * @param displayRequest 显示请求
     */
    void setDisplayRequest(Request displayRequest);

    /**
     * 获取显示参数集
     * @return 显示参数集
     */
    DisplayParams getDisplayParams();

    /**
     * 设置显示参数集
     * @param displayParams 显示参数集
     */
    void setDisplayParams(DisplayParams displayParams);
}
