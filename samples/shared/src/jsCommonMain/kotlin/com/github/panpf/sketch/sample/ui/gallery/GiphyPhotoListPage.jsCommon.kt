package com.github.panpf.sketch.sample.ui.gallery

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import com.github.panpf.sketch.sample.data.api.giphy.GiphyGif
import com.github.panpf.sketch.sample.ui.model.Photo
import org.koin.compose.viewmodel.koinViewModel

@Composable
actual fun GiphyPhotoListPage() {
    val navigator = LocalNavigator.current!!
    val giphyPhotoListViewModel: GiphyPhotoListViewModel = koinViewModel()
    PhotoList(
        photoPaging = giphyPhotoListViewModel.photoPaging,
        modifier = Modifier.fillMaxSize(),
        animatedPlaceholder = true,
        gridCellsMinSize = 150.dp,
        onClick = { photos1, _, index ->
            val params = buildPhotoPagerParams(photos1, index)
            navigator.push(PhotoPagerScreen(params))
        }
    )
}

private fun GiphyGif.toPhoto(): Photo = Photo(
    originalUrl = images.original.downloadUrl,
    mediumUrl = images.original.downloadUrl,
    thumbnailUrl = images.fixedWidth.downloadUrl,
    width = images.original.width.toInt(),
    height = images.original.height.toInt(),
)