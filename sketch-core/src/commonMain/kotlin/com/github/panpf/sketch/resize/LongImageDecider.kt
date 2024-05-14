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
package com.github.panpf.sketch.resize

import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.format
import kotlin.js.JsName

@JsName("newLongImageDecider")
fun LongImageDecider(): LongImageDecider {
    return DefaultLongImageDecider()
}

/**
 * Determine whether it is a long image given the image size and target size
 */
interface LongImageDecider {

    val key: String

    /**
     * Determine whether it is a long image given the image size and target size
     */
    fun isLongImage(imageSize: Size, targetSize: Size): Boolean
}

/**
 * Default [LongImageDecider] implementation
 */
open class DefaultLongImageDecider constructor(
    val sameDirectionMultiple: Float = 2.5f,
    val notSameDirectionMultiple: Float = 5.0f,
) : LongImageDecider {

    override val key: String by lazy { "Default($sameDirectionMultiple,$notSameDirectionMultiple)" }

    /**
     * Determine whether it is a long image given the image size and target size
     *
     * If the directions of image and target are the same, then the aspect ratio of
     * the two is considered as a long image when the aspect ratio reaches [sameDirectionMultiple] times,
     * otherwise it is considered as a long image when it reaches [notSameDirectionMultiple] times
     */
    override fun isLongImage(imageSize: Size, targetSize: Size): Boolean {
        if (imageSize.isEmpty || targetSize.isEmpty) {
            return false
        }
        val imageAspectRatio = imageSize.width.toFloat().div(imageSize.height).format(2)
        val targetAspectRatio = targetSize.width.toFloat().div(targetSize.height).format(2)
        val sameDirection = imageAspectRatio == 1.0f
                || targetAspectRatio == 1.0f
                || (imageAspectRatio > 1.0f && targetAspectRatio > 1.0f)
                || (imageAspectRatio < 1.0f && targetAspectRatio < 1.0f)
        val ratioMultiple = if (sameDirection) sameDirectionMultiple else notSameDirectionMultiple
        return if (ratioMultiple > 0) {
            val maxAspectRatio = targetAspectRatio.coerceAtLeast(imageAspectRatio)
            val minAspectRatio = targetAspectRatio.coerceAtMost(imageAspectRatio)
            val multipleMinAspectRatio = (minAspectRatio * ratioMultiple).format(2)
            maxAspectRatio >= multipleMinAspectRatio
        } else {
            false
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DefaultLongImageDecider) return false
        if (sameDirectionMultiple != other.sameDirectionMultiple) return false
        if (notSameDirectionMultiple != other.notSameDirectionMultiple) return false
        return true
    }

    override fun hashCode(): Int {
        var result = sameDirectionMultiple.hashCode()
        result = 31 * result + notSameDirectionMultiple.hashCode()
        return result
    }

    override fun toString(): String {
        return "DefaultLongImageDecider(sameDirectionMultiple=$sameDirectionMultiple, notSameDirectionMultiple=$notSameDirectionMultiple)"
    }
}