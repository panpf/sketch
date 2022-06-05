package com.github.panpf.sketch.decode

import androidx.annotation.IntDef
import kotlin.annotation.AnnotationRetention.SOURCE
import kotlin.annotation.AnnotationTarget.FIELD
import kotlin.annotation.AnnotationTarget.LOCAL_VARIABLE
import kotlin.annotation.AnnotationTarget.PROPERTY

@IntDef(
    value = [
        androidx.exifinterface.media.ExifInterface.ORIENTATION_UNDEFINED,
        androidx.exifinterface.media.ExifInterface.ORIENTATION_NORMAL,
        androidx.exifinterface.media.ExifInterface.ORIENTATION_FLIP_HORIZONTAL,
        androidx.exifinterface.media.ExifInterface.ORIENTATION_FLIP_VERTICAL,
        androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_90,
        androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_180,
        androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_270,
        androidx.exifinterface.media.ExifInterface.ORIENTATION_TRANSPOSE,
        androidx.exifinterface.media.ExifInterface.ORIENTATION_TRANSVERSE
    ]
)
@Retention(SOURCE)
@Target(FIELD, PROPERTY, LOCAL_VARIABLE)
annotation class ExifOrientation