package com.github.panpf.sketch.sample.ui.gallery

import androidx.compose.runtime.Composable
import com.github.panpf.sketch.sample.ui.LocalNavBackStack
import com.github.panpf.sketch.sample.ui.PhotoPagerRoute
import com.github.panpf.sketch.sample.ui.gridCellsMinSize
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun GiphyPhotoListPage() {
    val navBackStack = LocalNavBackStack.current
    val giphyPhotoListViewModel: GiphyPhotoListViewModel = koinViewModel()
    PagingPhotoList(
        photoPagingFlow = giphyPhotoListViewModel.pagingFlow,
        animatedPlaceholder = true,
        gridCellsMinSize = gridCellsMinSize,
        onClick = { photos, _, index ->
            val params = buildPhotoPagerParams(photos, index)
            navBackStack.add(PhotoPagerRoute(params))
        },
    )
}