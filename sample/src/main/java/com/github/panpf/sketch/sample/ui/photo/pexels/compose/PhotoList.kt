package com.github.panpf.sketch.sample.ui.photo.pexels.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.GridCells.Fixed
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.compose.AsyncImage
import com.github.panpf.sketch.sample.R.color
import com.github.panpf.sketch.sample.R.drawable
import com.github.panpf.sketch.sample.model.Photo
import com.github.panpf.sketch.sample.ui.common.compose.itemsIndexed
import com.github.panpf.sketch.stateimage.IconStateImage
import com.github.panpf.sketch.stateimage.ResColor
import com.github.panpf.tools4a.dimen.ktx.px2dp
import com.github.panpf.tools4a.display.ktx.getScreenWidth
import kotlinx.coroutines.flow.Flow

@Composable
@ExperimentalFoundationApi
fun PhotoListContent(photoPagingFlow: Flow<PagingData<Photo>>, disabledCache: Boolean = false) {
    val items = photoPagingFlow.collectAsLazyPagingItems()
    LazyVerticalGrid(Fixed(3)) {
        itemsIndexed(items) { index, photo ->
            photo?.let { PhotoContent(index, it, disabledCache) }
        }
    }
}

@Composable
fun PhotoContent(index: Int, photo: Photo, disabledCache: Boolean = false) {
    val itemSizeDp = LocalContext.current.getScreenWidth().px2dp / 3
    val resources = LocalContext.current.resources
    AsyncImage(
        imageUri = photo.firstThumbnailUrl,
        modifier = Modifier.size(itemSizeDp.dp, itemSizeDp.dp),
        contentScale = ContentScale.Crop,
        contentDescription = ""
    ) {
        placeholder(IconStateImage(drawable.ic_image_outline, ResColor(color.placeholder_bg)))
        error(IconStateImage(drawable.ic_error, ResColor(color.placeholder_bg)))
        crossfade()
        if (disabledCache) {
            downloadDiskCachePolicy(DISABLED)
            bitmapResultDiskCachePolicy(DISABLED)
            bitmapMemoryCachePolicy(DISABLED)
        }
    }
}