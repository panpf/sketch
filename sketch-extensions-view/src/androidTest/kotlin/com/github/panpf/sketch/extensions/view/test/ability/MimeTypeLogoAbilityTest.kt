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

package com.github.panpf.sketch.extensions.view.test.ability

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.panpf.sketch.SketchImageView
import com.github.panpf.sketch.ability.isShowMimeTypeLogo
import com.github.panpf.sketch.ability.removeMimeTypeLogo
import com.github.panpf.sketch.ability.showMimeTypeLogoWithDrawable
import com.github.panpf.sketch.ability.showMimeTypeLogoWithRes
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class MimeTypeLogoAbilityTest {

    @Test
    fun testShowMimeTypeLogoWithDrawable() {
        val context = InstrumentationRegistry.getInstrumentation().context
        val imageView = SketchImageView(context)

        assertFalse(imageView.isShowMimeTypeLogo)

        imageView.showMimeTypeLogoWithDrawable(mapOf("image/jpeg" to ColorDrawable(Color.BLUE)))
        assertTrue(imageView.isShowMimeTypeLogo)

        imageView.removeMimeTypeLogo()
        assertFalse(imageView.isShowMimeTypeLogo)

        imageView.showMimeTypeLogoWithRes(mapOf("image/jpeg" to android.R.drawable.btn_dialog))
        assertTrue(imageView.isShowMimeTypeLogo)
    }

    // TODO test
}