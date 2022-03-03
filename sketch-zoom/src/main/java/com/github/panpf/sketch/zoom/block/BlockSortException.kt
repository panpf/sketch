package com.github.panpf.sketch.zoom.block

class BlockSortException(
    cause: IllegalArgumentException,
    val blockList: List<Block>,
    val isUseLegacyMergeSort: Boolean
) : Exception(cause) {

    @get:Synchronized
    override val cause: IllegalArgumentException
        get() = super.cause as IllegalArgumentException
}