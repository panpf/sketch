package com.github.panpf.sketch.test.utils

import com.github.panpf.sketch.fetch.newAssetUri

object TestAssets {
    val SAMPLE_BMP_URI = newAssetUri("sample.bmp")
    val SAMPLE_HEIC_URI = newAssetUri("sample.heic")
    val SAMPLE_JPEG_URI = newAssetUri("sample.jpeg")
    val SAMPLE_PNG_URI = newAssetUri("sample.png")
    val SAMPLE_WEBP_URI = newAssetUri("sample.webp")
    val SAMPLE_ANIM_GIF_URI = newAssetUri("sample_anim.gif")
    val SAMPLE_ANIM_WEBP_URI = newAssetUri("sample_anim.webp")
    val SAMPLE_ANIM_HEIf_URI = newAssetUri("sample_anim.heif")
    val ERROR_URI = newAssetUri("error.jpeg")
}