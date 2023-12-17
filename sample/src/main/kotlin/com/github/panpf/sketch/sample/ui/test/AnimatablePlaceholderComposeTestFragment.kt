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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.compose.AsyncImage
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.sample.AssetImages
import com.github.panpf.sketch.sample.R.drawable
import com.github.panpf.sketch.sample.databinding.AnimatablePlaceholderComposeTestFragmentBinding
import com.github.panpf.sketch.sample.image.DelayBitmapDecodeInterceptor
import com.github.panpf.sketch.sample.ui.base.BaseToolbarBindingFragment
import kotlinx.coroutines.flow.MutableStateFlow

class AnimatablePlaceholderComposeTestFragment :
    BaseToolbarBindingFragment<AnimatablePlaceholderComposeTestFragmentBinding>() {

    private val urlIndexFlow = MutableStateFlow(0)

    override fun onViewCreated(
        toolbar: androidx.appcompat.widget.Toolbar,
        binding: AnimatablePlaceholderComposeTestFragmentBinding,
        savedInstanceState: Bundle?
    ) {
        toolbar.title = "AnimatablePlaceholder (Compose)"

        binding.animatablePlaceholderComposeTestCompose.setContent {
            // todo AnimatedVectorDrawable and AnimatedVectorDrawableCompat cannot be played above api 29

            val urlIndexState = urlIndexFlow.collectAsState()
            val uriString = AssetImages.STATICS[urlIndexState.value % AssetImages.STATICS.size]
            val request = DisplayRequest(LocalContext.current, uriString) {
                memoryCachePolicy(CachePolicy.DISABLED)
                resultCachePolicy(CachePolicy.DISABLED)
                placeholder(drawable.ic_placeholder_eclipse_animated)
                components {
                    addBitmapDecodeInterceptor(DelayBitmapDecodeInterceptor(3000))
                }
            }
            AsyncImage(
                request = request,
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

//            val context = LocalContext.current
//            val painter = remember {
////                val resources = context.resources
////                val drawable = ResourcesCompat
////                    .getDrawable(resources, R.drawable.ic_placeholder_eclipse_animated, null)
//                val drawable = AnimatedVectorDrawableCompat
//                    .create(context, R.drawable.ic_placeholder_eclipse_animated)
//                DrawablePainter(drawable!!)
//            }
//            Image(
//                painter = painter,
//                contentDescription = "",
//                contentScale = ContentScale.Crop,
//                modifier = Modifier.fillMaxSize()
//            )
        }

        binding.animatablePlaceholderComposeTestButton.setOnClickListener {
            urlIndexFlow.value = urlIndexFlow.value + 1
        }
    }
}