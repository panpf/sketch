@file:Suppress("unused")
@file:JvmName("Videos")

package com.github.panpf.sketch.decode

import android.media.MediaMetadataRetriever
import android.media.MediaMetadataRetriever.OPTION_CLOSEST
import android.media.MediaMetadataRetriever.OPTION_CLOSEST_SYNC
import android.media.MediaMetadataRetriever.OPTION_NEXT_SYNC
import android.media.MediaMetadataRetriever.OPTION_PREVIOUS_SYNC
import com.github.panpf.sketch.request.LoadRequest

/**
 * Set the time **in milliseconds** of the frame to extract from a video.
 *
 * Default: 0
 */
fun LoadRequest.Builder.videoFrameMillis(frameMillis: Long): LoadRequest.Builder {
    return videoFrameMicros(1000 * frameMillis)
}

// todo 增加第一帧、最后一针、中间帧、功能

/**
 * Set the time **in microseconds** of the frame to extract from a video.
 *
 * Default: 0
 */
fun LoadRequest.Builder.videoFrameMicros(frameMicros: Long): LoadRequest.Builder {
    require(frameMicros >= 0) { "frameMicros must be >= 0." }
    return setParameter(VideoFrameDecoder.VIDEO_FRAME_MICROS_KEY, frameMicros)
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
    return setParameter(VideoFrameDecoder.VIDEO_FRAME_OPTION_KEY, option)
}

/**
 * Get the time **in microseconds** of the frame to extract from a video.
 */
fun LoadRequest.videoFrameMicros(): Long? =
    parameters?.value(VideoFrameDecoder.VIDEO_FRAME_MICROS_KEY) as Long?

/**
 * Get the option for how to decode the video frame.
 */
fun LoadRequest.videoFrameOption(): Int? =
    parameters?.value(VideoFrameDecoder.VIDEO_FRAME_OPTION_KEY) as Int?
