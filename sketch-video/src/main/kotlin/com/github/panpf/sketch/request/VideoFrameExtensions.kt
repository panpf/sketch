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
package com.github.panpf.sketch.request

import android.media.MediaMetadataRetriever
import android.media.MediaMetadataRetriever.OPTION_CLOSEST
import android.media.MediaMetadataRetriever.OPTION_CLOSEST_SYNC
import android.media.MediaMetadataRetriever.OPTION_NEXT_SYNC
import android.media.MediaMetadataRetriever.OPTION_PREVIOUS_SYNC
import androidx.annotation.FloatRange

private const val VIDEO_FRAME_MICROS_KEY = "sketch#video_frame_micros"
private const val VIDEO_FRAME_PERCENT_KEY = "sketch#video_frame_percent"
private const val VIDEO_FRAME_OPTION_KEY = "sketch#video_frame_option"

/**
 * Set the time **in microseconds** of the frame to extract from a video.
 *
 * Default: 0
 */
fun ImageRequest.Builder.videoFrameMicros(frameMicros: Long): ImageRequest.Builder = apply {
    require(frameMicros >= 0) { "frameMicros must be >= 0." }
    removeParameter(VIDEO_FRAME_PERCENT_KEY)
    setParameter(VIDEO_FRAME_MICROS_KEY, frameMicros)
}

/**
 * Set the time **in milliseconds** of the frame to extract from a video.
 *
 * Default: 0
 */
fun ImageRequest.Builder.videoFrameMillis(frameMillis: Long): ImageRequest.Builder = apply {
    videoFrameMicros(1000 * frameMillis)
}

/**
 * Set the time **in microseconds** of the frame to extract from a video.
 *
 * Default: 0
 */
fun LoadRequest.Builder.videoFrameMicros(frameMicros: Long): LoadRequest.Builder = apply {
    require(frameMicros >= 0) { "frameMicros must be >= 0." }
    removeParameter(VIDEO_FRAME_PERCENT_KEY)
    setParameter(VIDEO_FRAME_MICROS_KEY, frameMicros)
}

/**
 * Set the time **in milliseconds** of the frame to extract from a video.
 *
 * Default: 0
 */
fun LoadRequest.Builder.videoFrameMillis(frameMillis: Long): LoadRequest.Builder = apply {
    videoFrameMicros(1000 * frameMillis)
}

/**
 * Set the time **in microseconds** of the frame to extract from a video.
 *
 * Default: 0
 */
fun DisplayRequest.Builder.videoFrameMicros(frameMicros: Long): DisplayRequest.Builder = apply {
    require(frameMicros >= 0) { "frameMicros must be >= 0." }
    removeParameter(VIDEO_FRAME_PERCENT_KEY)
    setParameter(VIDEO_FRAME_MICROS_KEY, frameMicros)
}

/**
 * Set the time **in milliseconds** of the frame to extract from a video.
 *
 * Default: 0
 */
fun DisplayRequest.Builder.videoFrameMillis(frameMillis: Long): DisplayRequest.Builder = apply {
    videoFrameMicros(1000 * frameMillis)
}

/**
 * Get the time **in microseconds** of the frame to extract from a video.
 */
val ImageRequest.videoFrameMicros: Long?
    get() = parameters?.value(VIDEO_FRAME_MICROS_KEY) as Long?

/**
 * Set the time **in microseconds** of the frame to extract from a video.
 *
 * Default: 0
 */
fun ImageOptions.Builder.videoFrameMicros(frameMicros: Long): ImageOptions.Builder = apply {
    require(frameMicros >= 0) { "frameMicros must be >= 0." }
    removeParameter(VIDEO_FRAME_PERCENT_KEY)
    setParameter(VIDEO_FRAME_MICROS_KEY, frameMicros)
}

/**
 * Set the time **in milliseconds** of the frame to extract from a video.
 *
 * Default: 0
 */
fun ImageOptions.Builder.videoFrameMillis(frameMillis: Long): ImageOptions.Builder = apply {
    videoFrameMicros(1000 * frameMillis)
}

/**
 * Get the time **in microseconds** of the frame to extract from a video.
 */
val ImageOptions.videoFrameMicros: Long?
    get() = parameters?.value(VIDEO_FRAME_MICROS_KEY) as Long?


/**
 * Set the time of the frame to extract from a video (by percent duration).
 *
 * Default: 0.0
 */
fun ImageRequest.Builder.videoFramePercent(
    @FloatRange(from = 0.0, to = 1.0) framePercent: Float
): ImageRequest.Builder = apply {
    require(framePercent in 0f..1f) { "framePercent must be in 0f..1f." }
    removeParameter(VIDEO_FRAME_MICROS_KEY)
    setParameter(VIDEO_FRAME_PERCENT_KEY, framePercent)
}

/**
 * Set the time of the frame to extract from a video (by framePercent duration).
 *
 * Default: 0.0
 */
fun LoadRequest.Builder.videoFramePercent(
    @FloatRange(from = 0.0, to = 1.0) framePercent: Float
): LoadRequest.Builder = apply {
    require(framePercent in 0f..1f) { "framePercent must be in 0f..1f." }
    removeParameter(VIDEO_FRAME_MICROS_KEY)
    setParameter(VIDEO_FRAME_PERCENT_KEY, framePercent)
}

/**
 * Set the time of the frame to extract from a video (by framePercent duration).
 *
 * Default: 0.0
 */
fun DisplayRequest.Builder.videoFramePercent(
    @FloatRange(from = 0.0, to = 1.0) framePercent: Float
): DisplayRequest.Builder = apply {
    require(framePercent in 0f..1f) { "framePercent must be in 0f..1f." }
    removeParameter(VIDEO_FRAME_MICROS_KEY)
    setParameter(VIDEO_FRAME_PERCENT_KEY, framePercent)
}

/**
 * Get the time of the frame to extract from a video (by framePercent duration).
 */
val ImageRequest.videoFramePercent: Float?
    get() = parameters?.value(VIDEO_FRAME_PERCENT_KEY) as Float?

/**
 * Set the time of the frame to extract from a video (by framePercent duration).
 *
 * Default: 0.0
 */
fun ImageOptions.Builder.videoFramePercent(
    @FloatRange(from = 0.0, to = 1.0) framePercent: Float
) = apply {
    require(framePercent in 0f..1f) { "framePercent must be in 0f..1f." }
    removeParameter(VIDEO_FRAME_MICROS_KEY)
    setParameter(VIDEO_FRAME_PERCENT_KEY, framePercent)
}

/**
 * Get the time of the frame to extract from a video (by percent duration).
 */
val ImageOptions.videoFramePercent: Float?
    get() = parameters?.value(VIDEO_FRAME_PERCENT_KEY) as Float?


/**
 * Set the option for how to decode the video frame.
 *
 * Must be one of [OPTION_PREVIOUS_SYNC], [OPTION_NEXT_SYNC], [OPTION_CLOSEST_SYNC], [OPTION_CLOSEST].
 *
 * Default: [OPTION_CLOSEST_SYNC]
 *
 * @see MediaMetadataRetriever
 */
fun ImageRequest.Builder.videoFrameOption(option: Int): ImageRequest.Builder = apply {
    require(
        option == OPTION_PREVIOUS_SYNC ||
                option == OPTION_NEXT_SYNC ||
                option == OPTION_CLOSEST_SYNC ||
                option == OPTION_CLOSEST
    ) { "Invalid video frame option: $option." }
    setParameter(VIDEO_FRAME_OPTION_KEY, option)
}

/**
 * Set the option for how to decode the video frame.
 *
 * Must be one of [OPTION_PREVIOUS_SYNC], [OPTION_NEXT_SYNC], [OPTION_CLOSEST_SYNC], [OPTION_CLOSEST].
 *
 * Default: [OPTION_CLOSEST_SYNC]
 *
 * @see MediaMetadataRetriever
 */
fun LoadRequest.Builder.videoFrameOption(option: Int): LoadRequest.Builder = apply {
    require(
        option == OPTION_PREVIOUS_SYNC ||
                option == OPTION_NEXT_SYNC ||
                option == OPTION_CLOSEST_SYNC ||
                option == OPTION_CLOSEST
    ) { "Invalid video frame option: $option." }
    setParameter(VIDEO_FRAME_OPTION_KEY, option)
}

/**
 * Set the option for how to decode the video frame.
 *
 * Must be one of [OPTION_PREVIOUS_SYNC], [OPTION_NEXT_SYNC], [OPTION_CLOSEST_SYNC], [OPTION_CLOSEST].
 *
 * Default: [OPTION_CLOSEST_SYNC]
 *
 * @see MediaMetadataRetriever
 */
fun DisplayRequest.Builder.videoFrameOption(option: Int): DisplayRequest.Builder = apply {
    require(
        option == OPTION_PREVIOUS_SYNC ||
                option == OPTION_NEXT_SYNC ||
                option == OPTION_CLOSEST_SYNC ||
                option == OPTION_CLOSEST
    ) { "Invalid video frame option: $option." }
    setParameter(VIDEO_FRAME_OPTION_KEY, option)
}

/**
 * Get the option for how to decode the video frame.
 */
val ImageRequest.videoFrameOption: Int?
    get() = parameters?.value(VIDEO_FRAME_OPTION_KEY) as Int?

/**
 * Set the option for how to decode the video frame.
 *
 * Must be one of [OPTION_PREVIOUS_SYNC], [OPTION_NEXT_SYNC], [OPTION_CLOSEST_SYNC], [OPTION_CLOSEST].
 *
 * Default: [OPTION_CLOSEST_SYNC]
 *
 * @see MediaMetadataRetriever
 */
fun ImageOptions.Builder.videoFrameOption(option: Int): ImageOptions.Builder = apply {
    require(
        option == OPTION_PREVIOUS_SYNC ||
                option == OPTION_NEXT_SYNC ||
                option == OPTION_CLOSEST_SYNC ||
                option == OPTION_CLOSEST
    ) { "Invalid video frame option: $option." }
    setParameter(VIDEO_FRAME_OPTION_KEY, option)
}

/**
 * Get the option for how to decode the video frame.
 */
val ImageOptions.videoFrameOption: Int?
    get() = parameters?.value(VIDEO_FRAME_OPTION_KEY) as Int?
