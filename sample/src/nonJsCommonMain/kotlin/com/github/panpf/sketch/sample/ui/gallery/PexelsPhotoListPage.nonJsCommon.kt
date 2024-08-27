package com.github.panpf.sketch.sample.ui.gallery

import androidx.compose.runtime.Composable
import androidx.paging.cachedIn
import app.cash.paging.Pager
import app.cash.paging.PagingConfig
import app.cash.paging.PagingData
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.github.panpf.sketch.sample.ui.gridCellsMinSize
import com.github.panpf.sketch.sample.ui.model.Photo
import kotlinx.coroutines.flow.Flow


@Composable
actual fun PexelsPhotoListPage(screen: Screen) {
    val navigator = LocalNavigator.current!!
    val screenModel = screen.rememberScreenModel {
        PexelsPhotoListScreenModel()
    }
    PagingPhotoList(
        photoPagingFlow = screenModel.pagingFlow,
        animatedPlaceholder = false,
        gridCellsMinSize = gridCellsMinSize,
        onClick = { photos, _, index ->
            val params = buildPhotoPagerParams(photos, index)
            navigator.push(PhotoPagerScreen(params))
        },
    )
}

class PexelsPhotoListScreenModel : ScreenModel {

    val pagingFlow: Flow<PagingData<Photo>> = Pager(
        config = PagingConfig(
            pageSize = 40,
            enablePlaceholders = false,
        ),
        initialKey = 0,
        pagingSourceFactory = {
            PexelsPhotoListPagingSource()
        }
    ).flow.cachedIn(screenModelScope)
}