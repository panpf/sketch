package com.github.panpf.sketch.sample.ui.screen

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import com.github.panpf.sketch.sample.ui.navigation.Navigation

@Composable
@Preview
fun MainScreen(navigation: Navigation) {
    GalleryScreen(navigation)
}