package com.github.panpf.sketch.sample.ui.gif.giphy.compose

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.viewModels
import com.github.panpf.sketch.sample.ui.base.ToolbarFragment
import com.github.panpf.sketch.sample.ui.gif.giphy.GiphyGifListViewModel
import com.github.panpf.sketch.sample.ui.photo.pexels.compose.PhotoListContent

class GiphyGifListComposeFragment : ToolbarFragment() {

    private val viewModel by viewModels<GiphyGifListViewModel>()

    @OptIn(ExperimentalFoundationApi::class)
    override fun createView(toolbar: Toolbar, inflater: LayoutInflater, parent: ViewGroup?): View {
        toolbar.title = "GIF On Compose"
        return ComposeView(requireContext()).apply {
            setContent {
                PhotoListContent(viewModel.pagingFlow)
            }
        }
    }
}