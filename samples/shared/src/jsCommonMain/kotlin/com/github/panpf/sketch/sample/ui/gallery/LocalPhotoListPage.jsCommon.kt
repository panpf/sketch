package com.github.panpf.sketch.sample.ui.gallery

import androidx.compose.runtime.Composable
import com.github.panpf.sketch.sample.ui.LocalNavBackStack
import com.github.panpf.sketch.sample.ui.PhotoPagerRoute
import com.github.panpf.sketch.sample.ui.gridCellsMinSize
import org.koin.compose.viewmodel.koinViewModel

actual fun localPhotoListPermission(): Any? = null

@Composable
actual fun LocalPhotoListPage() {
    val navBackStack = LocalNavBackStack.current
    val localPhotoListViewModel: LocalPhotoListViewModel = koinViewModel()
    PagingPhotoList(
        photoPagingFlow = localPhotoListViewModel.pagingFlow,
        animatedPlaceholder = false,
        gridCellsMinSize = gridCellsMinSize,
        onClick = { photos, _, index ->
            val params = buildPhotoPagerParams(photos, index)
            navBackStack.add(PhotoPagerRoute(params))
        }
    )
}