/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
 * Copyright (C) 2026 Kuki93
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

package com.github.panpf.sketch.decode.internal

import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFoundation.AVAsset
import platform.CoreMedia.CMTimeGetSeconds

/**
 * Get the duration of the video in microseconds. If the duration is invalid (NaN, infinite, or negative), return null.
 *
 * @see com.github.panpf.sketch.video.ios.test.decode.internal.AvAssetsTest.testDurationMicrosOrNull
 */
@OptIn(ExperimentalForeignApi::class)
internal fun AVAsset.durationMicrosOrNull(): Long? {
    val seconds = CMTimeGetSeconds(duration)
    if (seconds.isNaN() || seconds.isInfinite() || seconds < 0.0) return null
    return (seconds * 1_000_000.0).toLong().coerceAtLeast(0L)
}

/**
 * Generate a list of candidate frame times (in microseconds) based on the requested frame time and the total duration of the video.
 *
 * @see com.github.panpf.sketch.video.ios.test.decode.internal.AvAssetsTest.testFrameCandidates
 */
internal fun frameCandidates(requestFrameMicros: Long, durationMicros: Long?): List<Long> {
    val maxAllowed = durationMicros
        ?.takeIf { it > 0L }
        ?.minus(1L)
        ?.coerceAtLeast(0L)
    val candidates = linkedSetOf<Long>()
    fun add(raw: Long?) {
        val value = raw ?: return
        if (value < 0L) return
        val normalized = if (maxAllowed != null) value.coerceIn(0L, maxAllowed) else value
        candidates += normalized
    }

    add(requestFrameMicros)
    add(0L)
    add(100_000L)
    add(300_000L)
    add(durationMicros?.let { it / 3L })
    add(durationMicros?.let { it / 2L })
    return candidates.toList()
}