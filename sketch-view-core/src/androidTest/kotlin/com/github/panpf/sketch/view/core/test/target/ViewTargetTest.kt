/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
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

package com.github.panpf.sketch.view.core.test.target

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.asSketchImage
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.RequestContext
import com.github.panpf.sketch.request.internal.RequestManager
import com.github.panpf.sketch.request.internal.requestManager
import com.github.panpf.sketch.target.ViewTarget
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import org.junit.runner.RunWith
import kotlin.test.Test

@RunWith(AndroidJUnit4::class)
class ViewTargetTest {

    @Test
    fun test() {
        val (context, sketch) = getTestContextAndSketch()
        val requestContext = RequestContext(sketch, ImageRequest(context, null))
        TestImageViewTarget(ImageView(context)).apply {
            onStart(requestContext, null)
            onError(requestContext, null)
            onSuccess(requestContext, ColorDrawable(Color.RED).asSketchImage())
        }
    }

    class TestImageViewTarget(override val view: ImageView) : ViewTarget<ImageView> {

        override var drawable: Drawable?
            get() = view.drawable
            set(value) {
                view.setImageDrawable(value)
            }

        override fun getRequestManager(): RequestManager {
            return view.requestManager
        }
    }
}