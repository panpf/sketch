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
package com.github.panpf.sketch.core.test.target

import android.widget.RemoteViews
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.core.test.getTestContext
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.asSketchImage
import com.github.panpf.sketch.request.internal.RequestContext
import com.github.panpf.sketch.target.RemoteViewsTarget
import com.github.panpf.sketch.util.getDrawableCompat
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RemoteViewsTargetTest {

    @Test
    fun test() {
        val context = getTestContext()
        val remoteViews =
            RemoteViews(context.packageName, android.R.layout.activity_list_item)
        val requestContext = RequestContext(ImageRequest(context, null))

        var callbackCount = 0
        RemoteViewsTarget(remoteViews, android.R.id.icon) {
            callbackCount++
        }.apply {
            Assert.assertEquals(0, callbackCount)

            onStart(requestContext, context.getDrawableCompat(android.R.drawable.bottom_bar).asSketchImage())
            Assert.assertEquals(1, callbackCount)

            onStart(requestContext, null)
            Assert.assertEquals(2, callbackCount)

            onError(requestContext, context.getDrawableCompat(android.R.drawable.bottom_bar).asSketchImage())
            Assert.assertEquals(3, callbackCount)

            onError(requestContext, null)
            Assert.assertEquals(4, callbackCount)

            onSuccess(requestContext, context.getDrawableCompat(android.R.drawable.bottom_bar).asSketchImage())
            Assert.assertEquals(5, callbackCount)
        }

        callbackCount = 0
        RemoteViewsTarget(remoteViews, android.R.id.icon, ignoreNullDrawable = true) {
            callbackCount++
        }.apply {
            Assert.assertEquals(0, callbackCount)

            onStart(requestContext, context.getDrawableCompat(android.R.drawable.bottom_bar).asSketchImage())
            Assert.assertEquals(1, callbackCount)

            onStart(requestContext, null)
            Assert.assertEquals(1, callbackCount)

            onError(requestContext, context.getDrawableCompat(android.R.drawable.bottom_bar).asSketchImage())
            Assert.assertEquals(2, callbackCount)

            onError(requestContext, null)
            Assert.assertEquals(2, callbackCount)

            onSuccess(requestContext, context.getDrawableCompat(android.R.drawable.bottom_bar).asSketchImage())
            Assert.assertEquals(3, callbackCount)
        }
    }
}