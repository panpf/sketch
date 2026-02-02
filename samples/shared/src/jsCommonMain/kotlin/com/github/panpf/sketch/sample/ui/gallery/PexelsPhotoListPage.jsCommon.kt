package com.github.panpf.sketch.sample.ui.gallery

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import com.github.panpf.sketch.sample.data.api.pexels.PexelsPhoto
import com.github.panpf.sketch.sample.ui.model.Photo
import org.koin.compose.viewmodel.koinViewModel

@Composable
actual fun PexelsPhotoListPage() {
    val navigator = LocalNavigator.current!!
    val pexelsPhotoListViewModel: PexelsPhotoListViewModel = koinViewModel()
    PhotoList(
        photoPaging = pexelsPhotoListViewModel.photoPaging,
        modifier = Modifier.fillMaxSize(),
        animatedPlaceholder = false,
        gridCellsMinSize = 150.dp,
        onClick = { photos1, _, index ->
            val params = buildPhotoPagerParams(photos1, index)
            navigator.push(PhotoPagerScreen(params))
        }
    )
}

private fun PexelsPhoto.toPhoto(): Photo = Photo(
    originalUrl = src.original,
    mediumUrl = src.large,
    thumbnailUrl = src.medium,
    width = width,
    height = height,
)