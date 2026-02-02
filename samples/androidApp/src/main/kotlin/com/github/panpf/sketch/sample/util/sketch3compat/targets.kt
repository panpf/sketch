@file:Suppress("PackageDirectoryMismatch")

package com.github.panpf.sketch.target

import android.view.View

@Deprecated(
    message = "Use ViewTarget instead",
    replaceWith = ReplaceWith(
        "ViewTarget",
        "com.github.panpf.sketch.target.ViewTarget"
    )
)
typealias ViewDisplayTarget = ViewTarget<in View>

@Deprecated(
    message = "Use ImageViewTarget instead",
    replaceWith = ReplaceWith(
        "ImageViewTarget",
        "com.github.panpf.sketch.target.ImageViewTarget"
    )
)
typealias ImageViewDisplayTarget = ImageViewTarget

@Deprecated(
    message = "Use GenericViewTarget instead",
    replaceWith = ReplaceWith(
        "GenericViewTarget",
        "com.github.panpf.sketch.target.GenericViewTarget"
    )
)
typealias GenericViewDisplayTarget = GenericViewTarget<in View>

@Deprecated(
    message = "Use Target instead",
    replaceWith = ReplaceWith(
        "Target",
        "com.github.panpf.sketch.target.Target"
    )
)
typealias DisplayTarget = Target

@Deprecated(
    message = "Use DownloadTarget instead",
    replaceWith = ReplaceWith(
        "DownloadTarget",
        "com.github.panpf.sketch.target.DownloadTarget"
    )
)
typealias DownloadTarget = Target

@Deprecated(
    message = "Use LoadTarget instead",
    replaceWith = ReplaceWith(
        "LoadTarget",
        "com.github.panpf.sketch.target.LoadTarget"
    )
)
typealias LoadTarget = Target