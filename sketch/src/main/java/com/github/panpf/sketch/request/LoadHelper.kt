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
package com.github.panpf.sketch.request

import android.graphics.Bitmap
import android.text.TextUtils
import android.widget.ImageView.ScaleType
import com.github.panpf.sketch.SLog
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.cache.BitmapPool
import com.github.panpf.sketch.decode.ProcessedResultCacheProcessor
import com.github.panpf.sketch.decode.ThumbnailModeDecodeHelper
import com.github.panpf.sketch.process.ImageProcessor
import com.github.panpf.sketch.uri.UriModel
import com.github.panpf.sketch.util.SketchUtils

class LoadHelper(
    private val sketch: Sketch,
    private val uri: String,
    private val loadListener: LoadListener?
) {

    companion object {
        private const val NAME = "LoadHelper"
    }

    private var sync = false
    private val loadOptions: LoadOptions = DisplayOptions()
    private var downloadProgressListener: DownloadProgressListener? = null

    /**
     * Limit request processing depth
     */
    fun requestLevel(requestLevel: RequestLevel?): LoadHelper {
        if (requestLevel != null) {
            loadOptions.requestLevel = requestLevel
        }
        return this
    }

    fun disableCacheInDisk(): LoadHelper {
        loadOptions.isCacheInDiskDisabled = true
        return this
    }

    /**
     * Disabled get reusable [Bitmap] from [BitmapPool]
     */
    fun disableBitmapPool(): LoadHelper {
        loadOptions.isBitmapPoolDisabled = true
        return this
    }

    /**
     * Support gif images
     */
    fun decodeGifImage(): LoadHelper {
        loadOptions.isDecodeGifImage = true
        return this
    }

    /**
     * Limit the maximum size of the bitmap, default value is 'new MaxSize(displayMetrics.widthPixels, displayMetrics.heightPixels)'
     */
    fun maxSize(maxSize: MaxSize?): LoadHelper {
        loadOptions.maxSize = maxSize
        return this
    }

    /**
     * Limit the maximum size of the bitmap, default value is 'new MaxSize(displayMetrics.widthPixels, displayMetrics.heightPixels)'
     */
    fun maxSize(maxWidth: Int, maxHeight: Int): LoadHelper {
        loadOptions.maxSize(maxWidth, maxHeight)
        return this
    }

    /**
     * The size of the desired bitmap
     */
    fun resize(resize: Resize?): LoadHelper {
        loadOptions.resize = resize
        return this
    }

    /**
     * The size of the desired bitmap
     */
    fun resize(reWidth: Int, reHeight: Int): LoadHelper {
        loadOptions.resize(reWidth, reHeight)
        return this
    }

    /**
     * The size of the desired bitmap
     */
    fun resize(reWidth: Int, reHeight: Int, scaleType: ScaleType): LoadHelper {
        loadOptions.resize(reWidth, reHeight, scaleType)
        return this
    }

    /**
     * Prioritize low quality [Bitmap.Config] when creating bitmaps, the priority is lower than the [.bitmapConfig] method
     */
    fun lowQualityImage(): LoadHelper {
        loadOptions.isLowQualityImage = true
        return this
    }

    /**
     * Modify Bitmap after decoding the image
     */
    fun processor(processor: ImageProcessor?): LoadHelper {
        loadOptions.processor = processor
        return this
    }

    /**
     * Specify [Bitmap.Config] to use when creating the bitmap.
     * KITKAT and above [Bitmap.Config.ARGB_4444] will be forced to be replaced with [Bitmap.Config.ARGB_8888].
     * With priority higher than [.lowQualityImage] method.
     * Applied to [android.graphics.BitmapFactory.Options.inPreferredConfig]
     */
    fun bitmapConfig(bitmapConfig: Bitmap.Config?): LoadHelper {
        loadOptions.bitmapConfig = bitmapConfig
        return this
    }

    /**
     * Priority is given to speed or quality when decoding. Applied to the [android.graphics.BitmapFactory.Options.inPreferQualityOverSpeed]
     */
    fun inPreferQualityOverSpeed(inPreferQualityOverSpeed: Boolean): LoadHelper {
        loadOptions.isInPreferQualityOverSpeed = inPreferQualityOverSpeed
        return this
    }

    /**
     * Thumbnail mode, together with the [.resize] method, gives a sharper thumbnail, see [ThumbnailModeDecodeHelper]
     */
    fun thumbnailMode(): LoadHelper {
        loadOptions.isThumbnailMode = true
        return this
    }

    /**
     * In order to speed up, save the image processed by [.processor], [.resize] or [.thumbnailMode] to the disk cache,
     * read it directly next time, refer to [ProcessedResultCacheProcessor]
     */
    fun cacheProcessedImageInDisk(): LoadHelper {
        loadOptions.isCacheProcessedImageInDisk = true
        return this
    }

    /**
     * Disabled correcting picture orientation
     */
    fun disableCorrectImageOrientation(): LoadHelper {
        loadOptions.isCorrectImageOrientationDisabled = true
        return this
    }

    /**
     * Batch setting load parameters, all reset
     */
    fun options(newOptions: LoadOptions?): LoadHelper {
        loadOptions.copy(newOptions!!)
        return this
    }

    fun downloadProgressListener(downloadProgressListener: DownloadProgressListener?): LoadHelper {
        this.downloadProgressListener = downloadProgressListener
        return this
    }

    /**
     * Synchronous execution
     */
    fun sync(): LoadHelper {
        sync = true
        return this
    }

    // todo 支持协程 spend
    fun commit(): LoadRequest? {
        // Cannot run on UI threads
        check(!(sync && SketchUtils.isMainThread)) { "Cannot sync perform the load in the UI thread " }

        // Uri cannot is empty
        if (TextUtils.isEmpty(uri)) {
            SLog.em(NAME, "Uri is empty")
            CallbackHandler.postCallbackError(loadListener, ErrorCause.URI_INVALID, sync)
            return null
        }

        // Uri type must be supported
        val uriModel = UriModel.match(sketch, uri)
        if (uriModel == null) {
            SLog.emf(NAME, "Unsupported uri type. %s", uri)
            CallbackHandler.postCallbackError(loadListener, ErrorCause.URI_NO_SUPPORT, sync)
            return null
        }
        processOptions()
        val key = SketchUtils.makeRequestKey(uri, uriModel, loadOptions.makeKey())
        return if (!checkRequestLevel(key, uriModel)) {
            null
        } else submitRequest(key, uriModel)
    }

    private fun processOptions() {
        val configuration = sketch.configuration

        // LoadRequest can not be used Resize.ByViewFixedSizeResize
        var resize = loadOptions.resize
        if (resize === Resize.BY_VIEW_FIXED_SIZE || resize === Resize.BY_VIEW_FIXED_SIZE_EXACTLY_SAME) {
            resize = null
            loadOptions.resize = null
        }

        // The width and height of the Resize must be greater than 0
        require(!(resize != null && (resize.width <= 0 || resize.height <= 0))) { "Resize width and height must be > 0" }


        // If MaxSize is not set, the default MaxSize is used.
        var maxSize = loadOptions.maxSize
        if (maxSize == null) {
            maxSize = configuration.sizeCalculator.getDefaultImageMaxSize(configuration.context)
            loadOptions.maxSize = maxSize
        }

        // The width or height of MaxSize is greater than 0.
        require(!(maxSize.width <= 0 && maxSize.height <= 0)) { "MaxSize width or height must be > 0" }


        // There is no ImageProcessor but there is a Resize, you need to set a default image cropping processor
        if (loadOptions.processor == null && resize != null) {
            loadOptions.processor = configuration.resizeProcessor
        }
        configuration.optionsFilterManager.filter(loadOptions)
    }

    private fun checkRequestLevel(key: String, uriModel: UriModel): Boolean {
        if (loadOptions.requestLevel === RequestLevel.LOCAL && uriModel.isFromNet
            && !sketch.configuration.diskCache.exist(uriModel.getDiskCacheKey(uri))
        ) {
            if (SLog.isLoggable(SLog.DEBUG)) {
                SLog.dmf(NAME, "Request cancel. %s. %s", CancelCause.PAUSE_DOWNLOAD, key)
            }
            CallbackHandler.postCallbackCanceled(loadListener, CancelCause.PAUSE_DOWNLOAD, sync)
            return false
        }
        return true
    }

    private fun submitRequest(key: String, uriModel: UriModel): LoadRequest {
        CallbackHandler.postCallbackStarted(loadListener, sync)
        val request = LoadRequest(
            sketch,
            uri,
            uriModel,
            key,
            loadOptions,
            loadListener,
            downloadProgressListener
        )
        request.isSync = sync
        if (SLog.isLoggable(SLog.DEBUG)) {
            SLog.dmf(NAME, "Run dispatch submitted. %s", key)
        }
        request.submitDispatch()
        return request
    }
}