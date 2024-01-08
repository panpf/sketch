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
package com.github.panpf.sketch.gif.test.decode.internal

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.decode.internal.isAnimatedHeif
import com.github.panpf.sketch.decode.internal.isAnimatedWebP
import com.github.panpf.sketch.decode.internal.isGif
import com.github.panpf.sketch.decode.internal.isHeif
import com.github.panpf.sketch.decode.internal.isWebP
import com.github.panpf.sketch.test.utils.getTestContext
import com.github.panpf.sketch.resources.AssetImages
import com.github.panpf.sketch.util.Bytes
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GifDecodeUtilsTest {

    @Test
    fun testIsWebP() {
        val context = getTestContext()

        Bytes(context.assets.open(AssetImages.webp.fileName).use {
            ByteArray(1024).apply { it.read(this) }
        }).apply {
            Assert.assertTrue(isWebP())
        }
        Bytes(context.assets.open(AssetImages.animWebp.fileName).use {
            ByteArray(1024).apply { it.read(this) }
        }).apply {
            Assert.assertTrue(isWebP())
        }

        Bytes(context.assets.open(AssetImages.webp.fileName).use {
            ByteArray(1024).apply { it.read(this) }.apply {
                set(8, 'V'.code.toByte())
            }
        }).apply {
            Assert.assertFalse(isWebP())
        }
        Bytes(context.assets.open(AssetImages.jpeg.fileName).use {
            ByteArray(1024).apply { it.read(this) }
        }).apply {
            Assert.assertFalse(isWebP())
        }
    }

    @Test
    fun testIsAnimatedWebP() {
        val context = getTestContext()

        Bytes(context.assets.open(AssetImages.animWebp.fileName).use {
            ByteArray(1024).apply { it.read(this) }
        }).apply {
            Assert.assertTrue(isAnimatedWebP())
        }

        // test_error_webp_anim.webp is not animated webp, must use the RiffAnimChunk function to judge
        Bytes(context.assets.open(AssetImages.webp.fileName).use {
            ByteArray(1024).apply { it.read(this) }
        }).apply {
            Assert.assertFalse(isAnimatedWebP())
        }

        Bytes(context.assets.open(AssetImages.animWebp.fileName).use {
            ByteArray(1024).apply { it.read(this) }.apply {
                set(12, 'X'.code.toByte())
            }
        }).apply {
            Assert.assertFalse(isAnimatedWebP())
        }
        Bytes(context.assets.open(AssetImages.animWebp.fileName).use {
            ByteArray(1024).apply { it.read(this) }.apply {
                set(16, 0)
            }
        }).apply {
            Assert.assertFalse(isAnimatedWebP())
        }
        Bytes(context.assets.open(AssetImages.webp.fileName).use {
            ByteArray(1024).apply { it.read(this) }
        }).apply {
            Assert.assertFalse(isAnimatedWebP())
        }
        Bytes(context.assets.open(AssetImages.jpeg.fileName).use {
            ByteArray(1024).apply { it.read(this) }
        }).apply {
            Assert.assertFalse(isAnimatedWebP())
        }
    }

    @Test
    fun testIsHeif() {
        val context = getTestContext()

        Bytes(context.assets.open(AssetImages.heic.fileName).use {
            ByteArray(1024).apply { it.read(this) }
        }).apply {
            Assert.assertTrue(isHeif())
        }

        Bytes(context.assets.open(AssetImages.animWebp.fileName).use {
            ByteArray(1024).apply { it.read(this) }
        }).apply {
            Assert.assertFalse(isHeif())
        }
        Bytes(context.assets.open(AssetImages.jpeg.fileName).use {
            ByteArray(1024).apply { it.read(this) }
        }).apply {
            Assert.assertFalse(isHeif())
        }
    }

    @Test
    fun testIsAnimatedHeif() {
        val context = getTestContext()

        Bytes(context.assets.open(AssetImages.animHeif.fileName).use {
            ByteArray(1024).apply { it.read(this) }
        }).apply {
            Assert.assertTrue(isAnimatedHeif())
        }
        Bytes(context.assets.open(AssetImages.animHeif.fileName).use {
            ByteArray(1024).apply { it.read(this) }.apply {
                set(8, 'h'.code.toByte())
                set(9, 'e'.code.toByte())
                set(10, 'v'.code.toByte())
                set(11, 'c'.code.toByte())
            }
        }).apply {
            Assert.assertTrue(isAnimatedHeif())
        }
        Bytes(context.assets.open(AssetImages.animHeif.fileName).use {
            ByteArray(1024).apply { it.read(this) }.apply {
                set(8, 'h'.code.toByte())
                set(9, 'e'.code.toByte())
                set(10, 'v'.code.toByte())
                set(11, 'x'.code.toByte())
            }
        }).apply {
            Assert.assertTrue(isAnimatedHeif())
        }

        Bytes(context.assets.open(AssetImages.heic.fileName).use {
            ByteArray(1024).apply { it.read(this) }
        }).apply {
            Assert.assertFalse(isAnimatedHeif())
        }
        Bytes(context.assets.open(AssetImages.animWebp.fileName).use {
            ByteArray(1024).apply { it.read(this) }
        }).apply {
            Assert.assertFalse(isAnimatedHeif())
        }
        Bytes(context.assets.open(AssetImages.jpeg.fileName).use {
            ByteArray(1024).apply { it.read(this) }
        }).apply {
            Assert.assertFalse(isAnimatedHeif())
        }
    }

    @Test
    fun testIsGif() {
        val context = getTestContext()

        Bytes(context.assets.open(AssetImages.animGif.fileName).use {
            ByteArray(1024).apply { it.read(this) }
        }).apply {
            Assert.assertTrue(isGif())
        }
        Bytes(context.assets.open(AssetImages.animGif.fileName).use {
            ByteArray(1024).apply { it.read(this) }.apply {
                set(4, '7'.code.toByte())
            }
        }).apply {
            Assert.assertTrue(isGif())
        }

        Bytes(context.assets.open(AssetImages.animWebp.fileName).use {
            ByteArray(1024).apply { it.read(this) }
        }).apply {
            Assert.assertFalse(isGif())
        }
    }
}