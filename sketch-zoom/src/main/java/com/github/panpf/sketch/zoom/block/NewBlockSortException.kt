package com.github.panpf.sketch.zoom.block

class NewBlockSortException(
    cause: IllegalArgumentException,
    val blockList: List<NewBlock>,
    val isUseLegacyMergeSort: Boolean
) : Exception(cause) {

    @get:Synchronized
    override val cause: IllegalArgumentException
        get() = super.cause as IllegalArgumentException
}