/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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