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

package com.github.panpf.sketch.request

private const val VIDEO_FRAME_MICROS_KEY = "sketch#video_frame_micros"
private const val VIDEO_FRAME_PERCENT_KEY = "sketch#video_frame_percent"
private const val VIDEO_FRAME_OPTION_KEY = "sketch#video_frame_option"

/**
 * Set the time **in microseconds** of the frame to extract from a video.
 *
 * Default: 0
 *
 * @see com.github.panpf.sketch.video.test.request.VideoFrameExtensionsTest.testVideoFrameMicros
 */
fun ImageRequest.Builder.videoFrameMicros(frameMicros: Long?): ImageRequest.Builder = apply {
    require(frameMicros == null || frameMicros >= 0) { "frameMicros must be >= 0." }
    if (frameMicros != null) {
        removeExtra(VIDEO_FRAME_PERCENT_KEY)
        setExtra(key = VIDEO_FRAME_MICROS_KEY, value = frameMicros)
    } else {
        removeExtra(VIDEO_FRAME_MICROS_KEY)
    }
}

/**
 * Set the time **in milliseconds** of the frame to extract from a video.
 *
 * Default: 0
 *
 * @see com.github.panpf.sketch.video.test.request.VideoFrameExtensionsTest.testVideoFrameMicros
 */
fun ImageRequest.Builder.videoFrameMillis(frameMillis: Long): ImageRequest.Builder = apply {
    videoFrameMicros(1000 * frameMillis)
}

/**
 * Get the time **in microseconds** of the frame to extract from a video.
 *
 * @see com.github.panpf.sketch.video.test.request.VideoFrameExtensionsTest.testVideoFrameMicros
 */
val ImageRequest.videoFrameMicros: Long?
    get() = extras?.value(VIDEO_FRAME_MICROS_KEY) as Long?

/**
 * Set the time **in microseconds** of the frame to extract from a video.
 *
 * Default: 0
 *
 * @see com.github.panpf.sketch.video.test.request.VideoFrameExtensionsTest.testVideoFrameMicros
 */
fun ImageOptions.Builder.videoFrameMicros(frameMicros: Long?): ImageOptions.Builder = apply {
    require(frameMicros == null || frameMicros >= 0) { "frameMicros must be >= 0." }
    if (frameMicros != null) {
        removeExtra(VIDEO_FRAME_PERCENT_KEY)
        setExtra(key = VIDEO_FRAME_MICROS_KEY, value = frameMicros)
    } else {
        removeExtra(VIDEO_FRAME_MICROS_KEY)
    }
}

/**
 * Set the time **in milliseconds** of the frame to extract from a video.
 *
 * Default: 0
 *
 * @see com.github.panpf.sketch.video.test.request.VideoFrameExtensionsTest.testVideoFrameMicros
 */
fun ImageOptions.Builder.videoFrameMillis(frameMillis: Long): ImageOptions.Builder = apply {
    videoFrameMicros(1000 * frameMillis)
}

/**
 * Get the time **in microseconds** of the frame to extract from a video.
 *
 * @see com.github.panpf.sketch.video.test.request.VideoFrameExtensionsTest.testVideoFrameMicros
 */
val ImageOptions.videoFrameMicros: Long?
    get() = extras?.value(VIDEO_FRAME_MICROS_KEY) as Long?


/**
 * Set the time of the frame to extract from a video (by percent duration).
 *
 * Default: 0.0
 *
 * @see com.github.panpf.sketch.video.test.request.VideoFrameExtensionsTest.testVideoPercent
 */
fun ImageRequest.Builder.videoFramePercent(
    framePercent: Float?
): ImageRequest.Builder = apply {
    require(framePercent == null || framePercent in 0f..1f) { "framePercent must be in 0f..1f." }
    if (framePercent != null) {
        removeExtra(VIDEO_FRAME_MICROS_KEY)
        setExtra(key = VIDEO_FRAME_PERCENT_KEY, value = framePercent)
    } else {
        removeExtra(VIDEO_FRAME_PERCENT_KEY)
    }
}

/**
 * Get the time of the frame to extract from a video (by framePercent duration).
 *
 * @see com.github.panpf.sketch.video.test.request.VideoFrameExtensionsTest.testVideoPercent
 */
val ImageRequest.videoFramePercent: Float?
    get() = extras?.value(VIDEO_FRAME_PERCENT_KEY) as Float?

/**
 * Set the time of the frame to extract from a video (by framePercent duration).
 *
 * Default: 0.0
 *
 * @see com.github.panpf.sketch.video.test.request.VideoFrameExtensionsTest.testVideoPercent
 */
fun ImageOptions.Builder.videoFramePercent(
    framePercent: Float?
) = apply {
    require(framePercent == null || framePercent in 0f..1f) { "framePercent must be in 0f..1f." }
    if (framePercent != null) {
        removeExtra(VIDEO_FRAME_MICROS_KEY)
        setExtra(key = VIDEO_FRAME_PERCENT_KEY, value = framePercent)
    } else {
        removeExtra(VIDEO_FRAME_PERCENT_KEY)
    }
}

/**
 * Get the time of the frame to extract from a video (by percent duration).
 *
 * @see com.github.panpf.sketch.video.test.request.VideoFrameExtensionsTest.testVideoPercent
 */
val ImageOptions.videoFramePercent: Float?
    get() = extras?.value(VIDEO_FRAME_PERCENT_KEY) as Float?


/**
 * Set the option for how to decode the video frame.
 *
 * Must be one of [VideoFrameOptions.PREVIOUS_SYNC], [VideoFrameOptions.NEXT_SYNC], [VideoFrameOptions.CLOSEST_SYNC], [VideoFrameOptions.CLOSEST].
 *
 * Default: [VideoFrameOptions.CLOSEST_SYNC]
 *
 * @see com.github.panpf.sketch.video.test.request.VideoFrameExtensionsTest.testVideoOption
 */
fun ImageRequest.Builder.videoFrameOption(option: Int?): ImageRequest.Builder = apply {
    require(option == null || VideoFrameOptions.isValid(option)) {
        "Invalid video frame option: $option."
    }
    if (option != null) {
        setExtra(key = VIDEO_FRAME_OPTION_KEY, value = option)
    } else {
        removeExtra(VIDEO_FRAME_OPTION_KEY)
    }
}

/**
 * Get the option for how to decode the video frame.
 *
 * @see com.github.panpf.sketch.video.test.request.VideoFrameExtensionsTest.testVideoOption
 */
val ImageRequest.videoFrameOption: Int?
    get() = extras?.value(VIDEO_FRAME_OPTION_KEY) as Int?

/**
 * Set the option for how to decode the video frame.
 *
 * Must be one of [VideoFrameOptions.PREVIOUS_SYNC], [VideoFrameOptions.NEXT_SYNC], [VideoFrameOptions.CLOSEST_SYNC], [VideoFrameOptions.CLOSEST].
 *
 * Default: [VideoFrameOptions.CLOSEST_SYNC]
 *
 * @see com.github.panpf.sketch.video.test.request.VideoFrameExtensionsTest.testVideoOption
 */
fun ImageOptions.Builder.videoFrameOption(option: Int?): ImageOptions.Builder = apply {
    require(option == null || VideoFrameOptions.isValid(option)) {
        "Invalid video frame option: $option."
    }
    if (option != null) {
        setExtra(key = VIDEO_FRAME_OPTION_KEY, value = option)
    } else {
        removeExtra(VIDEO_FRAME_OPTION_KEY)
    }
}

/**
 * Get the option for how to decode the video frame.
 *
 * @see com.github.panpf.sketch.video.test.request.VideoFrameExtensionsTest.testVideoOption
 */
val ImageOptions.videoFrameOption: Int?
    get() = extras?.value(VIDEO_FRAME_OPTION_KEY) as Int?
