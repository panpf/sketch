package com.github.panpf.sketch.sample.ui.gallery

import com.github.panpf.sketch.sample.ui.model.Photo

data class PhotoPagerParams(
    val photos: List<Photo>,
    val startPosition: Int,
    val initialPosition: Int
)

fun buildPhotoPagerParams(
    items: List<Photo>, position: Int
): PhotoPagerParams {
    val startPosition = (position - 100).coerceAtLeast(0)
    val endPosition = (position + 100).coerceAtMost(items.size - 1)
    val photos = items.asSequence()
        .filterIndexed { index, _ -> index in startPosition..endPosition }
        .toList()
    return PhotoPagerParams(
        photos = photos,
        startPosition = startPosition,
        initialPosition = position
    )
}