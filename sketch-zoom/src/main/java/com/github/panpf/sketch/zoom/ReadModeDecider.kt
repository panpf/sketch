package com.github.panpf.sketch.zoom

fun defaultReadModeDecider(): ReadModeDecider = object : ReadModeDecider {
    override fun shouldUseByWidth(imageWidth: Int, imageHeight: Int): Boolean {
        return imageWidth > imageHeight * 3
    }

    override fun shouldUseByHeight(imageWidth: Int, imageHeight: Int): Boolean {
        return imageHeight > imageWidth * 2
    }
}

interface ReadModeDecider {

    fun shouldUseByHeight(imageWidth: Int, imageHeight: Int): Boolean

    fun shouldUseByWidth(imageWidth: Int, imageHeight: Int): Boolean
}