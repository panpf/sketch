package com.github.panpf.sketch.sample.ui.gallery

import com.github.panpf.sketch.sample.ui.model.Photo
import kotlinx.serialization.Serializable

@Serializable
actual data class PhotoPagerParams actual constructor(
    actual val photos: List<Photo>,
    actual val totalCount: Int,
    actual val startPosition: Int,
    actual val initialPosition: Int
)