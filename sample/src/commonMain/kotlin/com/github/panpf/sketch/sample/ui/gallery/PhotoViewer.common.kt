package com.github.panpf.sketch.sample.ui.gallery

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Color
import com.github.panpf.sketch.sample.ui.model.Photo

@Composable
expect fun PhotoViewer(
    photo: Photo,
    buttonBgColorState: MutableState<Color>,
)