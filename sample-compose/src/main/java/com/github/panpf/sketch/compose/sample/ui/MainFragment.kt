package com.github.panpf.sketch.compose.sample.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.fragment.findNavController
import com.github.panpf.sketch.compose.sample.NavMainDirections
import com.github.panpf.sketch.compose.sample.base.ToolbarFragment
import com.github.panpf.sketch.compose.sample.bean.Link

class MainFragment : ToolbarFragment() {

    private val items = listOf(
        Link("Pexels Photos", NavMainDirections.actionGlobalPexelsPhotosFragment()),
        Link("Giphy GIF", NavMainDirections.actionGlobalGiphyGifListFragment()),
    )
    private val linkClick: ((Link) -> Unit) = {
        findNavController().navigate(it.navDirections)
    }

    @OptIn(ExperimentalFoundationApi::class)
    override fun createView(toolbar: Toolbar, inflater: LayoutInflater, parent: ViewGroup?): View =
        ComposeView(requireContext()).apply {
            setContent {
                LazyColumn(content = {
                    items(items.size) {
                        LinkContent(items[it], linkClick)
                    }
                })
            }
        }
}

@Composable
fun LinkContent(link: Link, linkClick: ((Link) -> Unit)? = null) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, top = 16.dp, end = 20.dp, bottom = 16.dp)
            .clickable {
                linkClick?.invoke(link)
            })
    {
        Text(text = link.title, modifier = Modifier.fillMaxWidth(), color = Color.White)
    }
}

@Preview
@Composable
fun LinkContentPreview() {
    LinkContent(link = Link("TestTitle", NavMainDirections.actionGlobalGiphyGifListFragment()))
}