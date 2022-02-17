package com.github.panpf.sketch.sample.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.fragment.app.viewModels
import androidx.paging.compose.collectAsLazyPagingItems
import com.github.panpf.sketch.cache.CachePolicy.DISABLED
import com.github.panpf.sketch.sample.base.ToolbarFragment
import com.github.panpf.sketch.sample.bean.Photo
import com.github.panpf.sketch.sample.compose.AsyncImage
import com.github.panpf.sketch.sample.compose.R
import com.github.panpf.sketch.sample.vm.PexelsImageListViewModel
import com.github.panpf.tools4a.dimen.ktx.px2dp
import com.github.panpf.tools4a.display.ktx.getScreenWidth

class PexelsPhotosFragment : ToolbarFragment() {

    private val pexelsImageListViewModel by viewModels<PexelsImageListViewModel>()

    @OptIn(ExperimentalFoundationApi::class)
    override fun createView(inflater: LayoutInflater, parent: ViewGroup?): View =
        ComposeView(requireContext()).apply {
            setContent {
                PhotoListContent(pexelsImageListViewModel)
            }
        }
}

@ExperimentalFoundationApi
@Composable
fun PhotoListContent(viewModel: PexelsImageListViewModel) {
    val items = viewModel.pagingFlow.collectAsLazyPagingItems()
    LazyVerticalGrid(GridCells.Fixed(3)) {
        itemsIndexed(items) { index, photo ->
            photo?.let { PhotoContent(index, it) }
        }
    }
}

@Composable
fun PhotoContent(index: Int, photo: Photo) {
    val itemSizeDp = LocalContext.current.getScreenWidth().px2dp / 3
    AsyncImage(
        imageUri = photo.firstThumbnailUrl,
        modifier = Modifier.size(itemSizeDp.dp, itemSizeDp.dp),
        contentDescription = ""
    ) {
        placeholderImage(R.drawable.im_placeholder)
        errorImage(R.drawable.im_error)
        bitmapMemoryCachePolicy(DISABLED)
        bitmapResultDiskCachePolicy(DISABLED)
        networkContentDiskCachePolicy(DISABLED)
    }
//    Image(
//        painter = painterResource(id = mipmap.ic_launcher),
//        contentDescription = "",
//        modifier = Modifier.fillMaxSize()
//    )
}