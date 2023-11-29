/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.panpf.sketch.sample.ui.viewer.compose

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.panpf.sketch.sample.model.ImageDetail
import com.github.panpf.tools4a.display.ktx.getStatusBarHeight
import kotlinx.serialization.json.Json

class ImageViewerPagerComposeFragment : Fragment() {

    private val args by navArgs<ImageViewerPagerComposeFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val imageList = Json.decodeFromString<List<ImageDetail>>(args.imageDetailJsonArray)
        val currentItem = imageList.indexOfFirst { it.position == args.defaultPosition }
            .takeIf { it != -1 } ?: 0
        return ComposeView(requireContext()).apply {
            setBackgroundColor(
                ResourcesCompat.getColor(
                    requireContext().resources,
                    com.github.panpf.sketch.sample.R.color.windowBackground,
                    null
                )
            )
            setContent {
                ImageViewerPagerContent(imageList, currentItem) {
                    findNavController().popBackStack()
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ImageViewerPagerContent(
    imageList: List<ImageDetail>,
    currentItem: Int,
    onClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        val pagerState = rememberPagerState(initialPage = currentItem) {
            imageList.size
        }
        HorizontalPager(
            state = pagerState,
            beyondBoundsPageCount = 0,
            modifier = Modifier.fillMaxSize()
        ) { index ->
            ImageViewer(imageList[index], onClick)
        }

        ImageViewerPagerTools(pagerState, imageList.size)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ImageViewerPagerTools(pagerState: PagerState, imageCount: Int) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val toolbarTopMarginDp = remember {
        val toolbarTopMargin = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            context.getStatusBarHeight()
        } else {
            0
        }
        with(density) { toolbarTopMargin.toDp() }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = toolbarTopMarginDp)
    ) {
        PageNumber(
            number = pagerState.currentPage + 1,
            total = imageCount,
            modifier = Modifier.align(Alignment.TopEnd)
        )
    }
}

@Composable
private fun PageNumber(modifier: Modifier = Modifier, number: Int, total: Int) {
    val colors = MaterialTheme.colors
    Text(
        text = "${number}\n·\n${total}",
        textAlign = TextAlign.Center,
        color = colors.onSecondary,
        style = TextStyle(lineHeight = 12.sp),
        modifier = Modifier
            .padding(20.dp) // margin
            .background(
                color = colors.secondary.copy(alpha = 0.7f),
                shape = RoundedCornerShape(50)
            )
            .padding(horizontal = 8.dp, vertical = 12.dp)
            .then(modifier)
    )
}

@Preview
@Composable
private fun PageNumberPreview() {
    PageNumber(number = 9, total = 22)
}
