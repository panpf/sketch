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
package com.github.panpf.sketch.extensions.view.test

import android.widget.ImageView
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.displayAppIconImage
import com.github.panpf.sketch.fetch.newAppIconUri
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.internal.ViewTargetRequestDelegate
import com.github.panpf.sketch.request.internal.ViewTargetRequestManager
import com.github.panpf.sketch.test.utils.TestGlobalTargetLifecycle
import com.github.panpf.sketch.view.core.R
import com.github.panpf.tools4j.reflect.ktx.getFieldValue
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SingletonImageViewExtensionsEstensionsTest {

    @Test
    fun testDisplayAppIconImage() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val imageView = ImageView(context)

        val versionCode = context.packageManager.getPackageInfo(context.packageName, 0).versionCode
        imageView.displayAppIconImage(context.packageName, versionCode) {
            lifecycle(TestGlobalTargetLifecycle)
        }
        Thread.sleep(300)
        val manager = imageView.getTag(R.id.sketch_request_manager) as ViewTargetRequestManager
        val request = manager
            .getFieldValue<ViewTargetRequestDelegate>("currentRequestDelegate")!!
            .getFieldValue<ImageRequest>("initialRequest")!!
        Assert.assertEquals(
            newAppIconUri(context.packageName, versionCode),
            request.uriString
        )
    }
}