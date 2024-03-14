package com.github.panpf.sketch.sample.ui.gallery

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.panpf.sketch.sample.ui.base.BaseScreen

class PhotoPagerScreen(private val params: PhotoPagerParams) : BaseScreen() {

    @Composable
    override fun DrawContent() {
        Box(Modifier.fillMaxSize()) {
            PhotoPager(
                photos = params.photos,
                initialPosition = params.initialPosition,
                startPosition = params.startPosition,
            )
        }
    }
}