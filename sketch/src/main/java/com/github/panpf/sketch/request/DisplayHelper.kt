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
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.widget.ImageView.ScaleType
import androidx.annotation.DrawableRes
import com.github.panpf.sketch.SLog
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.SketchView
import com.github.panpf.sketch.cache.BitmapPool
import com.github.panpf.sketch.decode.ProcessedResultCacheProcessor
import com.github.panpf.sketch.decode.ThumbnailModeDecodeHelper
import com.github.panpf.sketch.display.ImageDisplayer
import com.github.panpf.sketch.display.TransitionImageDisplayer
import com.github.panpf.sketch.drawable.SketchBitmapDrawable
import com.github.panpf.sketch.drawable.SketchLoadingDrawable
import com.github.panpf.sketch.drawable.SketchRefDrawable
import com.github.panpf.sketch.drawable.SketchShapeBitmapDrawable
import com.github.panpf.sketch.process.ImageProcessor
import com.github.panpf.sketch.shaper.ImageShaper
import com.github.panpf.sketch.state.StateImage
import com.github.panpf.sketch.uri.UriModel
import com.github.panpf.sketch.util.SketchUtils
import com.github.panpf.sketch.util.Stopwatch

class DisplayHelper(
    private val sketch: Sketch,
    private val uri: String,
    private val sketchView: SketchView
) {

    companion object {
        private const val NAME = "DisplayHelper"
    }

    private val displayOptions: DisplayOptions
    private val displayListener: DisplayListener? = sketchView.displayListener
    private val downloadProgressListener: DownloadProgressListener? =
        sketchView.downloadProgressListener
    private val stopwatch = if (SLog.isLoggable(SLog.VERBOSE)) Stopwatch() else null

    init {
        stopwatch?.start("$NAME. display use time")

        // onDisplay 一定要在最前面执行，因为 在onDisplay 中会设置一些属性，这些属性会影响到后续一些 get 方法返回的结果
        sketchView.onReadyDisplay(uri)
        stopwatch?.record("onReadyDisplay")
        displayOptions = DisplayOptions(sketchView.options)
        stopwatch?.record("init")
    }

    /**
     * Limit request processing depth
     */
    fun requestLevel(requestLevel: RequestLevel?): DisplayHelper {
        if (requestLevel != null) {
            displayOptions.requestLevel = requestLevel
        }
        return this
    }

    fun disableCacheInDisk(): DisplayHelper {
        displayOptions.isCacheInDiskDisabled = true
        return this
    }

    /**
     * Disabled get reusable [Bitmap] from [BitmapPool]
     */
    fun disableBitmapPool(): DisplayHelper {
        displayOptions.isBitmapPoolDisabled = true
        return this
    }

    /**
     * Support gif images
     */
    fun decodeGifImage(): DisplayHelper {
        displayOptions.isDecodeGifImage = true
        return this
    }

    /**
     * Limit the maximum size of the bitmap, default value is 'new MaxSize(displayMetrics.widthPixels, displayMetrics.heightPixels)'
     */
    fun maxSize(maxSize: MaxSize?): DisplayHelper {
        displayOptions.maxSize = maxSize
        return this
    }

    /**
     * Limit the maximum size of the bitmap, default value is 'new MaxSize(displayMetrics.widthPixels, displayMetrics.heightPixels)'
     */
    fun maxSize(maxWidth: Int, maxHeight: Int): DisplayHelper {
        displayOptions.maxSize(maxWidth, maxHeight)
        return this
    }

    /**
     * The size of the desired bitmap
     */
    fun resize(resize: Resize?): DisplayHelper {
        displayOptions.resize = resize
        return this
    }

    /**
     * The size of the desired bitmap
     */
    fun resize(reWidth: Int, reHeight: Int): DisplayHelper {
        displayOptions.resize(reWidth, reHeight)
        return this
    }

    /**
     * The size of the desired bitmap
     */
    fun resize(reWidth: Int, reHeight: Int, scaleType: ScaleType): DisplayHelper {
        displayOptions.resize(reWidth, reHeight, scaleType)
        return this
    }

    /**
     * Prioritize low quality [Bitmap.Config] when creating bitmaps, the priority is lower than the [.bitmapConfig] method
     */
    fun lowQualityImage(): DisplayHelper {
        displayOptions.isLowQualityImage = true
        return this
    }

    /**
     * Modify Bitmap after decoding the image
     */
    fun processor(processor: ImageProcessor?): DisplayHelper {
        displayOptions.processor = processor
        return this
    }

    /**
     * Specify [Bitmap.Config] to use when creating the bitmap.
     * KITKAT and above [Bitmap.Config.ARGB_4444] will be forced to be replaced with [Bitmap.Config.ARGB_8888].
     * With priority higher than [.lowQualityImage] method.
     * Applied to [android.graphics.BitmapFactory.Options.inPreferredConfig]
     */
    fun bitmapConfig(bitmapConfig: Bitmap.Config?): DisplayHelper {
        displayOptions.bitmapConfig = bitmapConfig
        return this
    }

    /**
     * Priority is given to speed or quality when decoding. Applied to the [android.graphics.BitmapFactory.Options.inPreferQualityOverSpeed]
     */
    fun inPreferQualityOverSpeed(inPreferQualityOverSpeed: Boolean): DisplayHelper {
        displayOptions.isInPreferQualityOverSpeed = inPreferQualityOverSpeed
        return this
    }

    /**
     * Thumbnail mode, together with the [.resize] method, gives a sharper thumbnail, see [ThumbnailModeDecodeHelper]
     */
    fun thumbnailMode(): DisplayHelper {
        displayOptions.isThumbnailMode = true
        return this
    }

    /**
     * In order to speed up, save the image processed by [.processor], [.resize] or [.thumbnailMode] to the disk cache,
     * read it directly next time, refer to [ProcessedResultCacheProcessor]
     */
    fun cacheProcessedImageInDisk(): DisplayHelper {
        displayOptions.isCacheProcessedImageInDisk = true
        return this
    }

    /**
     * Disabled correcting picture orientation
     */
    fun disableCorrectImageOrientation(): DisplayHelper {
        displayOptions.isCorrectImageOrientationDisabled = true
        return this
    }

    fun disableCacheInMemory(): DisplayHelper {
        displayOptions.isCacheInMemoryDisabled = true
        return this
    }

    /**
     * Placeholder image displayed while loading
     */
    fun loadingImage(loadingImage: StateImage?): DisplayHelper {
        displayOptions.loadingImage = loadingImage
        return this
    }

    /**
     * Placeholder image displayed while loading
     */
    fun loadingImage(@DrawableRes drawableResId: Int): DisplayHelper {
        displayOptions.loadingImage(drawableResId)
        return this
    }

    /**
     * Show this image when loading fails
     */
    fun errorImage(errorImage: StateImage?): DisplayHelper {
        displayOptions.errorImage = errorImage
        return this
    }

    /**
     * Show this image when loading fails
     */
    fun errorImage(@DrawableRes drawableResId: Int): DisplayHelper {
        displayOptions.errorImage(drawableResId)
        return this
    }

    /**
     * Show this image when pausing a download
     */
    fun pauseDownloadImage(pauseDownloadImage: StateImage?): DisplayHelper {
        displayOptions.pauseDownloadImage = pauseDownloadImage
        return this
    }

    /**
     * Show this image when pausing a download
     */
    fun pauseDownloadImage(@DrawableRes drawableResId: Int): DisplayHelper {
        displayOptions.pauseDownloadImage(drawableResId)
        return this
    }

    /**
     * Modify the shape of the image when drawing
     */
    fun shaper(imageShaper: ImageShaper?): DisplayHelper {
        displayOptions.shaper = imageShaper
        return this
    }

    /**
     * Modify the size of the image when drawing
     */
    fun shapeSize(shapeSize: ShapeSize?): DisplayHelper {
        displayOptions.shapeSize = shapeSize
        return this
    }

    /**
     * Modify the size of the image when drawing
     */
    fun shapeSize(shapeWidth: Int, shapeHeight: Int): DisplayHelper {
        displayOptions.shapeSize(shapeWidth, shapeHeight)
        return this
    }

    /**
     * Modify the size of the image when drawing
     */
    fun shapeSize(shapeWidth: Int, shapeHeight: Int, scaleType: ScaleType?): DisplayHelper {
        displayOptions.shapeSize(shapeWidth, shapeHeight, scaleType)
        return this
    }

    /**
     * Display image after image loading is completeThe, default value is [com.github.panpf.sketch.display.DefaultImageDisplayer]
     */
    fun displayer(displayer: ImageDisplayer?): DisplayHelper {
        displayOptions.displayer = displayer
        return this
    }

    /**
     * Batch setting display parameters, all reset
     */
    fun options(newOptions: DisplayOptions?): DisplayHelper {
        displayOptions.copy(newOptions!!)
        return this
    }

    private val errorDrawable: Drawable?
        get() {
            var drawable: Drawable? = null
            if (displayOptions.errorImage != null) {
                val context = sketch.configuration.context
                drawable =
                    displayOptions.errorImage!!.getDrawable(context, sketchView, displayOptions)
            } else if (displayOptions.loadingImage != null) {
                val context = sketch.configuration.context
                drawable =
                    displayOptions.loadingImage!!.getDrawable(context, sketchView, displayOptions)
            }
            return drawable
        }

    fun commit(): DisplayRequest? {
        // Cannot run on non-UI threads
        check(SketchUtils.isMainThread) { "Cannot run on non-UI thread" }

        // Uri cannot is empty
        if (TextUtils.isEmpty(uri)) {
            SLog.emf(
                NAME, "Uri is empty. view(%s)", Integer.toHexString(
                    sketchView.hashCode()
                )
            )
            sketchView.setImageDrawable(errorDrawable)
            CallbackHandler.postCallbackError(displayListener, ErrorCause.URI_INVALID, false)
            return null
        }

        // Uri type must be supported
        val uriModel = UriModel.match(sketch, uri)
        if (uriModel == null) {
            SLog.emf(
                NAME, "Unsupported uri type. %s. view(%s)", uri, Integer.toHexString(
                    sketchView.hashCode()
                )
            )
            sketchView.setImageDrawable(errorDrawable)
            CallbackHandler.postCallbackError(displayListener, ErrorCause.URI_NO_SUPPORT, false)
            return null
        }
        processOptions()
        stopwatch?.record("processOptions")
        saveOptions()
        stopwatch?.record("saveOptions")
        val key = SketchUtils.makeRequestKey(uri, uriModel, displayOptions.makeKey())
        var checkResult = checkMemoryCache(key)
        stopwatch?.record("checkMemoryCache")
        if (!checkResult) {
            stopwatch?.print(key)
            return null
        }
        checkResult = checkRequestLevel(key, uriModel)
        stopwatch?.record("checkRequestLevel")
        if (!checkResult) {
            stopwatch?.print(key)
            return null
        }
        val potentialRequest = checkRepeatRequest(key)
        stopwatch?.record("checkRepeatRequest")
        if (potentialRequest != null) {
            stopwatch?.print(key)
            return potentialRequest
        }
        val request = submitRequest(key, uriModel)
        stopwatch?.print(key)
        return request
    }

    private fun processOptions() {
        val configuration = sketch.configuration
        val fixedSize = sketch.configuration.sizeCalculator.calculateImageFixedSize(
            sketchView
        )
        val scaleType = sketchView.getScaleType()

        // Replace ShapeSize.ByViewFixedSizeShapeSize
        var shapeSize = displayOptions.shapeSize
        if (shapeSize === ShapeSize.BY_VIEW_FIXED_SIZE) {
            if (fixedSize != null) {
                shapeSize = ShapeSize(fixedSize.width, fixedSize.height, scaleType)
                displayOptions.shapeSize = shapeSize
            } else {
                throw IllegalStateException(
                    "ImageView's width and height are not fixed," +
                            " can not be applied with the ShapeSize.byViewFixedSize() function"
                )
            }
        }

        // ShapeSize must set ScaleType
        if (shapeSize != null && shapeSize.scaleType == null) {
            shapeSize.scaleType = scaleType
        }

        // The width and height of the ShapeSize must be greater than 0
        require(!(shapeSize != null && (shapeSize.width == 0 || shapeSize.height == 0))) { "ShapeSize width and height must be > 0" }


        // Replace Resize.ByViewFixedSizeShapeSize
        var resize = displayOptions.resize
        if (resize === Resize.BY_VIEW_FIXED_SIZE || resize === Resize.BY_VIEW_FIXED_SIZE_EXACTLY_SAME) {
            if (fixedSize != null) {
                resize = Resize(fixedSize.width, fixedSize.height, scaleType, resize.mode)
                displayOptions.resize = resize
            } else {
                throw IllegalStateException(
                    "ImageView's width and height are not fixed," +
                            " can not be applied with the Resize.byViewFixedSize() function"
                )
            }
        }

        // Resize must set ScaleType
        if (resize != null && resize.scaleType == null) {
            resize.scaleType = scaleType
        }

        // The width and height of the Resize must be greater than 0
        require(!(resize != null && (resize.width <= 0 || resize.height <= 0))) { "Resize width and height must be > 0" }


        // If MaxSize is not set, the default MaxSize is used.
        var maxSize = displayOptions.maxSize
        if (maxSize == null) {
            val imageSizeCalculator = sketch.configuration.sizeCalculator
            maxSize = imageSizeCalculator.calculateImageMaxSize(sketchView)
            if (maxSize == null) {
                maxSize = imageSizeCalculator.getDefaultImageMaxSize(configuration.context)
            }
            displayOptions.maxSize = maxSize
        }

        // The width or height of MaxSize is greater than 0.
        require(!(maxSize.width <= 0 && maxSize.height <= 0)) { "MaxSize width or height must be > 0" }


        // There is no ImageProcessor but there is a Resize, you need to set a default image cropping processor
        if (displayOptions.processor == null && resize != null) {
            displayOptions.processor = configuration.resizeProcessor
        }


        // When using TransitionImageDisplayer, if you use a loadingImage , you must have a ShapeSize
        if (displayOptions.displayer is TransitionImageDisplayer
            && displayOptions.loadingImage != null && displayOptions.shapeSize == null
        ) {
            if (fixedSize != null) {
                displayOptions.shapeSize(fixedSize.width, fixedSize.height)
            } else {
                val layoutParams = sketchView.getLayoutParams()
                val widthName = SketchUtils.viewLayoutFormatted(layoutParams?.width ?: -1)
                val heightName = SketchUtils.viewLayoutFormatted(layoutParams?.height ?: -1)
                val errorInfo = String.format(
                    "If you use TransitionImageDisplayer and loadingImage, " +
                            "You must be setup ShapeSize or imageView width and height must be fixed. width=%s, height=%s",
                    widthName,
                    heightName
                )
                if (SLog.isLoggable(SLog.DEBUG)) {
                    SLog.dmf(
                        NAME, "%s. view(%s). %s", errorInfo, Integer.toHexString(
                            sketchView.hashCode()
                        ), uri
                    )
                }
                throw IllegalArgumentException(errorInfo)
            }
        }
        configuration.optionsFilterManager.filter(displayOptions)
    }

    private fun saveOptions() {
        var displayCache = sketchView.displayCache
        if (displayCache == null) {
            displayCache = DisplayCache()
            sketchView.displayCache = displayCache
        }
        displayCache.uri = uri
        displayCache.options.copy(displayOptions)
    }

    private fun checkMemoryCache(key: String): Boolean {
        if (displayOptions.isCacheInMemoryDisabled) {
            return true
        }
        val cachedRefBitmap = sketch.configuration.memoryCache[key] ?: return true
        if (cachedRefBitmap.isRecycled) {
            sketch.configuration.memoryCache.remove(key)
            val viewCode = Integer.toHexString(sketchView.hashCode())
            SLog.wmf(
                NAME,
                "Memory cache drawable recycled. %s. view(%s)",
                cachedRefBitmap.info,
                viewCode
            )
            return true
        }

        // Gif does not use memory cache
        if (displayOptions.isDecodeGifImage && "image/gif".equals(
                cachedRefBitmap.attrs.mimeType,
                ignoreCase = true
            )
        ) {
            SLog.dmf(
                NAME,
                "The picture in the memory cache is just the first frame of the gif. It cannot be used. %s",
                cachedRefBitmap.info
            )
            return true
        }
        cachedRefBitmap.setIsWaitingUse(String.format("%s:waitingUse:fromMemory", NAME), true)
        if (SLog.isLoggable(SLog.DEBUG)) {
            val viewCode = Integer.toHexString(sketchView.hashCode())
            SLog.dmf(
                NAME,
                "Display image completed. %s. %s. view(%s)",
                ImageFrom.MEMORY_CACHE.name,
                cachedRefBitmap.info,
                viewCode
            )
        }
        val refBitmapDrawable = SketchBitmapDrawable(cachedRefBitmap, ImageFrom.MEMORY_CACHE)
        val finalDrawable: Drawable
        finalDrawable = if (displayOptions.shapeSize != null || displayOptions.shaper != null) {
            SketchShapeBitmapDrawable(
                sketch.configuration.context, refBitmapDrawable,
                displayOptions.shapeSize, displayOptions.shaper
            )
        } else {
            refBitmapDrawable
        }
        val imageDisplayer = displayOptions.displayer
        if (imageDisplayer != null && imageDisplayer.isAlwaysUse) {
            imageDisplayer.display(sketchView, finalDrawable)
        } else {
            sketchView.setImageDrawable(finalDrawable)
        }
        displayListener?.onCompleted(finalDrawable, ImageFrom.MEMORY_CACHE, cachedRefBitmap.attrs)
        (finalDrawable as SketchRefDrawable).setIsWaitingUse(
            String.format(
                "%s:waitingUse:finish",
                NAME
            ), false
        )
        return false
    }

    private fun checkRequestLevel(key: String, uriModel: UriModel): Boolean {
        if (displayOptions.requestLevel === RequestLevel.MEMORY) {
            if (SLog.isLoggable(SLog.DEBUG)) {
                SLog.dmf(
                    NAME,
                    "Request cancel. %s. view(%s). %s",
                    CancelCause.PAUSE_LOAD,
                    Integer.toHexString(
                        sketchView.hashCode()
                    ),
                    key
                )
            }
            var loadingDrawable: Drawable? = null
            if (displayOptions.loadingImage != null) {
                val context = sketch.configuration.context
                loadingDrawable =
                    displayOptions.loadingImage!!.getDrawable(context, sketchView, displayOptions)
            }
            sketchView.clearAnimation()
            sketchView.setImageDrawable(loadingDrawable)
            CallbackHandler.postCallbackCanceled(displayListener, CancelCause.PAUSE_LOAD, false)
            return false
        }
        if (displayOptions.requestLevel === RequestLevel.LOCAL && uriModel.isFromNet
            && !sketch.configuration.diskCache.exist(uriModel.getDiskCacheKey(uri))
        ) {
            if (SLog.isLoggable(SLog.DEBUG)) {
                SLog.dmf(
                    NAME,
                    "Request cancel. %s. view(%s). %s",
                    CancelCause.PAUSE_DOWNLOAD,
                    Integer.toHexString(
                        sketchView.hashCode()
                    ),
                    key
                )
            }
            var drawable: Drawable? = null
            if (displayOptions.pauseDownloadImage != null) {
                val context = sketch.configuration.context
                drawable = displayOptions.pauseDownloadImage!!.getDrawable(
                    context,
                    sketchView,
                    displayOptions
                )
                sketchView.clearAnimation()
            } else if (displayOptions.loadingImage != null) {
                val context = sketch.configuration.context
                drawable =
                    displayOptions.loadingImage!!.getDrawable(context, sketchView, displayOptions)
            }
            sketchView.setImageDrawable(drawable)
            CallbackHandler.postCallbackCanceled(displayListener, CancelCause.PAUSE_DOWNLOAD, false)
            return false
        }
        return true
    }

    /**
     * Attempting to cancel an existing request
     *
     * @return null: The request has been canceled or no existing; otherwise: the request is repeated
     */
    private fun checkRepeatRequest(key: String): DisplayRequest? {
        val potentialRequest = SketchUtils.findDisplayRequest(sketchView)
        if (potentialRequest != null && !potentialRequest.isFinished) {
            if (key == potentialRequest.key) {
                if (SLog.isLoggable(SLog.DEBUG)) {
                    SLog.dmf(
                        NAME, "Repeat request. key=%s. view(%s)", key, Integer.toHexString(
                            sketchView.hashCode()
                        )
                    )
                }
                return potentialRequest
            } else {
                if (SLog.isLoggable(SLog.DEBUG)) {
                    SLog.dmf(
                        NAME, "Cancel old request. newKey=%s. oldKey=%s. view(%s)",
                        key, potentialRequest.key, Integer.toHexString(sketchView.hashCode())
                    )
                }
                potentialRequest.cancel(CancelCause.BE_REPLACED_ON_HELPER)
            }
        }
        return null
    }

    private fun submitRequest(key: String, uriModel: UriModel): DisplayRequest {
        CallbackHandler.postCallbackStarted(displayListener, false)
        stopwatch?.record("callbackStarted")
        val requestAndViewBinder = RequestAndViewBinder(sketchView)
        val request = DisplayRequest(
            sketch, uri, uriModel, key, displayOptions, sketchView.isUseSmallerThumbnails,
            requestAndViewBinder, displayListener, downloadProgressListener
        )
        stopwatch?.record("createRequest")
        val loadingDrawable: SketchLoadingDrawable
        val loadingImage = displayOptions.loadingImage
        loadingDrawable = if (loadingImage != null) {
            val context = sketch.configuration.context
            val drawable = loadingImage.getDrawable(context, sketchView, displayOptions)
            SketchLoadingDrawable(drawable, request)
        } else {
            SketchLoadingDrawable(null, request)
        }
        stopwatch?.record("createLoadingImage")
        sketchView.setImageDrawable(loadingDrawable)
        stopwatch?.record("setLoadingImage")
        if (SLog.isLoggable(SLog.DEBUG)) {
            SLog.dmf(
                NAME, "Run dispatch submitted. view(%s). %s", Integer.toHexString(
                    sketchView.hashCode()
                ), key
            )
        }
        request.submitDispatch()
        stopwatch?.record("submitRequest")
        return request
    }
}