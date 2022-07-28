package com.github.panpf.sketch.test.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder

fun decodeImageUseImageDecoder(
    context: Context,
    imageAssetName: String,
    sampleSize: Int? = null,
    mutable: Boolean? = null
): Bitmap {
    return ImageDecoder.decodeBitmap(
        ImageDecoder.createSource(context.assets, imageAssetName)
    ) { decoder, _, _ ->
        if (sampleSize != null) {
            decoder.setTargetSampleSize(sampleSize)
        }
        if (mutable != null) {
            decoder.isMutableRequired = mutable
        }
    }
}