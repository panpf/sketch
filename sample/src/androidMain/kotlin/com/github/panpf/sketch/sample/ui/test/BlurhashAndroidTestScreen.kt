/*
 * Copyright (C) 2024 panpf <panpf@outlook.com>
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

import android.widget.LinearLayout
import android.widget.TextView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.github.panpf.sketch.loadImage
import com.github.panpf.sketch.sample.ui.base.BaseScreen
import com.github.panpf.sketch.sample.ui.base.ToolbarScaffold
import com.github.panpf.sketch.sample.ui.widget.MyImageView
import com.github.panpf.sketch.state.BlurhashStateImage
import com.github.panpf.sketch.util.Size

class BlurhashAndroidTestScreen : BaseScreen() {

    @Composable
    override fun DrawContent() {
        ToolbarScaffold(title = "Blurhash Android View") {
            val context = LocalContext.current

            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    LinearLayout(ctx).apply {
                        orientation = LinearLayout.VERTICAL
                        setPadding(48, 48, 48, 48)

                        // Example blurhash strings
                        val blurhash1 = "L6PZfSi_.AyE_3t7t7R**0o#DgR4"
                        val blurhash2 = "UEHLh[WB2yk8pyoJadR*.7kCMdnjS#M|%1%2"
                        val blurhash3 = "oKN]Rv%2Tw=wR6cE]~RBVZRip0W9};RPxuwH%3s8tLOtxZ%gixtQI.ENa0NZIVt6%1j^M_bcRPX9"
                        val blurhash4 = "LGF5]+Yk^6#M@-5c,1J5@[or[Q6."

                        // Title
                        addView(TextView(ctx).apply {
                            text = "Blurhash Android View Examples"
                            textSize = 20f
                            setPadding(0, 0, 0, 48)
                        })

                        // Blurhash URI example
//                        addView(TextView(ctx).apply {
//                            text = "Blurhash URI with ImageView"
//                            textSize = 16f
//                            setPadding(0, 0, 0, 24)
//                        })
//
//                        addView(MyImageView(ctx).apply {
//                            layoutParams = LinearLayout.LayoutParams(400, 400).apply {
//                                setMargins(0, 0, 0, 48)
//                            }
//                            loadImage(newBlurhashUri(blurhash1))
//                        })

                        // Blurhash as placeholder
                        addView(TextView(ctx).apply {
                            text = "Blurhash as Placeholder"
                            textSize = 16f
                            setPadding(0, 0, 0, 24)
                        })

                        addView(MyImageView(ctx).apply {
                            layoutParams = LinearLayout.LayoutParams(400, 400).apply {
                                setMargins(0, 0, 0, 48)
                            }
                            loadImage("https://httpbin.org/delay/3") {
                                placeholder(BlurhashStateImage(blurhash2, Size(100, 100)))
                            }
                        })

                        // Blurhash as error state
                        addView(TextView(ctx).apply {
                            text = "Blurhash as Error State"
                            textSize = 16f
                            setPadding(0, 0, 0, 24)
                        })

                        addView(MyImageView(ctx).apply {
                            layoutParams = LinearLayout.LayoutParams(400, 400).apply {
                                setMargins(0, 0, 0, 48)
                            }
                            loadImage("invalid_url") {
                                fallback(BlurhashStateImage(blurhash3, Size(100, 100)))
                            }
                        })

                        // Different variations
//                        addView(TextView(ctx).apply {
//                            text = "Different Blurhash Variations"
//                            textSize = 16f
//                            setPadding(0, 0, 0, 24)
//                        })
//
//                        listOf(blurhash1, blurhash2, blurhash3, blurhash4).forEach { blurhash ->
//                            addView(MyImageView(ctx).apply {
//                                layoutParams = LinearLayout.LayoutParams(300, 300).apply {
//                                    setMargins(0, 0, 0, 24)
//                                }
//                                loadImage(newBlurhashUri(blurhash))
//                            })
//                        }
                    }
                }
            )
        }
    }
}