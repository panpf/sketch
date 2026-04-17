/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
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

package com.github.panpf.sketch.util

/**
 * Resolve the video frame time in microseconds based on the specified duration, video frame time in microseconds, and video frame percentage.
 *
 * @see com.github.panpf.sketch.video.core.common.test.util.VideoCoreUtilsTest.testResolveRequestVideoFrameMicros
 */
fun resolveRequestVideoFrameMicros(
    durationMicros: Long?,
    videoFrameMicros: Long?,
    videoFramePercent: Float?,
): Long {
    if (videoFrameMicros != null && videoFrameMicros >= 0L) {
        return videoFrameMicros.coerceIn(0L, durationMicros ?: Long.MAX_VALUE)
    }
    if (durationMicros != null && durationMicros > 0L && videoFramePercent != null) {
        return (durationMicros * videoFramePercent).toLong().coerceIn(0L, durationMicros)
    }
    return 0L
}
