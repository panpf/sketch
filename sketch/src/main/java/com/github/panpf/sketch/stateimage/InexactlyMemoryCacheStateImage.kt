package com.github.panpf.sketch.stateimage

import android.graphics.drawable.Drawable
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.cache.CountBitmap
import com.github.panpf.sketch.datasource.DataFrom.MEMORY_CACHE
import com.github.panpf.sketch.decode.internal.isExifOrientationTransformed
import com.github.panpf.sketch.decode.internal.isInSampledTransformed
import com.github.panpf.sketch.drawable.SketchCountBitmapDrawable
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.util.SketchException
import com.github.panpf.sketch.util.format
import kotlin.math.abs

/**
 * Find a Bitmap with the same aspect ratio and not modified by Transformation as a status image from memory
 * @param uri The uri of the image, if null use ImageRequest.uriString
 */
class InexactlyMemoryCacheStateImage(
    private val uri: String? = null,
    private val defaultImage: StateImage? = null
) : StateImage {

    override fun getDrawable(
        sketch: Sketch,
        request: ImageRequest,
        exception: SketchException?
    ): Drawable? {
        val uri = uri ?: request.uriString
        val keys = sketch.memoryCache.keys()
        var cachedCountBitmap: CountBitmap? = null
        var count = 0
        for (key in keys) {
            // The key is spliced by uri and options. The options start with '_'. See RequestUtils.newKey() for details.
            var paramsStartFlagIndex = key.indexOf("?_")
            if (paramsStartFlagIndex == -1) {
                paramsStartFlagIndex = key.indexOf("&_")
            }
            val uriFromKey = if (paramsStartFlagIndex != -1) {
                key.substring(startIndex = 0, endIndex = paramsStartFlagIndex)
            } else {
                key
            }
            if (uri == uriFromKey) {
                val cached = sketch.memoryCache[key]?.takeIf {
                    val bitmap = it.bitmap ?: return@takeIf false

                    val bitmapAspectRatio = (bitmap.width.toFloat() / bitmap.height).format(1)
                    val imageAspectRatio =
                        (it.imageInfo.width.toFloat() / it.imageInfo.height).format(1)
                    val sizeSame = abs(bitmapAspectRatio - imageAspectRatio) <= 0.1f

                    val transformedList = it.transformedList
                    val noOtherTransformed =
                        transformedList == null || transformedList.all { transformed ->
                            isInSampledTransformed(transformed) || isExifOrientationTransformed(
                                transformed
                            )
                        }

                    sizeSame && noOtherTransformed
                }
                if (cached != null) {
                    cachedCountBitmap = cached
                    break
                } else if (++count >= 3) {
                    break
                }
            }
        }
        return if (cachedCountBitmap != null) {
            val resources = request.context.resources
            SketchCountBitmapDrawable(resources, cachedCountBitmap, MEMORY_CACHE)
        } else {
            defaultImage?.getDrawable(sketch, request, exception)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is InexactlyMemoryCacheStateImage) return false
        if (uri != other.uri) return false
        if (defaultImage != other.defaultImage) return false
        return true
    }

    override fun hashCode(): Int {
        var result = uri?.hashCode() ?: 0
        result = 31 * result + (defaultImage?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "InexactlyMemoryCacheStateImage(uri=$uri, defaultImage=$defaultImage)"
    }
}