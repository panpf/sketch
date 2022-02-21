package com.github.panpf.sketch.compose.sample.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.viewModels
import com.github.panpf.sketch.compose.sample.base.ToolbarFragment
import com.github.panpf.sketch.compose.sample.vm.GiphyGifListViewModel

class GiphyGifListFragment : ToolbarFragment() {

    private val viewModel by viewModels<GiphyGifListViewModel>()

    @OptIn(ExperimentalFoundationApi::class)
    override fun createView(inflater: LayoutInflater, parent: ViewGroup?): View =
        ComposeView(requireContext()).apply {
            setContent {
                PhotoListContent(viewModel.pagingFlow)
            }
        }
}