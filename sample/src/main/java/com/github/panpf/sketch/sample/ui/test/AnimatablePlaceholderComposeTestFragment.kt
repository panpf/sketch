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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.transition.TransitionInflater
import com.github.panpf.sketch.cache.CachePolicy
import com.github.panpf.sketch.compose.AsyncImage
import com.github.panpf.sketch.decode.BitmapDecodeInterceptor
import com.github.panpf.sketch.decode.BitmapDecodeResult
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.sample.AssetImages
import com.github.panpf.sketch.sample.R
import com.github.panpf.sketch.sample.R.drawable
import com.github.panpf.sketch.sample.databinding.AnimatablePlaceholderComposeTestFragmentBinding
import com.github.panpf.sketch.sample.ui.base.ToolbarBindingFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow

class AnimatablePlaceholderComposeTestFragment :
    ToolbarBindingFragment<AnimatablePlaceholderComposeTestFragmentBinding>() {

    private val urlIndexFlow = MutableStateFlow(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            sharedElementEnterTransition = TransitionInflater.from(requireContext())
                .inflateTransition(R.transition.my_move)
        }
    }

    override fun onViewCreated(
        toolbar: androidx.appcompat.widget.Toolbar,
        binding: AnimatablePlaceholderComposeTestFragmentBinding,
        savedInstanceState: Bundle?
    ) {
        toolbar.title = "AnimatablePlaceholder (Compose)"

        binding.animatablePlaceholderComposeTestCompose.setContent {
            // todo AnimatedVectorDrawable 和 AnimatedVectorDrawableCompat 在 api 29 以上无法播放

            val urlIndexState = urlIndexFlow.collectAsState()
            val uriString = AssetImages.STATICS[urlIndexState.value % AssetImages.STATICS.size]
            AsyncImage(
                request = DisplayRequest(LocalContext.current, uriString) {
                    memoryCachePolicy(CachePolicy.DISABLED)
                    resultCachePolicy(CachePolicy.DISABLED)
                    placeholder(drawable.ic_placeholder_eclipse_animated)
                    components {
                        addBitmapDecodeInterceptor(object : BitmapDecodeInterceptor {
                            override val key: String?
                                get() = null
                            override val sortWeight: Int
                                get() = 0

                            override suspend fun intercept(chain: BitmapDecodeInterceptor.Chain): Result<BitmapDecodeResult> {
                                delay(5000)
                                return chain.proceed()
                            }
                        })
                    }
                },
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