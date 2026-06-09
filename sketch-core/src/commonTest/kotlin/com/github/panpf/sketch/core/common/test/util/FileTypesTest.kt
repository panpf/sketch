package com.github.panpf.sketch.core.common.test.util

import com.github.panpf.sketch.images.ComposeResImageFile
import com.github.panpf.sketch.images.ComposeResImageFiles
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.IsoMediaFileTypeBox
import com.github.panpf.sketch.test.utils.fetch
import com.github.panpf.sketch.test.utils.set
import com.github.panpf.sketch.test.utils.toByteArray
import com.github.panpf.sketch.util.isAnimatedAvifFile
import com.github.panpf.sketch.util.isAnimatedHeifFile
import com.github.panpf.sketch.util.isAnimatedWebPFile
import com.github.panpf.sketch.util.isAvifFile
import com.github.panpf.sketch.util.isBmpFile
import com.github.panpf.sketch.util.isGifFile
import com.github.panpf.sketch.util.isHeifFile
import com.github.panpf.sketch.util.isJpegFile
import com.github.panpf.sketch.util.isPngFile
import com.github.panpf.sketch.util.isStaticsAvifFile
import com.github.panpf.sketch.util.isStaticsHeifFile
import com.github.panpf.sketch.util.isStaticsWebPFile
import com.github.panpf.sketch.util.isSvgFile
import com.github.panpf.sketch.util.isWebPFile
import com.github.panpf.sketch.util.readIsoMediaFileTypeBoxLength
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FileTypesTest {

    @Test
    fun testGif() = runTest {
        val animatedGifHeaderBytes = ComposeResImageFiles.animGif.readHeaderBytes()
        val animatedWebpHeaderBytes = ComposeResImageFiles.animWebp.readHeaderBytes()
        val jpegHeaderBytes = ComposeResImageFiles.jpeg.readHeaderBytes()

        assertTrue(isGifFile(animatedGifHeaderBytes))
        assertTrue(isGifFile(animatedGifHeaderBytes.copyOf().apply {
            set(4, '7'.code.toByte())
        }))
        assertFalse(isGifFile(animatedWebpHeaderBytes))
        assertFalse(isGifFile(jpegHeaderBytes))
    }

    @Test
    fun testWebP() = runTest {
        val staticsWebpHeaderBytes = ComposeResImageFiles.webp.readHeaderBytes()
        val animatedWebpHeaderBytes = ComposeResImageFiles.animWebp.readHeaderBytes()
        val animatedGifHeaderBytes = ComposeResImageFiles.animGif.readHeaderBytes()
        val jpegHeaderBytes = ComposeResImageFiles.jpeg.readHeaderBytes()

        assertTrue(isWebPFile(staticsWebpHeaderBytes))
        assertTrue(isWebPFile(animatedWebpHeaderBytes))
        assertFalse(isWebPFile(animatedGifHeaderBytes))
        assertFalse(isWebPFile(jpegHeaderBytes))
        assertFalse(isWebPFile(staticsWebpHeaderBytes.copyOf().apply {
            set(8, 'V'.code.toByte())
        }))

        assertTrue(isStaticsWebPFile(staticsWebpHeaderBytes))
        assertFalse(isStaticsWebPFile(animatedWebpHeaderBytes))
        assertFalse(isStaticsWebPFile(animatedGifHeaderBytes))
        assertFalse(isStaticsWebPFile(jpegHeaderBytes))

        assertFalse(isAnimatedWebPFile(staticsWebpHeaderBytes))
        assertTrue(isAnimatedWebPFile(animatedWebpHeaderBytes))
        assertFalse(isAnimatedWebPFile(animatedGifHeaderBytes))
        assertFalse(isAnimatedWebPFile(jpegHeaderBytes))
        assertFalse(isAnimatedWebPFile(animatedWebpHeaderBytes.copyOf().apply {
            set(12, 'X'.code.toByte())
        }))
        assertFalse(isAnimatedWebPFile(animatedWebpHeaderBytes.copyOf().apply {
            set(16, 0)
        }))
    }

    @Test
    fun testHeif() = runTest {
        val staticsHeifHeaderBytes = ComposeResImageFiles.heic.readHeaderBytes()
        assertTrue(isHeifFile(staticsHeifHeaderBytes))
        assertTrue(isStaticsHeifFile(staticsHeifHeaderBytes))
        assertFalse(isAnimatedHeifFile(staticsHeifHeaderBytes))

        val animatedHeifHeaderBytes = ComposeResImageFiles.animHeif.readHeaderBytes()
        assertTrue(isHeifFile(animatedHeifHeaderBytes))
        assertFalse(isStaticsHeifFile(animatedHeifHeaderBytes))
        assertTrue(isAnimatedHeifFile(animatedHeifHeaderBytes))

        val animatedWebpHeaderBytes = ComposeResImageFiles.animWebp.readHeaderBytes()
        assertFalse(isHeifFile(animatedWebpHeaderBytes))
        assertFalse(isStaticsHeifFile(animatedWebpHeaderBytes))
        assertFalse(isAnimatedHeifFile(animatedWebpHeaderBytes))

        val staticsAvifHeaderBytes = ComposeResImageFiles.avif.readHeaderBytes()
        assertFalse(isHeifFile(staticsAvifHeaderBytes))
        assertFalse(isStaticsHeifFile(staticsAvifHeaderBytes))
        assertFalse(isAnimatedHeifFile(staticsAvifHeaderBytes))

        val animatedAvifHeaderBytes = ComposeResImageFiles.animAvif.readHeaderBytes()
        assertFalse(isHeifFile(animatedAvifHeaderBytes))
        assertFalse(isStaticsHeifFile(animatedAvifHeaderBytes))
        assertFalse(isAnimatedHeifFile(animatedAvifHeaderBytes))

        val jpegHeaderBytes = ComposeResImageFiles.jpeg.readHeaderBytes()
        assertFalse(isHeifFile(jpegHeaderBytes))
        assertFalse(isStaticsHeifFile(jpegHeaderBytes))
        assertFalse(isAnimatedHeifFile(jpegHeaderBytes))

        IsoMediaFileTypeBox(majorBrand = "heic").apply {
            assertTrue(isHeifFile(bytes))
            assertTrue(isStaticsHeifFile(bytes))
            assertFalse(isAnimatedHeifFile(bytes))
        }
        IsoMediaFileTypeBox(majorBrand = "heix").apply {
            assertTrue(isHeifFile(bytes))
            assertTrue(isStaticsHeifFile(bytes))
            assertFalse(isAnimatedHeifFile(bytes))
        }
        IsoMediaFileTypeBox(majorBrand = "hevc").apply {
            assertTrue(isHeifFile(bytes))
            assertFalse(isStaticsHeifFile(bytes))
            assertTrue(isAnimatedHeifFile(bytes))
        }
        IsoMediaFileTypeBox(majorBrand = "hevx").apply {
            assertTrue(isHeifFile(bytes))
            assertFalse(isStaticsHeifFile(bytes))
            assertTrue(isAnimatedHeifFile(bytes))
        }

        // mif1 or msf1
        IsoMediaFileTypeBox(majorBrand = "mif1").apply {
            assertFalse(isHeifFile(bytes))
            assertFalse(isStaticsHeifFile(bytes))
            assertFalse(isAnimatedHeifFile(bytes))
        }
        IsoMediaFileTypeBox(majorBrand = "mif1", compatibleBrands = listOf("heic")).apply {
            assertTrue(isHeifFile(bytes))
            assertTrue(isStaticsHeifFile(bytes))
            assertFalse(isAnimatedHeifFile(bytes))
        }
        IsoMediaFileTypeBox(majorBrand = "mif1", compatibleBrands = listOf("heix")).apply {
            assertTrue(isHeifFile(bytes))
            assertTrue(isStaticsHeifFile(bytes))
            assertFalse(isAnimatedHeifFile(bytes))
        }
        IsoMediaFileTypeBox(majorBrand = "mif1", compatibleBrands = listOf("hevc")).apply {
            assertFalse(isHeifFile(bytes))
            assertFalse(isStaticsHeifFile(bytes))
            assertFalse(isAnimatedHeifFile(bytes))
        }
        IsoMediaFileTypeBox(majorBrand = "mif1", compatibleBrands = listOf("hevx")).apply {
            assertFalse(isHeifFile(bytes))
            assertFalse(isStaticsHeifFile(bytes))
            assertFalse(isAnimatedHeifFile(bytes))
        }

        IsoMediaFileTypeBox(majorBrand = "msf1").apply {
            assertFalse(isHeifFile(bytes))
            assertFalse(isStaticsHeifFile(bytes))
            assertFalse(isAnimatedHeifFile(bytes))
        }
        IsoMediaFileTypeBox(majorBrand = "msf1", compatibleBrands = listOf("heic")).apply {
            assertTrue(isHeifFile(bytes))
            assertTrue(isStaticsHeifFile(bytes))
            assertFalse(isAnimatedHeifFile(bytes))
        }
        IsoMediaFileTypeBox(majorBrand = "msf1", compatibleBrands = listOf("heix")).apply {
            assertTrue(isHeifFile(bytes))
            assertTrue(isStaticsHeifFile(bytes))
            assertFalse(isAnimatedHeifFile(bytes))
        }
        IsoMediaFileTypeBox(majorBrand = "msf1", compatibleBrands = listOf("hevc")).apply {
            assertTrue(isHeifFile(bytes))
            assertFalse(isStaticsHeifFile(bytes))
            assertTrue(isAnimatedHeifFile(bytes))
        }
        IsoMediaFileTypeBox(majorBrand = "msf1", compatibleBrands = listOf("hevx")).apply {
            assertTrue(isHeifFile(bytes))
            assertFalse(isStaticsHeifFile(bytes))
            assertTrue(isAnimatedHeifFile(bytes))
        }

        // test sliding equals
        IsoMediaFileTypeBox("mif1", listOf("fake", "heic")).apply {
            assertTrue(isHeifFile(bytes))
            assertTrue(isStaticsHeifFile(bytes))
            assertFalse(isAnimatedHeifFile(bytes))
        }
        IsoMediaFileTypeBox("mif1", listOf("fake", "heix")).apply {
            assertTrue(isHeifFile(bytes))
            assertTrue(isStaticsHeifFile(bytes))
            assertFalse(isAnimatedHeifFile(bytes))
        }
        IsoMediaFileTypeBox("mif1", listOf("fake", "hevc")).apply {
            assertFalse(isHeifFile(bytes))
            assertFalse(isStaticsHeifFile(bytes))
            assertFalse(isAnimatedHeifFile(bytes))
        }
        IsoMediaFileTypeBox("mif1", listOf("fake", "hevx")).apply {
            assertFalse(isHeifFile(bytes))
            assertFalse(isStaticsHeifFile(bytes))
            assertFalse(isAnimatedHeifFile(bytes))
        }

        IsoMediaFileTypeBox("msf1", listOf("fake", "heic")).apply {
            assertTrue(isHeifFile(bytes))
            assertTrue(isStaticsHeifFile(bytes))
            assertFalse(isAnimatedHeifFile(bytes))
        }
        IsoMediaFileTypeBox("msf1", listOf("fake", "heix")).apply {
            assertTrue(isHeifFile(bytes))
            assertTrue(isStaticsHeifFile(bytes))
            assertFalse(isAnimatedHeifFile(bytes))
        }
        IsoMediaFileTypeBox("msf1", listOf("fake", "hevc")).apply {
            assertTrue(isHeifFile(bytes))
            assertFalse(isStaticsHeifFile(bytes))
            assertTrue(isAnimatedHeifFile(bytes))
        }
        IsoMediaFileTypeBox("msf1", listOf("fake", "hevx")).apply {
            assertTrue(isHeifFile(bytes))
            assertFalse(isStaticsHeifFile(bytes))
            assertTrue(isAnimatedHeifFile(bytes))
        }

        // test box length
        IsoMediaFileTypeBox(majorBrand = "mif1", compatibleBrands = listOf("heic")).apply {
            assertTrue(isHeifFile(bytes))
            assertTrue(isStaticsHeifFile(bytes))
            assertFalse(isAnimatedHeifFile(bytes))

            val badBytes = bytes.copyOf().apply {
                set(16, 0.toByteArray())
            }
            val bad2Bytes = badBytes.plus("heic".encodeToByteArray())
            assertFalse(isHeifFile(badBytes))
            assertFalse(isStaticsHeifFile(badBytes))
            assertFalse(isAnimatedHeifFile(badBytes))

            assertFalse(isHeifFile(bad2Bytes))
            assertFalse(isStaticsHeifFile(bad2Bytes))
            assertFalse(isAnimatedHeifFile(bad2Bytes))
        }
        IsoMediaFileTypeBox(majorBrand = "mif1", compatibleBrands = listOf("heix")).apply {
            assertTrue(isHeifFile(bytes))
            assertTrue(isStaticsHeifFile(bytes))
            assertFalse(isAnimatedHeifFile(bytes))

            val badBytes = bytes.copyOf().apply {
                set(16, 0.toByteArray())
            }
            val bad2Bytes = badBytes.plus("heix".encodeToByteArray())
            assertFalse(isHeifFile(badBytes))
            assertFalse(isStaticsHeifFile(badBytes))
            assertFalse(isAnimatedHeifFile(badBytes))

            assertFalse(isHeifFile(bad2Bytes))
            assertFalse(isStaticsHeifFile(bad2Bytes))
            assertFalse(isAnimatedHeifFile(bad2Bytes))
        }

        IsoMediaFileTypeBox(majorBrand = "msf1", compatibleBrands = listOf("heic")).apply {
            assertTrue(isHeifFile(bytes))
            assertTrue(isStaticsHeifFile(bytes))
            assertFalse(isAnimatedHeifFile(bytes))

            val badBytes = bytes.copyOf().apply {
                set(16, 0.toByteArray())
            }
            val bad2Bytes = badBytes.plus("heic".encodeToByteArray())
            assertFalse(isHeifFile(badBytes))
            assertFalse(isStaticsHeifFile(badBytes))
            assertFalse(isAnimatedHeifFile(badBytes))

            assertFalse(isHeifFile(bad2Bytes))
            assertFalse(isStaticsHeifFile(bad2Bytes))
            assertFalse(isAnimatedHeifFile(bad2Bytes))
        }
        IsoMediaFileTypeBox(majorBrand = "msf1", compatibleBrands = listOf("heix")).apply {
            assertTrue(isHeifFile(bytes))
            assertTrue(isStaticsHeifFile(bytes))
            assertFalse(isAnimatedHeifFile(bytes))

            val badBytes = bytes.copyOf().apply {
                set(16, 0.toByteArray())
            }
            val bad2Bytes = badBytes.plus("heix".encodeToByteArray())
            assertFalse(isHeifFile(badBytes))
            assertFalse(isStaticsHeifFile(badBytes))
            assertFalse(isAnimatedHeifFile(badBytes))

            assertFalse(isHeifFile(bad2Bytes))
            assertFalse(isStaticsHeifFile(bad2Bytes))
            assertFalse(isAnimatedHeifFile(bad2Bytes))
        }
        IsoMediaFileTypeBox(majorBrand = "msf1", compatibleBrands = listOf("hevc")).apply {
            assertTrue(isHeifFile(bytes))
            assertFalse(isStaticsHeifFile(bytes))
            assertTrue(isAnimatedHeifFile(bytes))

            val badBytes = bytes.copyOf().apply {
                set(16, 0.toByteArray())
            }
            val bad2Bytes = badBytes.plus("hevc".encodeToByteArray())
            assertFalse(isHeifFile(badBytes))
            assertFalse(isStaticsHeifFile(badBytes))
            assertFalse(isAnimatedHeifFile(badBytes))

            assertFalse(isHeifFile(bad2Bytes))
            assertFalse(isStaticsHeifFile(bad2Bytes))
            assertFalse(isAnimatedHeifFile(bad2Bytes))
        }
        IsoMediaFileTypeBox(majorBrand = "msf1", compatibleBrands = listOf("hevx")).apply {
            assertTrue(isHeifFile(bytes))
            assertFalse(isStaticsHeifFile(bytes))
            assertTrue(isAnimatedHeifFile(bytes))

            val badBytes = bytes.copyOf().apply {
                set(16, 0.toByteArray())
            }
            val bad2Bytes = badBytes.plus("hevx".encodeToByteArray())
            assertFalse(isHeifFile(badBytes))
            assertFalse(isStaticsHeifFile(badBytes))
            assertFalse(isAnimatedHeifFile(badBytes))

            assertFalse(isHeifFile(bad2Bytes))
            assertFalse(isStaticsHeifFile(bad2Bytes))
            assertFalse(isAnimatedHeifFile(bad2Bytes))
        }
    }

    @Test
    fun testAvif() = runTest {
        val staticsAvifHeaderBytes = ComposeResImageFiles.avif.readHeaderBytes()
        assertTrue(isAvifFile(staticsAvifHeaderBytes))
        assertTrue(isStaticsAvifFile(staticsAvifHeaderBytes))
        assertFalse(isAnimatedAvifFile(staticsAvifHeaderBytes))

        val staticsAvif2HeaderBytes = ComposeResImageFiles.avif2.readHeaderBytes()
        assertTrue(isAvifFile(staticsAvif2HeaderBytes))
        assertTrue(isStaticsAvifFile(staticsAvif2HeaderBytes))
        assertFalse(isAnimatedAvifFile(staticsAvif2HeaderBytes))

        val animatedAvifHeaderBytes = ComposeResImageFiles.animAvif.readHeaderBytes()
        assertTrue(isAvifFile(animatedAvifHeaderBytes))
        assertFalse(isStaticsAvifFile(animatedAvifHeaderBytes))
        assertTrue(isAnimatedAvifFile(animatedAvifHeaderBytes))

        val animatedWebpHeaderBytes = ComposeResImageFiles.animWebp.readHeaderBytes()
        assertFalse(isAvifFile(animatedWebpHeaderBytes))
        assertFalse(isStaticsAvifFile(animatedWebpHeaderBytes))
        assertFalse(isAnimatedAvifFile(animatedWebpHeaderBytes))

        val staticsHeifHeaderBytes = ComposeResImageFiles.heic.readHeaderBytes()
        assertFalse(isAvifFile(staticsHeifHeaderBytes))
        assertFalse(isStaticsAvifFile(staticsHeifHeaderBytes))
        assertFalse(isAnimatedAvifFile(staticsHeifHeaderBytes))

        val animatedHeifHeaderBytes = ComposeResImageFiles.animHeif.readHeaderBytes()
        assertFalse(isAvifFile(animatedHeifHeaderBytes))
        assertFalse(isStaticsAvifFile(animatedHeifHeaderBytes))
        assertFalse(isAnimatedAvifFile(animatedHeifHeaderBytes))

        val jpegHeaderBytes = ComposeResImageFiles.jpeg.readHeaderBytes()
        assertFalse(isAvifFile(jpegHeaderBytes))
        assertFalse(isStaticsAvifFile(jpegHeaderBytes))
        assertFalse(isAnimatedAvifFile(jpegHeaderBytes))

        IsoMediaFileTypeBox(majorBrand = "avif").apply {
            assertTrue(isAvifFile(bytes))
            assertTrue(isStaticsAvifFile(bytes))
            assertFalse(isAnimatedAvifFile(bytes))
        }
        IsoMediaFileTypeBox(majorBrand = "avis").apply {
            assertTrue(isAvifFile(bytes))
            assertFalse(isStaticsAvifFile(bytes))
            assertTrue(isAnimatedAvifFile(bytes))
        }

        // mif1 or msf1
        IsoMediaFileTypeBox(majorBrand = "mif1").apply {
            assertFalse(isAvifFile(bytes))
            assertFalse(isStaticsAvifFile(bytes))
            assertFalse(isAnimatedAvifFile(bytes))
        }
        IsoMediaFileTypeBox(majorBrand = "mif1", compatibleBrands = listOf("avif")).apply {
            assertTrue(isAvifFile(bytes))
            assertTrue(isStaticsAvifFile(bytes))
            assertFalse(isAnimatedAvifFile(bytes))
        }
        IsoMediaFileTypeBox(majorBrand = "mif1", compatibleBrands = listOf("avis")).apply {
            assertFalse(isAvifFile(bytes))
            assertFalse(isStaticsAvifFile(bytes))
            assertFalse(isAnimatedAvifFile(bytes))
        }

        IsoMediaFileTypeBox(majorBrand = "msf1").apply {
            assertFalse(isAvifFile(bytes))
            assertFalse(isStaticsAvifFile(bytes))
            assertFalse(isAnimatedAvifFile(bytes))
        }
        IsoMediaFileTypeBox(majorBrand = "msf1", compatibleBrands = listOf("avif")).apply {
            assertTrue(isAvifFile(bytes))
            assertTrue(isStaticsAvifFile(bytes))
            assertFalse(isAnimatedAvifFile(bytes))
        }
        IsoMediaFileTypeBox(majorBrand = "msf1", compatibleBrands = listOf("avis")).apply {
            assertTrue(isAvifFile(bytes))
            assertFalse(isStaticsAvifFile(bytes))
            assertTrue(isAnimatedAvifFile(bytes))
        }

        // test sliding equals
        IsoMediaFileTypeBox("mif1", listOf("fake", "avif")).apply {
            assertTrue(isAvifFile(bytes))
            assertTrue(isStaticsAvifFile(bytes))
            assertFalse(isAnimatedAvifFile(bytes))
        }
        IsoMediaFileTypeBox("mif1", listOf("fake", "avis")).apply {
            assertFalse(isAvifFile(bytes))
            assertFalse(isStaticsAvifFile(bytes))
            assertFalse(isAnimatedAvifFile(bytes))
        }

        IsoMediaFileTypeBox("msf1", listOf("fake", "avif")).apply {
            assertTrue(isAvifFile(bytes))
            assertTrue(isStaticsAvifFile(bytes))
            assertFalse(isAnimatedAvifFile(bytes))
        }
        IsoMediaFileTypeBox("msf1", listOf("fake", "avis")).apply {
            assertTrue(isAvifFile(bytes))
            assertFalse(isStaticsAvifFile(bytes))
            assertTrue(isAnimatedAvifFile(bytes))
        }

        // test box length
        IsoMediaFileTypeBox(majorBrand = "mif1", compatibleBrands = listOf("avif")).apply {
            assertTrue(isAvifFile(bytes))
            assertTrue(isStaticsAvifFile(bytes))
            assertFalse(isAnimatedAvifFile(bytes))

            val badBytes = bytes.copyOf().apply {
                set(16, 0.toByteArray())
            }
            val bad2Bytes = badBytes.plus("avif".encodeToByteArray())
            assertFalse(isAvifFile(badBytes))
            assertFalse(isStaticsAvifFile(badBytes))
            assertFalse(isAnimatedAvifFile(badBytes))

            assertFalse(isAvifFile(bad2Bytes))
            assertFalse(isStaticsAvifFile(bad2Bytes))
            assertFalse(isAnimatedAvifFile(bad2Bytes))
        }

        IsoMediaFileTypeBox(majorBrand = "msf1", compatibleBrands = listOf("avif")).apply {
            assertTrue(isAvifFile(bytes))
            assertTrue(isStaticsAvifFile(bytes))
            assertFalse(isAnimatedAvifFile(bytes))

            val badBytes = bytes.copyOf().apply {
                set(16, 0.toByteArray())
            }
            val bad2Bytes = badBytes.plus("avif".encodeToByteArray())
            assertFalse(isAvifFile(badBytes))
            assertFalse(isStaticsAvifFile(badBytes))
            assertFalse(isAnimatedAvifFile(badBytes))

            assertFalse(isAvifFile(bad2Bytes))
            assertFalse(isStaticsAvifFile(bad2Bytes))
            assertFalse(isAnimatedAvifFile(bad2Bytes))
        }
        IsoMediaFileTypeBox(majorBrand = "msf1", compatibleBrands = listOf("avis")).apply {
            assertTrue(isAvifFile(bytes))
            assertFalse(isStaticsAvifFile(bytes))
            assertTrue(isAnimatedAvifFile(bytes))

            val badBytes = bytes.copyOf().apply {
                set(16, 0.toByteArray())
            }
            val bad2Bytes = badBytes.plus("avis".encodeToByteArray())
            assertFalse(isAvifFile(badBytes))
            assertFalse(isStaticsAvifFile(badBytes))
            assertFalse(isAnimatedAvifFile(badBytes))

            assertFalse(isAvifFile(bad2Bytes))
            assertFalse(isStaticsAvifFile(bad2Bytes))
            assertFalse(isAnimatedAvifFile(bad2Bytes))
        }
    }

    @Test
    fun testSvg() = runTest {
        val svgHeaderBytes = ComposeResImageFiles.svg.readHeaderBytes()
        val pngHeaderBytes = ComposeResImageFiles.png.readHeaderBytes()

        assertTrue(isSvgFile(svgHeaderBytes))
        assertFalse(isSvgFile(pngHeaderBytes))
    }

    @Test
    fun testBmp() = runTest {
        val bmpHeaderBytes = ComposeResImageFiles.bmp.readHeaderBytes()
        val pngHeaderBytes = ComposeResImageFiles.png.readHeaderBytes()

        assertTrue(isBmpFile(bmpHeaderBytes))
        assertFalse(isBmpFile(pngHeaderBytes))
    }

    @Test
    fun testJpeg() = runTest {
        val jpegHeaderBytes = ComposeResImageFiles.jpeg.readHeaderBytes()
        val clockHorJpegHeaderBytes = ComposeResImageFiles.clockHor.readHeaderBytes()
        val pngHeaderBytes = ComposeResImageFiles.png.readHeaderBytes()

        assertTrue(isJpegFile(jpegHeaderBytes))
        assertTrue(isJpegFile(clockHorJpegHeaderBytes))
        assertFalse(isJpegFile(pngHeaderBytes))
    }

    @Test
    fun testPng() = runTest {
        val pngHeaderBytes = ComposeResImageFiles.png.readHeaderBytes()
        val jpegHeaderBytes = ComposeResImageFiles.jpeg.readHeaderBytes()

        assertTrue(isPngFile(pngHeaderBytes))
        assertFalse(isPngFile(jpegHeaderBytes))
    }

    @Test
    fun testReadIsoMediaFileTypeBoxLength() = runTest {
        val pairs = listOf(
            ComposeResImageFiles.heic to 24,
            ComposeResImageFiles.avif to 32,
            ComposeResImageFiles.animHeif to 36,
            ComposeResImageFiles.animAvif to 44,
            ComposeResImageFiles.mp4 to 32,
            ComposeResImageFiles.rotationMp4 to 28,
        )
        ComposeResImageFiles.values.forEach {
            val headerBytes = it.readHeaderBytes()
            val pair = pairs.find { pair -> pair.first == it }
            val boxLength = readIsoMediaFileTypeBoxLength(headerBytes)
            if (pair != null) {
                assertEquals(pair.second.toLong(), boxLength, message = it.name)
            } else {
                assertTrue(boxLength !in 0..36, message = "${it.name}: box length $boxLength")
            }
        }

        IsoMediaFileTypeBox(
            majorBrand = "mif1",
            minorVersion = 0,
            compatibleBrands = listOf("avif", "avis")
        ).apply {
            assertEquals(24L, readIsoMediaFileTypeBoxLength(bytes))
        }

        IsoMediaFileTypeBox(
            majorBrand = "mif1",
            minorVersion = 0,
            compatibleBrands = listOf("avif", "avis", "hevc")
        ).apply {
            assertEquals(28L, readIsoMediaFileTypeBoxLength(bytes))
        }

        10.toByteArray().plus((Int.MAX_VALUE.toLong() + 1L).toByteArray()).apply {
            assertEquals(10L, readIsoMediaFileTypeBoxLength(this))
        }

        0.toByteArray().plus((Int.MAX_VALUE.toLong() + 1L).toByteArray()).apply {
            assertEquals(0L, readIsoMediaFileTypeBoxLength(this))
        }

        1.toByteArray().plus((Int.MAX_VALUE.toLong() + 1L).toByteArray()).apply {
            assertEquals((Int.MAX_VALUE.toLong() + 1L), readIsoMediaFileTypeBoxLength(this))
        }
    }

    private suspend fun ComposeResImageFile.readHeaderBytes(): ByteArray {
        val (context, sketch) = getTestContextAndSketch()
        return ImageRequest(context, this.uri).fetch(sketch).headerBytes
    }
}