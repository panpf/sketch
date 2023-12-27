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

import android.os.Bundle
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.compose.AsyncImage
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.resources.AssetImages
import com.github.panpf.sketch.sample.R.color
import com.github.panpf.sketch.sample.R.drawable
import com.github.panpf.sketch.sample.image.DelayBitmapDecodeInterceptor
import com.github.panpf.sketch.sample.ui.base.BaseToolbarComposeFragment
import com.github.panpf.sketch.stateimage.AnimatableIconStateImage
import kotlinx.coroutines.flow.MutableStateFlow

class AnimatablePlaceholderComposeTestFragment : BaseToolbarComposeFragment() {

    private val urlIndexFlow = MutableStateFlow(0)

    @Composable
    override fun DrawContent() {
        Column(modifier = Modifier.fillMaxSize()) {
            val urlIndexState = urlIndexFlow.collectAsState()
            val images = remember {
                arrayOf(AssetImages.jpeg.uri, AssetImages.webp.uri, AssetImages.bmp.uri)
            }
            val uriString = images[urlIndexState.value % images.size]
            val request = DisplayRequest(LocalContext.current, uriString) {
                memoryCachePolicy(CachePolicy.DISABLED)
                resultCachePolicy(CachePolicy.DISABLED)
                // TODO AnimatedVectorDrawable and AnimatedVectorDrawableCompat cannot be played above api 29
                placeholder(
                    AnimatableIconStateImage(drawable.ic_placeholder_eclipse_animated) {
                        resColorBackground(color.placeholder_bg)
                    }
                )
                components {
                    addBitmapDecodeInterceptor(DelayBitmapDecodeInterceptor(3000))
                }
            }
            Spacer(modifier = Modifier.size(20.dp))
            AsyncImage(
                request = request,
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .aspectRatio(1f)
                    .weight(1f)
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.size(16.dp))
            AsyncImage(
                request = request,
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .aspectRatio(1.5f)
                    .weight(1f)
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.size(16.dp))
            AsyncImage(
                request = request,
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .aspectRatio(0.5f)
                    .weight(1f)
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.size(40.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Button(
                    onClick = { urlIndexFlow.value = urlIndexFlow.value + 1 },
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Text(text = "Next")
                }
            }
            Spacer(modifier = Modifier.size(40.dp))
        }
    }

    override fun onViewCreated(
        toolbar: androidx.appcompat.widget.Toolbar,
        savedInstanceState: Bundle?
    ) {
        toolbar.title = "AnimatablePlaceholder (Compose)"
    }
}