package com.github.panpf.sketch.request

import android.media.MediaMetadataRetriever
import android.media.MediaMetadataRetriever.OPTION_CLOSEST
import android.media.MediaMetadataRetriever.OPTION_CLOSEST_SYNC
import android.media.MediaMetadataRetriever.OPTION_NEXT_SYNC
import android.media.MediaMetadataRetriever.OPTION_PREVIOUS_SYNC
import androidx.annotation.FloatRange

private const val VIDEO_FRAME_MICROS_KEY = "sketch#videoFrameMicros"
private const val VIDEO_FRAME_PERCENT_DURATION_KEY = "sketch#videoFramePercentDuration"
private const val VIDEO_FRAME_OPTION_KEY = "sketch#videoFrameOption"

/**
 * Set the time **in milliseconds** of the frame to extract from a video.
 *
 * Default: 0
 */
fun LoadRequest.Builder.videoFrameMillis(frameMillis: Long) = apply {
    videoFrameMicros(1000 * frameMillis)
}

/**
 * Set the time **in microseconds** of the frame to extract from a video.
 *
 * Default: 0
 */
fun LoadRequest.Builder.videoFrameMicros(frameMicros: Long) = apply {
    require(frameMicros >= 0) { "frameMicros must be >= 0." }
    removeParameter(VIDEO_FRAME_PERCENT_DURATION_KEY)
    setParameter(VIDEO_FRAME_MICROS_KEY, frameMicros)
}

/**
 * Set the time of the frame to extract from a video (by percent duration).
 *
 * Default: 0.0
 */
fun LoadRequest.Builder.videoFramePercentDuration(
    @FloatRange(from = 0.0, to = 1.0) percentDuration: Float
) = apply {
    require(percentDuration in 0.0..1.0) { "percentDuration must be in 0.0..1.0." }
    removeParameter(VIDEO_FRAME_MICROS_KEY)
    setParameter(VIDEO_FRAME_PERCENT_DURATION_KEY, percentDuration)
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
fun LoadRequest.Builder.videoFrameOption(option: Int) = apply {
    require(
        option == OPTION_PREVIOUS_SYNC ||
                option == OPTION_NEXT_SYNC ||
                option == OPTION_CLOSEST_SYNC ||
                option == OPTION_CLOSEST
    ) { "Invalid video frame option: $option." }
    setParameter(VIDEO_FRAME_OPTION_KEY, option)
}

/**
 * Set the time **in milliseconds** of the frame to extract from a video.
 *
 * Default: 0
 */
fun DisplayRequest.Builder.videoFrameMillis(frameMillis: Long) = apply {
    videoFrameMicros(1000 * frameMillis)
}

/**
 * Set the time **in microseconds** of the frame to extract from a video.
 *
 * Default: 0
 */
fun DisplayRequest.Builder.videoFrameMicros(frameMicros: Long) = apply {
    require(frameMicros >= 0) { "frameMicros must be >= 0." }
    removeParameter(VIDEO_FRAME_PERCENT_DURATION_KEY)
    setParameter(VIDEO_FRAME_MICROS_KEY, frameMicros)
}

/**
 * Set the time of the frame to extract from a video (by percent duration).
 *
 * Default: 0.0
 */
fun DisplayRequest.Builder.videoFramePercentDuration(
    @FloatRange(from = 0.0, to = 1.0) percentDuration: Float
) = apply {
    require(percentDuration in 0f..1f) { "percentDuration must be in 0f..1f." }
    removeParameter(VIDEO_FRAME_MICROS_KEY)
    setParameter(VIDEO_FRAME_PERCENT_DURATION_KEY, percentDuration)
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
fun DisplayRequest.Builder.videoFrameOption(option: Int) = apply {
    require(
        option == OPTION_PREVIOUS_SYNC ||
                option == OPTION_NEXT_SYNC ||
                option == OPTION_CLOSEST_SYNC ||
                option == OPTION_CLOSEST
    ) { "Invalid video frame option: $option." }
    setParameter(VIDEO_FRAME_OPTION_KEY, option)
}

/**
 * Get the time **in microseconds** of the frame to extract from a video.
 */
fun LoadRequest.videoFrameMicros(): Long? =
    parameters?.value(VIDEO_FRAME_MICROS_KEY) as Long?

/**
 * Get the option for how to decode the video frame.
 */
fun LoadRequest.videoFrameOption(): Int? =
    parameters?.value(VIDEO_FRAME_OPTION_KEY) as Int?

/**
 * Get the time of the frame to extract from a video (by percent duration).
 */
fun LoadRequest.videoFramePercentDuration(): Float? =
    parameters?.value(VIDEO_FRAME_PERCENT_DURATION_KEY) as Float?


/**
 * Set the time **in milliseconds** of the frame to extract from a video.
 *
 * Default: 0
 */
fun LoadOptions.Builder.videoFrameMillis(frameMillis: Long) = apply {
    videoFrameMicros(1000 * frameMillis)
}

/**
 * Set the time **in microseconds** of the frame to extract from a video.
 *
 * Default: 0
 */
fun LoadOptions.Builder.videoFrameMicros(frameMicros: Long) = apply {
    require(frameMicros >= 0) { "frameMicros must be >= 0." }
    removeParameter(VIDEO_FRAME_PERCENT_DURATION_KEY)
    setParameter(VIDEO_FRAME_MICROS_KEY, frameMicros)
}

/**
 * Set the time of the frame to extract from a video (by percent duration).
 *
 * Default: 0.0
 */
fun LoadOptions.Builder.videoFramePercentDuration(
    @FloatRange(from = 0.0, to = 1.0) percentDuration: Float
) = apply {
    require(percentDuration in 0.0..1.0) { "percentDuration must be in 0.0..1.0." }
    removeParameter(VIDEO_FRAME_MICROS_KEY)
    setParameter(VIDEO_FRAME_PERCENT_DURATION_KEY, percentDuration)
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
fun LoadOptions.Builder.videoFrameOption(option: Int) = apply {
    require(
        option == OPTION_PREVIOUS_SYNC ||
                option == OPTION_NEXT_SYNC ||
                option == OPTION_CLOSEST_SYNC ||
                option == OPTION_CLOSEST
    ) { "Invalid video frame option: $option." }
    setParameter(VIDEO_FRAME_OPTION_KEY, option)
}

/**
 * Set the time **in milliseconds** of the frame to extract from a video.
 *
 * Default: 0
 */
fun DisplayOptions.Builder.videoFrameMillis(frameMillis: Long) = apply {
    videoFrameMicros(1000 * frameMillis)
}

/**
 * Set the time **in microseconds** of the frame to extract from a video.
 *
 * Default: 0
 */
fun DisplayOptions.Builder.videoFrameMicros(frameMicros: Long) = apply {
    require(frameMicros >= 0) { "frameMicros must be >= 0." }
    removeParameter(VIDEO_FRAME_PERCENT_DURATION_KEY)
    setParameter(VIDEO_FRAME_MICROS_KEY, frameMicros)
}

/**
 * Set the time of the frame to extract from a video (by percent duration).
 *
 * Default: 0.0
 */
fun DisplayOptions.Builder.videoFramePercentDuration(
    @FloatRange(from = 0.0, to = 1.0) percentDuration: Float
) = apply {
    require(percentDuration in 0f..1f) { "percentDuration must be in 0f..1f." }
    removeParameter(VIDEO_FRAME_MICROS_KEY)
    setParameter(VIDEO_FRAME_PERCENT_DURATION_KEY, percentDuration)
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
fun DisplayOptions.Builder.videoFrameOption(option: Int) = apply {
    require(
        option == OPTION_PREVIOUS_SYNC ||
                option == OPTION_NEXT_SYNC ||
                option == OPTION_CLOSEST_SYNC ||
                option == OPTION_CLOSEST
    ) { "Invalid video frame option: $option." }
    setParameter(VIDEO_FRAME_OPTION_KEY, option)
}

/**
 * Get the time **in microseconds** of the frame to extract from a video.
 */
fun LoadOptions.videoFrameMicros(): Long? =
    parameters?.value(VIDEO_FRAME_MICROS_KEY) as Long?

/**
 * Get the option for how to decode the video frame.
 */
fun LoadOptions.videoFrameOption(): Int? =
    parameters?.value(VIDEO_FRAME_OPTION_KEY) as Int?

/**
 * Get the time of the frame to extract from a video (by percent duration).
 */
fun LoadOptions.videoFramePercentDuration(): Float? =
    parameters?.value(VIDEO_FRAME_PERCENT_DURATION_KEY) as Float?
