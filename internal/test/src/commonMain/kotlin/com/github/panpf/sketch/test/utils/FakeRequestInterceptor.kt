package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.decode.ImageInfo
import com.github.panpf.sketch.request.ImageData
import com.github.panpf.sketch.request.RequestInterceptor
import com.github.panpf.sketch.resize.Precision
import com.github.panpf.sketch.resize.Resize
import com.github.panpf.sketch.resize.Scale
import com.github.panpf.sketch.source.DataFrom

class FakeRequestInterceptor : RequestInterceptor {

    companion object {
        const val SORT_WEIGHT = 0
    }

    override val key: String? = null
    override val sortWeight: Int = SORT_WEIGHT

    override suspend fun intercept(chain: RequestInterceptor.Chain): Result<ImageData> =
        runCatching {
            val image = createBitmapImage(100, 100)
            val imageInfo = ImageInfo(100, 100, "image/png")
            ImageData(
                image = image,
                imageInfo = imageInfo,
                resize = Resize(100, 100, Precision.LESS_PIXELS, Scale.CENTER_CROP),
                dataFrom = DataFrom.LOCAL,
                transformeds = null,
                extras = null
            )
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return other != null && this::class == other::class
    }

    override fun hashCode(): Int {
        return this::class.hashCode()
    }

    override fun toString(): String = "FakeRequestInterceptor"
}