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
package com.github.panpf.sketch.extensions.view.test.viewability

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.SketchImageView
import com.github.panpf.sketch.ability.isShowMimeTypeLogo
import com.github.panpf.sketch.ability.removeMimeTypeLogo
import com.github.panpf.sketch.ability.showMimeTypeLogoWithDrawable
import com.github.panpf.sketch.ability.showMimeTypeLogoWithRes
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MimeTypeLogoAbilityTest {

    @Test
    fun testExtensions() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val imageView = SketchImageView(context)

        Assert.assertFalse(imageView.isShowMimeTypeLogo)

        imageView.showMimeTypeLogoWithDrawable(mapOf("image/jpeg" to ColorDrawable(Color.BLUE)))
        Assert.assertTrue(imageView.isShowMimeTypeLogo)

        imageView.removeMimeTypeLogo()
        Assert.assertFalse(imageView.isShowMimeTypeLogo)

        imageView.showMimeTypeLogoWithRes(mapOf("image/jpeg" to android.R.drawable.btn_dialog))
        Assert.assertTrue(imageView.isShowMimeTypeLogo)
    }
}