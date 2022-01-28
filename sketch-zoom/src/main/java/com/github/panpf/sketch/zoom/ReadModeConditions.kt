package com.github.panpf.sketch.zoom

interface ReadModeConditions {
    /**
     * 根据高度计算是否可以使用阅读模式
     */
    fun canUseReadModeByHeight(imageWidth: Int, imageHeight: Int): Boolean

    /**
     * 根据宽度度计算是否可以使用阅读模式
     */
    fun canUseReadModeByWidth(imageWidth: Int, imageHeight: Int): Boolean
}

class DefaultReadModeConditions : ReadModeConditions {

    override fun canUseReadModeByHeight(imageWidth: Int, imageHeight: Int): Boolean =
        imageHeight > imageWidth * 2

    override fun canUseReadModeByWidth(imageWidth: Int, imageHeight: Int): Boolean =
        imageWidth > imageHeight * 3
}