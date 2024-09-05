package com.github.panpf.sketch.core.common.test.decode.internal

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.decode.DecodeResult
import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.decode.internal.HelperDecoder
import com.github.panpf.sketch.decode.internal.createInSampledTransformed
import com.github.panpf.sketch.decode.internal.createResizeTransformed
import com.github.panpf.sketch.decode.internal.createSubsamplingTransformed
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.resize.Precision
import com.github.panpf.sketch.size
import com.github.panpf.sketch.source.DataFrom
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.corners
import com.github.panpf.sketch.test.utils.createDecodeHelper
import com.github.panpf.sketch.test.utils.toRequestContext
import com.github.panpf.sketch.util.Rect
import com.github.panpf.sketch.util.Size
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class HelperDecoderTest {

    private suspend fun ImageRequest.helperDecode(sketch: Sketch): DecodeResult {
        val request = this
        val requestContext = request.toRequestContext(sketch)
        val fetchResult =
            sketch.components.newFetcherOrThrow(request.toRequestContext(sketch, Size.Empty))
                .fetch().getOrThrow()
        val dataSource = fetchResult.dataSource
        val helperDecoder = HelperDecoder(requestContext, dataSource) {
            createDecodeHelper(request, dataSource)
        }
        return helperDecoder.decode()
    }

    // TODO test: decodeImageInfo

    @Test
    fun testDecode() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        val exifRotate90ImageUri = ResourceImages.clockExifRotate90.uri

        val result1 = ImageRequest(context, exifRotate90ImageUri) {
            size(3000, 3000)
            precision(Precision.LESS_PIXELS)
        }.helperDecode(sketch).apply {
            assertEquals(ImageInfo(1500, 750, "image/jpeg"), imageInfo)
            assertEquals(imageInfo.size, image.size)
            // TODO resize
            assertEquals(DataFrom.LOCAL, dataFrom)
            assertNull(transformeds)
        }

        ImageRequest(context, exifRotate90ImageUri) {
            size(3000, 3000)
            precision(Precision.LESS_PIXELS)
        }.helperDecode(sketch).apply {
            assertEquals(ImageInfo(1500, 750, "image/jpeg"), imageInfo)
            assertEquals(imageInfo.size, image.size)
            assertEquals(DataFrom.LOCAL, dataFrom)
            assertNull(transformeds)
            assertEquals(result1.image.corners(), image.corners())
        }

        val result3 = ImageRequest(context, exifRotate90ImageUri).newRequest {
            size(100, 200)
            precision(Precision.EXACTLY)
        }.helperDecode(sketch).apply {
            assertEquals(ImageInfo(1500, 750, "image/jpeg"), imageInfo)
            assertEquals(Size(100, 200), image.size)
            assertEquals(DataFrom.LOCAL, dataFrom)
            assertEquals(
                listOf(
                    createInSampledTransformed(4),
                    createSubsamplingTransformed(Rect(562, 0, 937, 750)),
                    createResizeTransformed(resize)
                ),
                transformeds
            )
        }

        ImageRequest(context, exifRotate90ImageUri).newRequest {
            size(100, 200)
            precision(Precision.EXACTLY)
        }.helperDecode(sketch).apply {
            assertEquals(ImageInfo(1500, 750, "image/jpeg"), imageInfo)
            assertEquals(Size(100, 200), image.size)
            assertEquals(DataFrom.LOCAL, dataFrom)
            assertEquals(
                listOf(
                    createInSampledTransformed(4),
                    createSubsamplingTransformed(Rect(562, 0, 937, 750)),
                    createResizeTransformed(resize)
                ),
                transformeds
            )
            assertEquals(result3.image.corners(), image.corners())
        }

        val result5 = ImageRequest(context, exifRotate90ImageUri).newRequest {
            size(100, 200)
            precision(Precision.SAME_ASPECT_RATIO)
        }.helperDecode(sketch).apply {
            assertEquals(ImageInfo(1500, 750, "image/jpeg"), imageInfo)
            assertEquals(Size(94, 188), image.size)
            assertEquals(DataFrom.LOCAL, dataFrom)
            assertEquals(
                listOf(
                    createInSampledTransformed(4),
                    createSubsamplingTransformed(Rect(562, 0, 937, 750))
                ),
                transformeds
            )
        }

        ImageRequest(context, exifRotate90ImageUri).newRequest {
            size(100, 200)
            precision(Precision.SAME_ASPECT_RATIO)
        }.helperDecode(sketch).apply {
            assertEquals(ImageInfo(1500, 750, "image/jpeg"), imageInfo)
            assertEquals(Size(94, 188), image.size)
            assertEquals(DataFrom.LOCAL, dataFrom)
            assertEquals(
                listOf(
                    createInSampledTransformed(4),
                    createSubsamplingTransformed(Rect(562, 0, 937, 750))
                ),
                transformeds
            )
            assertEquals(result5.image.corners(), image.corners())
        }

        val result7 = ImageRequest(context, exifRotate90ImageUri).newRequest {
            size(100, 200)
            precision(Precision.LESS_PIXELS)
        }.helperDecode(sketch).apply {
            assertEquals(ImageInfo(1500, 750, "image/jpeg"), imageInfo)
            assertEquals(Size(188, 94), image.size)
            assertEquals(DataFrom.LOCAL, dataFrom)
            assertEquals(listOf(createInSampledTransformed(8)), transformeds)
        }

        ImageRequest(context, exifRotate90ImageUri).newRequest {
            size(100, 200)
            precision(Precision.LESS_PIXELS)
        }.helperDecode(sketch).apply {
            assertEquals(ImageInfo(1500, 750, "image/jpeg"), imageInfo)
            assertEquals(Size(188, 94), image.size)
            assertEquals(DataFrom.LOCAL, dataFrom)
            assertEquals(listOf(createInSampledTransformed(8)), transformeds)
            assertEquals(result7.image.corners(), image.corners())
        }

        val result9 = ImageRequest(context, ResourceImages.bmp.uri) {
            size(100, 200)
            precision(Precision.EXACTLY)
        }.helperDecode(sketch).apply {
            assertEquals(ImageInfo(700, 1012, "image/bmp"), imageInfo)
            assertEquals(Size(100, 200), image.size)
            assertEquals(DataFrom.LOCAL, dataFrom)
            assertEquals(
                listOf(
                    createInSampledTransformed(8),
                    createSubsamplingTransformed(Rect(97, 0, 603, 1012)),
                    createResizeTransformed(resize)
                ),
                transformeds
            )
        }

        ImageRequest(context, ResourceImages.bmp.uri).newRequest {
            size(100, 200)
            precision(Precision.EXACTLY)
        }.helperDecode(sketch).apply {
            assertEquals(ImageInfo(700, 1012, "image/bmp"), imageInfo)
            assertEquals(Size(100, 200), image.size)
            assertEquals(DataFrom.LOCAL, dataFrom)
            assertEquals(
                listOf(
                    createInSampledTransformed(8),
                    createSubsamplingTransformed(Rect(97, 0, 603, 1012)),
                    createResizeTransformed(resize)
                ),
                transformeds
            )
            assertEquals(result9.image.corners(), image.corners())
        }
    }
}