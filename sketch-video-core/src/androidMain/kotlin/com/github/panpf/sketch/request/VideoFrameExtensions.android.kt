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

import android.media.MediaMetadataRetriever
import android.media.MediaMetadataRetriever.OPTION_CLOSEST
import android.media.MediaMetadataRetriever.OPTION_CLOSEST_SYNC
import android.media.MediaMetadataRetriever.OPTION_NEXT_SYNC
import android.media.MediaMetadataRetriever.OPTION_PREVIOUS_SYNC

private const val VIDEO_FRAME_OPTION_KEY = "sketch#video_frame_option"

/**
 * Set the option for how to decode the video frame.
 *
 * Must be one of [OPTION_PREVIOUS_SYNC], [OPTION_NEXT_SYNC], [OPTION_CLOSEST_SYNC], [OPTION_CLOSEST].
 *
 * Default: [OPTION_CLOSEST_SYNC]
 *
 * @see MediaMetadataRetriever
 * @see com.github.panpf.sketch.video.core.android.test.request.VideoFrameExtensionsTest.testVideoOption
 */
fun ImageRequest.Builder.videoFrameOption(option: Int?): ImageRequest.Builder = apply {
    require(
        option == null ||
                option == OPTION_PREVIOUS_SYNC ||
                option == OPTION_NEXT_SYNC ||
                option == OPTION_CLOSEST_SYNC ||
                option == OPTION_CLOSEST
    ) { "Invalid video frame option: $option." }
    if (option != null) {
        setExtra(key = VIDEO_FRAME_OPTION_KEY, value = option)
    } else {
        removeExtra(VIDEO_FRAME_OPTION_KEY)
    }
}

/**
 * Get the option for how to decode the video frame.
 *
 * @see com.github.panpf.sketch.video.core.android.test.request.VideoFrameExtensionsTest.testVideoOption
 */
val ImageRequest.videoFrameOption: Int?
    get() = extras?.value(VIDEO_FRAME_OPTION_KEY)

/**
 * Set the option for how to decode the video frame.
 *
 * Must be one of [OPTION_PREVIOUS_SYNC], [OPTION_NEXT_SYNC], [OPTION_CLOSEST_SYNC], [OPTION_CLOSEST].
 *
 * Default: [OPTION_CLOSEST_SYNC]
 *
 * @see MediaMetadataRetriever
 * @see com.github.panpf.sketch.video.core.android.test.request.VideoFrameExtensionsTest.testVideoOption
 */
fun ImageOptions.Builder.videoFrameOption(option: Int?): ImageOptions.Builder = apply {
    require(
        option == null ||
                option == OPTION_PREVIOUS_SYNC ||
                option == OPTION_NEXT_SYNC ||
                option == OPTION_CLOSEST_SYNC ||
                option == OPTION_CLOSEST
    ) { "Invalid video frame option: $option." }
    if (option != null) {
        setExtra(key = VIDEO_FRAME_OPTION_KEY, value = option)
    } else {
        removeExtra(VIDEO_FRAME_OPTION_KEY)
    }
}

/**
 * Get the option for how to decode the video frame.
 *
 * @see com.github.panpf.sketch.video.core.android.test.request.VideoFrameExtensionsTest.testVideoOption
 */
val ImageOptions.videoFrameOption: Int?
    get() = extras?.value(VIDEO_FRAME_OPTION_KEY)
