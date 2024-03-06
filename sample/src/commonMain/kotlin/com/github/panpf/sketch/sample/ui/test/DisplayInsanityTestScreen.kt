package com.github.panpf.sketch.sample.ui.test

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.cachedIn
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.SingletonSketch
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.compose.LocalPlatformContext
import com.github.panpf.sketch.decode.internal.ExifOrientationHelper
import com.github.panpf.sketch.resources.AssetImages
import com.github.panpf.sketch.sample.data.paging.isIgnoreExifOrientation
import com.github.panpf.sketch.sample.data.paging.readImageInfoOrNull
import com.github.panpf.sketch.sample.ui.model.Photo
import com.github.panpf.sketch.sample.ui.gallery.PhotoGrid
import com.github.panpf.sketch.sample.ui.base.BaseScreen
import com.github.panpf.sketch.sample.ui.base.ToolbarScaffold
import com.github.panpf.sketch.util.Size

class DisplayInsanityTestScreen : BaseScreen() {

    @Composable
    override fun DrawContent() {
        ToolbarScaffold(title = "DisplayInsanityTest") {
            val context = LocalPlatformContext.current
            val sketch = SingletonSketch.get(context)
            val viewModel = rememberScreenModel { InsanityTestScreenModel(context, sketch) }
            PhotoGrid(
                photoPagingFlow = viewModel.pagingFlow,
                animatedPlaceholder = false,
                gridCellsMinSize = 100.dp,
                onClick = { _, _, _ -> },
            )
        }
    }
}

class InsanityTestScreenModel(context: PlatformContext, sketch: Sketch) : ScreenModel {
    val pagingFlow = Pager(
        config = PagingConfig(
            pageSize = 80,
            enablePlaceholders = false,
        ),
        initialKey = 0,
        pagingSourceFactory = {
            DisplayInsanityTestPagingSource(context, sketch)
        }
    ).flow.cachedIn(screenModelScope)
}

class DisplayInsanityTestPagingSource(private val context: PlatformContext, val sketch: Sketch) :
    PagingSource<Int, Photo>() {

    private val keySet = HashSet<String>()  // Compose LazyVerticalGrid does not allow a key repeat

    override fun getRefreshKey(state: PagingState<Int, Photo>): Int = 0

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> {
        val startPosition = params.key ?: 0
        val assetPhotos = if (startPosition == 0) readAssetPhotos() else emptyList()
        val photos = urisToPhotos(assetPhotos)
        return LoadResult.Page(
            photos.filter { keySet.add(it.let { "${it.originalUrl}:${it.index}" }) },
            null,
            null
        )
    }

    private fun readAssetPhotos(): List<String> = buildList {
        repeat(100) {
            addAll(AssetImages.numbers.map { it.uri })
        }
    }

    private suspend fun urisToPhotos(uris: List<String>): List<Photo> =
        uris.mapIndexed { index, uri ->
            val imageInfo = readImageInfoOrNull(
                context = context,
                sketch = sketch,
                uri = uri,
                ignoreExifOrientation = isIgnoreExifOrientation(context)
            )
            if (imageInfo != null) {
                val exifOrientationHelper = ExifOrientationHelper(imageInfo.exifOrientation)
                val imageSize = Size(imageInfo.width, imageInfo.height)
                val size = exifOrientationHelper?.applyToSize(imageSize) ?: imageSize
                Photo(
                    originalUrl = uri,
                    mediumUrl = null,
                    thumbnailUrl = null,
                    width = size.width,
                    height = size.height,
                    exifOrientation = imageInfo.exifOrientation,
                    index = index,
                )
            } else {
                Photo(
                    originalUrl = uri,
                    mediumUrl = null,
                    thumbnailUrl = null,
                    width = null,
                    height = null,
                    exifOrientation = 0,
                    index = index,
                )
            }
        }

}