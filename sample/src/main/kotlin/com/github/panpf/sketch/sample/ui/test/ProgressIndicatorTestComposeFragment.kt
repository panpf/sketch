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
package com.github.panpf.sketch.sample.ui.test

import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import androidx.appcompat.widget.Toolbar
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.transition.TransitionInflater
import com.github.panpf.sketch.drawable.MaskProgressDrawable
import com.github.panpf.sketch.drawable.RingProgressDrawable
import com.github.panpf.sketch.drawable.SectorProgressDrawable
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.sample.ui.base.ToolbarFragment
import com.github.panpf.sketch.sample.ui.photo.pexels.progressIndicator
import com.github.panpf.sketch.sample.ui.photo.pexels.rememberDrawableProgressPainter
import com.github.panpf.sketch.sample.ui.photo.pexels.rememberProgressIndicatorState
import com.google.accompanist.drawablepainter.DrawablePainter
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.random.Random

class ProgressIndicatorTestComposeFragment : ToolbarFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            sharedElementEnterTransition = TransitionInflater.from(requireContext())
                .inflateTransition(R.transition.my_move)
        }
    }


    override fun createView(toolbar: Toolbar, inflater: LayoutInflater, parent: ViewGroup?): View {
        toolbar.title = "ProgressIndicator（Compose）"

        return ComposeView(requireContext()).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            setContent {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val context = LocalContext.current
                    val placeholderPainter = remember() {
                        val drawable = context.resources.getDrawable(R.drawable.im_placeholder)
                        DrawablePainter(drawable)
                    }

                    val maskProgressIndicatorState = rememberProgressIndicatorState(
                        rememberDrawableProgressPainter(MaskProgressDrawable())
                    )
                    val sectorProgressIndicatorState = rememberProgressIndicatorState(
                        rememberDrawableProgressPainter(SectorProgressDrawable())
                    )
                    val ringProgressIndicatorState = rememberProgressIndicatorState(
                        rememberDrawableProgressPainter(RingProgressDrawable())
                    )

                    Text(text = "MaskProgressIndicator", color = Color.White)
                    Spacer(modifier = Modifier.size(4.dp))
                    Image(
                        painter = placeholderPainter,
                        contentDescription = "Image",
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .progressIndicator(maskProgressIndicatorState)
                    )

                    Spacer(modifier = Modifier.size(12.dp))

                    Text(text = "SectorProgressIndicator", color = Color.White)
                    Spacer(modifier = Modifier.size(4.dp))
                    Image(
                        painter = placeholderPainter,
                        contentDescription = "Image",
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .progressIndicator(sectorProgressIndicatorState)
                    )

                    Spacer(modifier = Modifier.size(12.dp))

                    Text(text = "RingProgressIndicator", color = Color.White)
                    Spacer(modifier = Modifier.size(4.dp))
                    Image(
                        painter = placeholderPainter,
                        contentDescription = "Image",
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .progressIndicator(ringProgressIndicatorState)
                    )

                    LaunchedEffect(Unit) {
                        var progress = 0L
                        ringProgressIndicatorState.progress = null
                        while (progress <= 100 && isActive) {
                            delay(Random.nextLong(500, 2000))
                            progress += 20
                            maskProgressIndicatorState.progress = progress / 100f
                            sectorProgressIndicatorState.progress = progress / 100f
                            ringProgressIndicatorState.progress = progress / 100f
                        }
                    }
                }
            }
        }
    }
}