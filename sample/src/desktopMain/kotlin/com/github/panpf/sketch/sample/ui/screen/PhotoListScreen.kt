package com.github.panpf.sketch.sample.ui.screen

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.github.panpf.sketch.sample.ui.model.Photo
import com.github.panpf.sketch.sample.ui.page.PhotoListPage
import com.github.panpf.sketch.sample.ui.page.buildPhotoPagerParams

object PhotoListScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current!!
        PhotoListPage { items: List<Photo>, position: Int ->
            val params = buildPhotoPagerParams(items, position)
            navigator.push(PhotoPagerScreen(params))
        }
    }
}