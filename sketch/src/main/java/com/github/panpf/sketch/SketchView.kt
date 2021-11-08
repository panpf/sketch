/*
 * Copyright (C) 2019 panpf <panpfpanpf@outlook.com>
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
package com.github.panpf.sketch

import android.content.ContentProvider
import android.content.ContentResolver
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.ImageView.ScaleType
import androidx.annotation.DrawableRes
import com.github.panpf.sketch.request.*

interface SketchView {

    fun getDrawable(): Drawable?
    fun setImageDrawable(drawable: Drawable?)
    fun getScaleType(): ScaleType
    fun clearAnimation()
    fun startAnimation(animation: Animation?)
    fun getLayoutParams(): ViewGroup.LayoutParams?
    fun getResources(): Resources
    fun getPaddingLeft(): Int
    fun getPaddingTop(): Int
    fun getPaddingRight(): Int
    fun getPaddingBottom(): Int

    /**
     * 根据指定的 uri 显示图片
     *
     * @param uri 图片 uri，支持全部的 uri 类型，请参考 [https://github.com/panpf/sketch/blob/master/docs/wiki/uri.md](https://github.com/panpf/sketch/blob/master/docs/wiki/uri.md)
     */
    fun displayImage(uri: String?): DisplayRequest?

    /**
     * 显示 drawable 资源图片
     *
     * @param drawableResId drawable 资源 id
     */
    fun displayResourceImage(@DrawableRes drawableResId: Int?): DisplayRequest?

    /**
     * 显示 assets 资源图片
     *
     * @param assetFileName assets 文件夹下的图片文件的名称
     */
    fun displayAssetImage(assetFileName: String?): DisplayRequest?

    /**
     * 显示来自 [ContentProvider] 的图片
     *
     * @param uri 来自 [ContentProvider] 的图片 uri，例如：content://、file://，使用 [ContentResolver.openInputStream] api 读取图片
     */
    fun displayContentImage(uri: String?): DisplayRequest?

    /**
     * 准备显示图片
     */
    fun onReadyDisplay(uri: String)

    /**
     * 显示参数
     */
    var options: DisplayOptions

    /**
     * 显示监听器
     */
    var displayListener: DisplayListener?

    /**
     * 下载进度监听器
     */
    var downloadProgressListener: DownloadProgressListener?

    /**
     * 显示缓存，此属性由 [Sketch] 调用
     */
    var displayCache: DisplayCache?

    /**
     * 重新显示
     *
     * @param listener 在重新显示之前你可以通过这个 listener，修改缓存的 options
     * @return false：重新显示失败，之前没有显示过
     */
    fun redisplay(listener: RedisplayListener?): Boolean

    /**
     * 是否使用更小的缩略图，此方法是为手势缩放里的分块显示超大图功能准备的
     */
    val isUseSmallerThumbnails: Boolean
}