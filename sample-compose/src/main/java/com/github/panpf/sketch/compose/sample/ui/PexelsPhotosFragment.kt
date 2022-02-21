package com.github.panpf.sketch.compose.sample.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.github.panpf.sketch.compose.AsyncImage
import com.github.panpf.sketch.compose.sample.R
import com.github.panpf.sketch.compose.sample.R.drawable
import com.github.panpf.sketch.compose.sample.base.ToolbarFragment
import com.github.panpf.sketch.compose.sample.bean.Photo
import com.github.panpf.sketch.compose.sample.vm.PexelsImageListViewModel
import com.github.panpf.sketch.stateimage.IconResStateImage
import com.github.panpf.tools4a.dimen.ktx.px2dp
import com.github.panpf.tools4a.display.ktx.getScreenWidth
import kotlinx.coroutines.flow.Flow

class PexelsPhotosFragment : ToolbarFragment() {

    private val viewModel by viewModels<PexelsImageListViewModel>()

    @OptIn(ExperimentalFoundationApi::class)
    override fun createView(inflater: LayoutInflater, parent: ViewGroup?): View =
        ComposeView(requireContext()).apply {
            setContent {
                PhotoListContent(viewModel.pagingFlow)
            }
        }
}

@ExperimentalFoundationApi
@Composable
fun PhotoListContent(photoPagingFlow: Flow<PagingData<Photo>>) {
    val items = photoPagingFlow.collectAsLazyPagingItems()
    LazyVerticalGrid(GridCells.Fixed(3)) {
        itemsIndexed(items) { index, photo ->
            photo?.let { PhotoContent(index, it) }
        }
    }
}

@Composable
fun PhotoContent(index: Int, photo: Photo) {
    val itemSizeDp = LocalContext.current.getScreenWidth().px2dp / 3
    val resources = LocalContext.current.resources
    AsyncImage(
        imageUri = photo.firstThumbnailUrl,
        modifier = Modifier.size(itemSizeDp.dp, itemSizeDp.dp),
        contentScale = ContentScale.Crop,
        contentDescription = ""
    ) {
        placeholderImage(
            IconResStateImage(
                drawable.ic_image_outline,
                ResourcesCompat.getColor(resources, R.color.placeholder_bg, null)
            )
        )
        errorImage(
            IconResStateImage(
                drawable.ic_error,
                ResourcesCompat.getColor(resources, R.color.placeholder_bg, null)
            )
        )
        crossfadeTransition()
    }
}