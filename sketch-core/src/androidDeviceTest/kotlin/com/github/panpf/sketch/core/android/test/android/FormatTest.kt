package com.github.panpf.sketch.core.android.test.android

import com.github.panpf.sketch.core.android.test.android.Support.AtLeast
import com.github.panpf.sketch.images.ImageFile
import com.github.panpf.sketch.test.utils.isVersionAtLeast

class TestItem(val image: ImageFile, val bounds: Support, val decode: Support) {
    constructor(image: ImageFile, boundsAtLeast: Int, decodeAtLeast: Int) : this(
        image = image,
        bounds = AtLeast(boundsAtLeast) as Support,
        decode = AtLeast(decodeAtLeast) as Support
    )

    constructor(image: ImageFile, bounds: Support, decodeAtLeast: Int) : this(
        image = image,
        bounds = bounds,
        decode = AtLeast(decodeAtLeast) as Support
    )

    constructor(image: ImageFile, boundsAtLeast: Int, decode: Support) : this(
        image = image,
        bounds = AtLeast(boundsAtLeast) as Support,
        decode = decode
    )

    constructor(image: ImageFile, support: Support) : this(
        image = image,
        bounds = support,
        decode = support
    )

    constructor(image: ImageFile, atLeast: Int) : this(
        image = image,
        bounds = AtLeast(atLeast) as Support,
        decode = AtLeast(atLeast) as Support
    )
}

class TestItem2(val image: ImageFile, val decode: Support, val animated: Support) {
    constructor(image: ImageFile, decodeAtLeast: Int, animatedAtLeast: Int) : this(
        image = image,
        decode = AtLeast(decodeAtLeast) as Support,
        animated = AtLeast(animatedAtLeast) as Support
    )

    constructor(image: ImageFile, decodeAtLeast: Int, animated: Support) : this(
        image = image,
        decode = AtLeast(decodeAtLeast) as Support,
        animated = animated
    )

    constructor(image: ImageFile, decode: Support, animatedAtLeast: Int) : this(
        image = image,
        decode = decode,
        animated = AtLeast(animatedAtLeast) as Support
    )

    constructor(image: ImageFile, support: Support) : this(
        image = image,
        decode = support,
        animated = support
    )

    constructor(image: ImageFile, atLeast: Int) : this(
        image = image,
        decode = AtLeast(atLeast) as Support,
        animated = AtLeast(atLeast) as Support
    )
}

sealed interface Support {
    fun isSupport(): Boolean

    object Ok : Support {
        override fun isSupport(): Boolean = true
    }

    object No : Support {
        override fun isSupport(): Boolean = false
    }

    class AtLeast(val minSdk: Int) : Support {
        override fun isSupport(): Boolean = isVersionAtLeast(minSdk)
    }
}