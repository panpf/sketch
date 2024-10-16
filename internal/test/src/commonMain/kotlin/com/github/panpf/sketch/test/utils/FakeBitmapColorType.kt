package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.decode.BitmapColorType
import com.github.panpf.sketch.decode.PlatformColorType

data object FakeBitmapColorType : BitmapColorType {

    override fun getColorType(mimeType: String?, isOpaque: Boolean): PlatformColorType? = null

    override fun toString(): String = "FakeBitmapColorType"

    override val key: String = "FakeBitmapColorType"
}
