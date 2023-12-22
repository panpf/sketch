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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.compose.AsyncImage
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.resources.AssetImages
import com.github.panpf.sketch.sample.R.drawable
import com.github.panpf.sketch.sample.image.DelayBitmapDecodeInterceptor
import com.github.panpf.sketch.sample.ui.base.BaseToolbarComposeFragment
import kotlinx.coroutines.flow.MutableStateFlow

class AnimatablePlaceholderComposeTestFragment : BaseToolbarComposeFragment() {

    private val urlIndexFlow = MutableStateFlow(0)

    @Composable
    override fun DrawContent() {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(2f)
            ) {
                val urlIndexState = urlIndexFlow.collectAsState()
                val uriString = AssetImages.statics[urlIndexState.value % AssetImages.statics.size]
                val request = DisplayRequest(LocalContext.current, uriString) {
                    memoryCachePolicy(CachePolicy.DISABLED)
                    resultCachePolicy(CachePolicy.DISABLED)
                    // TODO AnimatedVectorDrawable and AnimatedVectorDrawableCompat cannot be played above api 29
                    placeholder(drawable.ic_placeholder_eclipse_animated)
                    components {
                        addBitmapDecodeInterceptor(DelayBitmapDecodeInterceptor(3000))
                    }
                }
                AsyncImage(
                    request = request,
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(200.dp)
                        .align(Alignment.Center)
                )
            }
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
        }
    }

    override fun onViewCreated(
        toolbar: androidx.appcompat.widget.Toolbar,
        savedInstanceState: Bundle?
    ) {
        toolbar.title = "AnimatablePlaceholder (Compose)"
    }
}