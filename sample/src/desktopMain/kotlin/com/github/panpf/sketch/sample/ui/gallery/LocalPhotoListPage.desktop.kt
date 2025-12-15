package com.github.panpf.sketch.sample.ui.gallery

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.LocalNavigator
import com.github.panpf.sketch.sample.AppSettings
import com.github.panpf.sketch.sample.ui.gridCellsMinSize
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

actual fun localPhotoListPermission(): Any? = null

@Composable
actual fun LocalPhotoListPage() {
    val navigator = LocalNavigator.current!!
    val appSettings: AppSettings = koinInject()
    val localPhotoListViewModel: LocalPhotoListViewModel = koinViewModel()
    PagingPhotoList(
        photoPagingFlow = localPhotoListViewModel.pagingFlow,
        animatedPlaceholder = false,
        gridCellsMinSize = gridCellsMinSize,
        refreshWhen = appSettings.localPhotosDirPath,
        onClick = { photos, _, index ->
            val params = buildPhotoPagerParams(photos, index)
            navigator.push(PhotoPagerScreen(params))
        },
    )
}