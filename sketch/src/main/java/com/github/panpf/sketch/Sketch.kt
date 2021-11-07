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
import android.content.Context
import androidx.annotation.DrawableRes
import com.github.panpf.sketch.request.*
import com.github.panpf.sketch.uri.*
import com.github.panpf.sketch.util.SketchUtils

class Sketch private constructor(context: Context) {

    companion object {
        const val META_DATA_KEY_INITIALIZER = "SKETCH_INITIALIZER"

        @Volatile
        private var instance: Sketch? = null

        /**
         * Get a unique instance
         */
        // todo 取消默认提供的单例，改成 builder 方式创建
        @JvmStatic
        fun with(context: Context): Sketch {
            val oldInstance = instance
            if (oldInstance != null) return oldInstance
            synchronized(Sketch::class.java) {
                val oldInstance1 = instance
                if (oldInstance1 != null) return oldInstance1
                val newInstance = Sketch(context)
                SLog.iff(
                    "Version %s %s(%d) -> %s",
                    BuildConfig.BUILD_TYPE,
                    BuildConfig.VERSION_NAME,
                    BuildConfig.VERSION_CODE,
                    newInstance.configuration.toString()
                )
                val initializer = SketchUtils.findInitializer(context)
                initializer?.onInitialize(context.applicationContext, newInstance.configuration)
                instance = newInstance
                return newInstance
            }
        }
    }

    val configuration: Configuration = Configuration(context)

    /**
     * Download image by uri, only supports http and https. Finally, the [DownloadHelper.commit] method is called to submit
     */
    fun download(uri: String, listener: DownloadListener?): DownloadHelper {
        return DownloadHelper(this, uri, listener)
    }

    /**
     * Load image into memory by uri. Finally, the [LoadHelper.commit] method is called to submit
     *
     * @see AndroidResUriModel
     *
     * @see ApkIconUriModel
     *
     * @see AppIconUriModel
     *
     * @see AssetUriModel
     *
     * @see Base64UriModel
     *
     * @see Base64VariantUriModel
     *
     * @see ContentUriModel
     *
     * @see DrawableUriModel
     *
     * @see FileUriModel
     *
     * @see FileVariantUriModel
     *
     * @see HttpUriModel
     *
     * @see HttpsUriModel
     */
    // todo 支持异步和同步两种方式
    fun load(uri: String, listener: LoadListener?): LoadHelper {
        return LoadHelper(this, uri, listener)
    }

    /**
     * Load image into memory from asset resource. Finally, need the [LoadHelper.commit] method is called to submit
     *
     * @see AssetUriModel
     */
    fun loadFromAsset(assetFileName: String, listener: LoadListener?): LoadHelper {
        val uri = AssetUriModel.makeUri(assetFileName)
        return LoadHelper(this, uri, listener)
    }

    /**
     * Load image into memory from drawable resource. Finally, need the [LoadHelper.commit] method is called to submit
     *
     * @see DrawableUriModel
     */
    fun loadFromResource(@DrawableRes drawableResId: Int, listener: LoadListener?): LoadHelper {
        val uri = DrawableUriModel.makeUri(drawableResId)
        return LoadHelper(this, uri, listener)
    }

    /**
     * Load image into memory from [ContentProvider]. Finally, need the [LoadHelper.commit] method is called to submit
     *
     * @see ContentUriModel
     */
    fun loadFromContent(uri: String, listener: LoadListener?): LoadHelper {
        return LoadHelper(this, uri, listener)
    }

    /**
     * Display image to [SketchView] by uri. Finally, need the [DisplayHelper.commit] method is called to submit
     *
     * @see AndroidResUriModel
     *
     * @see ApkIconUriModel
     *
     * @see AppIconUriModel
     *
     * @see AssetUriModel
     *
     * @see Base64UriModel
     *
     * @see Base64VariantUriModel
     *
     * @see ContentUriModel
     *
     * @see DrawableUriModel
     *
     * @see FileUriModel
     *
     * @see FileVariantUriModel
     *
     * @see HttpUriModel
     *
     * @see HttpsUriModel
     */
    fun display(uri: String, sketchView: SketchView): DisplayHelper {
        return DisplayHelper(this, uri, sketchView)
    }

    /**
     * Display image to [SketchView] from asset resource. Finally, need the [DisplayHelper.commit] method is called to submit
     *
     * @see AssetUriModel
     */
    fun displayFromAsset(assetFileName: String, sketchView: SketchView): DisplayHelper {
        val uri = AssetUriModel.makeUri(assetFileName)
        return DisplayHelper(this, uri, sketchView)
    }

    /**
     * Display image to [SketchView] from drawable resource. Finally, need the [DisplayHelper.commit] method is called to submit
     *
     * @see DrawableUriModel
     */
    fun displayFromResource(
        @DrawableRes drawableResId: Int,
        sketchView: SketchView
    ): DisplayHelper {
        val uri = DrawableUriModel.makeUri(drawableResId)
        return DisplayHelper(this, uri, sketchView)
    }

    /**
     * Display image to [SketchView] from asset [ContentProvider]. Finally, need the [DisplayHelper.commit] method is called to submit
     *
     * @see ContentUriModel
     */
    fun displayFromContent(uri: String, sketchView: SketchView): DisplayHelper {
        return DisplayHelper(this, uri, sketchView)
    }

    @Deprecated("")
    fun onTrimMemory(level: Int) {
        SLog.wf("Trim of memory, level= %s", SketchUtils.getTrimLevelName(level))
        configuration.memoryCache.trimMemory(level)
        configuration.bitmapPool.trimMemory(level)
    }

    @Deprecated("")
    fun onLowMemory() {
        SLog.w("Memory is very low, clean memory cache and bitmap pool")
        configuration.memoryCache.clear()
        configuration.bitmapPool.clear()
    }
}