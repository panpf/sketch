package com.github.panpf.sketch.decode.internal

import com.github.panpf.sketch.Image
import com.github.panpf.sketch.annotation.WorkerThread
import com.github.panpf.sketch.decode.DecodeResult
import com.github.panpf.sketch.decode.Decoder
import com.github.panpf.sketch.decode.ImageInvalidException
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.size
import com.github.panpf.sketch.source.DataSource
import com.github.panpf.sketch.util.requiredWorkThread

abstract class AbstractHelperDecoder (
        protected val requestContext: RequestContext,
        protected val dataSource: DataSource,
        protected val decodeHelperFactory: () -> DecodeHelper,
    ) : Decoder {

    @WorkerThread
    override fun decode(): DecodeResult {
        requiredWorkThread()
        val decodeHelper = decodeHelperFactory()
        try {
            val imageInfo = decodeHelper.imageInfo
            val resize = requestContext.computeResize(imageInfo.size)
            val (image, transformeds) = if (resize.shouldClip(imageInfo.size) && decodeHelper.supportRegion) {
                try {
                    decodeRegion(decodeHelper, resize)
                } catch (e: Throwable) {
                    if (e !is UnsupportedOperationException) {
                        requestContext.sketch.logger.w("Decode region failed. '${requestContext.request.key}'")
                    }
                    decodeFull(decodeHelper, resize)
                }
            } else {
                decodeFull(decodeHelper, resize)
            }
            if (image.size.isEmpty) {
                throw ImageInvalidException("Invalid image size. size=${image.size}")
            }
            val decodeResult = DecodeResult(
                image = image,
                imageInfo = imageInfo,
                dataFrom = dataSource.dataFrom,
                resize = resize,
                transformeds = transformeds,
                extras = null,
            )
            val resizeResult = decodeResult.resize(resize)
            return resizeResult
        } finally {
            decodeHelper.close()
        }
    }

    abstract fun decodeFull(decodeHelper: DecodeHelper, resize: Resize): Pair<Image, List<String>?>

    abstract fun decodeRegion(
        decodeHelper: DecodeHelper,
        resize: Resize
    ): Pair<Image, List<String>?>
}