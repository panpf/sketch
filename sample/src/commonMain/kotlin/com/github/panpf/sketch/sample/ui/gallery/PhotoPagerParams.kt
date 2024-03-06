package com.github.panpf.sketch.sample.ui.gallery

import com.github.panpf.sketch.sample.ui.model.Photo

data class PhotoPagerParams(
    val photos: List<Photo>,
    val totalCount: Int,
    val startPosition: Int,
    val initialPosition: Int
)

fun buildPhotoPagerParams(
    items: List<Photo>, position: Int
): PhotoPagerParams {
    val totalCount = items.size
    val startPosition = (position - 50).coerceAtLeast(0)
    val endPosition = (position + 50).coerceAtMost(totalCount - 1)
    val photos = items.asSequence()
        .filterIndexed { index, _ -> index in startPosition..endPosition }
        .toList()
    return PhotoPagerParams(
        photos = photos,
        totalCount = totalCount,
        startPosition = startPosition,
        initialPosition = position
    )
}