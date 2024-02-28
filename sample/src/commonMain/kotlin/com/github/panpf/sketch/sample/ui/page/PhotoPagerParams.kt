package com.github.panpf.sketch.sample.ui.page

import com.github.panpf.sketch.sample.ui.model.ImageDetail
import com.github.panpf.sketch.sample.ui.model.Photo

data class PhotoPagerParams(
    val imageList: List<ImageDetail>,
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
    val imageList = items.asSequence()
        .filterIndexed { index, _ -> index in startPosition..endPosition }
        .map {
            ImageDetail(
                originUrl = it.originalUrl,
                mediumUrl = it.detailPreviewUrl,
                thumbnailUrl = it.listThumbnailUrl,
            )
        }.toList()
    return PhotoPagerParams(
        imageList = imageList,
        totalCount = totalCount,
        startPosition = startPosition,
        initialPosition = position
    )
}