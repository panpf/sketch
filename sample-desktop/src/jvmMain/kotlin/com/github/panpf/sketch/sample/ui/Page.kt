package com.github.panpf.sketch.sample.ui

import androidx.compose.runtime.Composable
import com.github.panpf.sketch.sample.ui.navigation.Navigation
import com.github.panpf.sketch.sample.ui.screen.GalleryScreen
import com.github.panpf.sketch.sample.ui.screen.MainScreen

sealed interface Page {

    @Composable
    fun content(navigation: Navigation, index: Int)

    data object Main : Page {

        @Composable
        override fun content(navigation: Navigation, index: Int) {
            MainScreen(navigation)
        }
    }

    data object Gallery : Page {

        @Composable
        override fun content(navigation: Navigation, index: Int) {
            GalleryScreen(navigation)
        }
    }

//    data class Slideshow(val imageResources: List<ImageResource>, val currentIndex: Int) : Page {
//
//        @Composable
//        override fun content(navigation: Navigation, index: Int) {
//            SlideshowScreen(navigation, imageResources, currentIndex)
//        }
//    }
}