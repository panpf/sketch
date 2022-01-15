package com.github.panpf.sketch.decode.video

import android.media.MediaMetadataRetriever
import android.media.MediaMetadataRetriever.OPTION_CLOSEST
import android.media.MediaMetadataRetriever.OPTION_CLOSEST_SYNC
import android.media.MediaMetadataRetriever.OPTION_NEXT_SYNC
import android.media.MediaMetadataRetriever.OPTION_PREVIOUS_SYNC
import androidx.annotation.FloatRange
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.LoadRequest

internal const val VIDEO_FRAME_MICROS_KEY = "sketch#video_frame_micros"
internal const val VIDEO_FRAME_PERCENT_DURATION_KEY = "sketch#video_frame_percent_duration"
internal const val VIDEO_FRAME_OPTION_KEY = "sketch#video_frame_option"

/**
 * Set the time **in milliseconds** of the frame to extract from a video.
 *
 * Default: 0
 */
fun LoadRequest.Builder.videoFrameMillis(frameMillis: Long): LoadRequest.Builder {
    return videoFrameMicros(1000 * frameMillis)
}

/**
 * Set the time **in microseconds** of the frame to extract from a video.
 *
 * Default: 0
 */
fun LoadRequest.Builder.videoFrameMicros(frameMicros: Long): LoadRequest.Builder {
    require(frameMicros >= 0) { "frameMicros must be >= 0." }
    removeParameter(VIDEO_FRAME_PERCENT_DURATION_KEY)
    return setParameter(VIDEO_FRAME_MICROS_KEY, frameMicros)
}

/**
 * Set the time of the frame to extract from a video (by percent duration).
 *
 * Default: 0.0
 */
fun LoadRequest.Builder.videoFramePercentDuration(
    @FloatRange(from = 0.0, to = 1.0) percentDuration: Float
): LoadRequest.Builder {
    require(percentDuration in 0.0..1.0) { "percentDuration must be in 0.0..1.0." }
    removeParameter(VIDEO_FRAME_MICROS_KEY)
    return setParameter(VIDEO_FRAME_PERCENT_DURATION_KEY, percentDuration)
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
fun LoadRequest.Builder.videoFrameOption(option: Int): LoadRequest.Builder {
    require(
        option == OPTION_PREVIOUS_SYNC ||
                option == OPTION_NEXT_SYNC ||
                option == OPTION_CLOSEST_SYNC ||
                option == OPTION_CLOSEST
    ) { "Invalid video frame option: $option." }
    return setParameter(VIDEO_FRAME_OPTION_KEY, option)
}

/**
 * Set the time **in milliseconds** of the frame to extract from a video.
 *
 * Default: 0
 */
fun DisplayRequest.Builder.videoFrameMillis(frameMillis: Long): DisplayRequest.Builder {
    return videoFrameMicros(1000 * frameMillis)
}

/**
 * Set the time **in microseconds** of the frame to extract from a video.
 *
 * Default: 0
 */
fun DisplayRequest.Builder.videoFrameMicros(frameMicros: Long): DisplayRequest.Builder {
    require(frameMicros >= 0) { "frameMicros must be >= 0." }
    removeParameter(VIDEO_FRAME_PERCENT_DURATION_KEY)
    return setParameter(VIDEO_FRAME_MICROS_KEY, frameMicros)
}

/**
 * Set the time of the frame to extract from a video (by percent duration).
 *
 * Default: 0.0
 */
fun DisplayRequest.Builder.videoFramePercentDuration(
    @FloatRange(from = 0.0, to = 1.0) percentDuration: Float
): DisplayRequest.Builder {
    require(percentDuration in 0f..1f) { "percentDuration must be in 0f..1f." }
    removeParameter(VIDEO_FRAME_MICROS_KEY)
    return setParameter(VIDEO_FRAME_PERCENT_DURATION_KEY, percentDuration)
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
fun DisplayRequest.Builder.videoFrameOption(option: Int): DisplayRequest.Builder {
    require(
        option == OPTION_PREVIOUS_SYNC ||
                option == OPTION_NEXT_SYNC ||
                option == OPTION_CLOSEST_SYNC ||
                option == OPTION_CLOSEST
    ) { "Invalid video frame option: $option." }
    return setParameter(VIDEO_FRAME_OPTION_KEY, option)
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
