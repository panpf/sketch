@file:Suppress("PackageDirectoryMismatch")

package com.github.panpf.sketch.stateimage

import com.github.panpf.sketch.state.ColorDrawableStateImage
import com.github.panpf.sketch.state.ConditionStateImage
import com.github.panpf.sketch.state.CurrentStateImage
import com.github.panpf.sketch.state.DrawableStateImage
import com.github.panpf.sketch.state.IconAnimatableDrawableStateImage
import com.github.panpf.sketch.state.IconDrawableStateImage
import com.github.panpf.sketch.state.MemoryCacheStateImage
import com.github.panpf.sketch.state.StateImage
import com.github.panpf.sketch.state.ThumbnailMemoryCacheStateImage
import com.github.panpf.sketch.util.ColorFetcher
import com.github.panpf.sketch.util.IntColorFetcher
import com.github.panpf.sketch.util.ResColorFetcher

@Deprecated(
    message = "Use com.github.panpf.sketch.resize.StateImage instead",
    replaceWith = ReplaceWith(
        "StateImage",
        "com.github.panpf.sketch.state.StateImage"
    )
)
typealias StateImage = StateImage

@Deprecated(
    message = "Use IconAnimatableDrawableStateImage instead",
    replaceWith = ReplaceWith(
        "IconAnimatableDrawableStateImage",
        "com.github.panpf.sketch.state.IconAnimatableDrawableStateImage"
    )
)
typealias AnimatableIconStateImage = IconAnimatableDrawableStateImage

@Deprecated(
    message = "Use IconDrawableStateImage instead",
    replaceWith = ReplaceWith(
        "IconDrawableStateImage",
        "com.github.panpf.sketch.state.IconDrawableStateImage"
    )
)
typealias IconStateImage = IconDrawableStateImage

@Deprecated(
    message = "Use com.github.panpf.sketch.resize.ColorFetcher instead",
    replaceWith = ReplaceWith(
        "ColorFetcher",
        "com.github.panpf.sketch.state.ColorFetcher"
    )
)
typealias ColorFetcher = ColorFetcher

@Deprecated(
    message = "Use IntColorFetcher instead",
    replaceWith = ReplaceWith(
        "IntColorFetcher",
        "com.github.panpf.sketch.state.IntColorFetcher"
    )
)
typealias IntColor = IntColorFetcher

@Deprecated(
    message = "Use ResColorFetcher instead",
    replaceWith = ReplaceWith(
        "ResColorFetcher",
        "com.github.panpf.sketch.state.ResColorFetcher"
    )
)
typealias ResColor = ResColorFetcher

@Deprecated(
    message = "Use ColorDrawableStateImage instead",
    replaceWith = ReplaceWith(
        "ColorDrawableStateImage",
        "com.github.panpf.sketch.state.ColorDrawableStateImage"
    )
)
typealias ColorStateImage = ColorDrawableStateImage

@Deprecated(
    message = "Use com.github.panpf.sketch.resize.CurrentStateImage instead",
    replaceWith = ReplaceWith(
        "CurrentStateImage",
        "com.github.panpf.sketch.state.CurrentStateImage"
    )
)
typealias CurrentStateImage = CurrentStateImage

@Deprecated(
    message = "Use com.github.panpf.sketch.resize.DrawableStateImage instead",
    replaceWith = ReplaceWith(
        "DrawableStateImage",
        "com.github.panpf.sketch.state.DrawableStateImage"
    )
)
typealias DrawableStateImage = DrawableStateImage

@Deprecated(
    message = "Use ConditionStateImage instead",
    replaceWith = ReplaceWith(
        "ConditionStateImage",
        "com.github.panpf.sketch.state.ConditionStateImage"
    )
)
typealias ErrorStateImage = ConditionStateImage

@Deprecated(
    message = "Use com.github.panpf.sketch.resize.MemoryCacheStateImage instead",
    replaceWith = ReplaceWith(
        "MemoryCacheStateImage",
        "com.github.panpf.sketch.state.MemoryCacheStateImage"
    )
)
typealias MemoryCacheStateImage = MemoryCacheStateImage

@Deprecated(
    message = "Use com.github.panpf.sketch.resize.ThumbnailMemoryCacheStateImage instead",
    replaceWith = ReplaceWith(
        "ThumbnailMemoryCacheStateImage",
        "com.github.panpf.sketch.state.ThumbnailMemoryCacheStateImage"
    )
)
typealias ThumbnailMemoryCacheStateImage = ThumbnailMemoryCacheStateImage