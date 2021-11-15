package com.github.panpf.sketch.zoom.block

import com.github.panpf.sketch.SketchException
import java.lang.IllegalArgumentException

class BlockSortException(
    cause: IllegalArgumentException,
    val blockList: List<Block>,
    val isUseLegacyMergeSort: Boolean
) : SketchException(cause) {

    @get:Synchronized
    override val cause: IllegalArgumentException
        get() = super.cause as IllegalArgumentException
}