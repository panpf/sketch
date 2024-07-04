package com.github.panpf.sketch.sample.ui.gallery

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.github.panpf.sketch.sample.image.palette.PhotoPalette
import com.github.panpf.sketch.sample.ui.model.Photo

@Composable
expect fun PhotoViewer(
    photo: Photo,
    photoPaletteState: MutableState<PhotoPalette>,
)