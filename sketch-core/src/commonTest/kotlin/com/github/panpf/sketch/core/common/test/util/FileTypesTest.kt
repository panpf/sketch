package com.github.panpf.sketch.core.common.test.util

import com.github.panpf.sketch.images.ComposeResImageFile
import com.github.panpf.sketch.images.ComposeResImageFiles
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.fetch
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
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
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
        val animatedHeifHeaderBytes = ComposeResImageFiles.animHeif.readHeaderBytes()
        val animatedWebpHeaderBytes = ComposeResImageFiles.animWebp.readHeaderBytes()
        val staticsAvifHeaderBytes = ComposeResImageFiles.avif.readHeaderBytes()
        val animatedAvifHeaderBytes = ComposeResImageFiles.animAvif.readHeaderBytes()
        val jpegHeaderBytes = ComposeResImageFiles.jpeg.readHeaderBytes()

        assertTrue(isHeifFile(staticsHeifHeaderBytes))
        assertTrue(isHeifFile(animatedHeifHeaderBytes))
        assertFalse(isHeifFile(animatedWebpHeaderBytes))
        assertFalse(isHeifFile(staticsAvifHeaderBytes))
        assertFalse(isHeifFile(animatedAvifHeaderBytes))
        assertFalse(isHeifFile(jpegHeaderBytes))

        assertTrue(isStaticsHeifFile(staticsHeifHeaderBytes))
        assertFalse(isStaticsHeifFile(animatedHeifHeaderBytes))
        assertFalse(isStaticsHeifFile(animatedWebpHeaderBytes))
        assertFalse(isStaticsHeifFile(staticsAvifHeaderBytes))
        assertFalse(isStaticsHeifFile(animatedAvifHeaderBytes))
        assertFalse(isStaticsHeifFile(jpegHeaderBytes))
        assertTrue(isStaticsHeifFile(animatedHeifHeaderBytes.copyOf().apply {
            set(8, 'm'.code.toByte())
            set(9, 'i'.code.toByte())
            set(10, 'f'.code.toByte())
            set(11, '1'.code.toByte())
        }))
        assertTrue(isStaticsHeifFile(animatedHeifHeaderBytes.copyOf().apply {
            set(8, 'h'.code.toByte())
            set(9, 'e'.code.toByte())
            set(10, 'i'.code.toByte())
            set(11, 'c'.code.toByte())
        }))
        assertTrue(isStaticsHeifFile(animatedHeifHeaderBytes.copyOf().apply {
            set(8, 'h'.code.toByte())
            set(9, 'e'.code.toByte())
            set(10, 'i'.code.toByte())
            set(11, 'x'.code.toByte())
        }))

        assertFalse(isAnimatedHeifFile(staticsHeifHeaderBytes))
        assertTrue(isAnimatedHeifFile(animatedHeifHeaderBytes))
        assertFalse(isAnimatedHeifFile(animatedWebpHeaderBytes))
        assertFalse(isAnimatedHeifFile(staticsAvifHeaderBytes))
        assertFalse(isAnimatedHeifFile(animatedAvifHeaderBytes))
        assertFalse(isAnimatedHeifFile(jpegHeaderBytes))
        assertTrue(isAnimatedHeifFile(staticsHeifHeaderBytes.copyOf().apply {
            set(8, 'm'.code.toByte())
            set(9, 's'.code.toByte())
            set(10, 'f'.code.toByte())
            set(11, '1'.code.toByte())
        }))
        assertTrue(isAnimatedHeifFile(staticsHeifHeaderBytes.copyOf().apply {
            set(8, 'h'.code.toByte())
            set(9, 'e'.code.toByte())
            set(10, 'v'.code.toByte())
            set(11, 'c'.code.toByte())
        }))
        assertTrue(isAnimatedHeifFile(staticsHeifHeaderBytes.copyOf().apply {
            set(8, 'h'.code.toByte())
            set(9, 'e'.code.toByte())
            set(10, 'v'.code.toByte())
            set(11, 'x'.code.toByte())
        }))
    }

    @Test
    fun testAvif() = runTest {
        val staticsAvifHeaderBytes = ComposeResImageFiles.avif.readHeaderBytes()
        val animatedAvifHeaderBytes = ComposeResImageFiles.animAvif.readHeaderBytes()
        val animatedWebpHeaderBytes = ComposeResImageFiles.animWebp.readHeaderBytes()
        val staticsHeifHeaderBytes = ComposeResImageFiles.heic.readHeaderBytes()
        val animatedHeifHeaderBytes = ComposeResImageFiles.animHeif.readHeaderBytes()
        val jpegHeaderBytes = ComposeResImageFiles.jpeg.readHeaderBytes()

        assertTrue(isAvifFile(staticsAvifHeaderBytes))
        assertTrue(isAvifFile(animatedAvifHeaderBytes))
        assertFalse(isAvifFile(animatedWebpHeaderBytes))
        assertFalse(isAvifFile(staticsHeifHeaderBytes))
        assertFalse(isAvifFile(animatedHeifHeaderBytes))
        assertFalse(isAvifFile(jpegHeaderBytes))

        assertTrue(isStaticsAvifFile(staticsAvifHeaderBytes))
        assertFalse(isStaticsAvifFile(animatedAvifHeaderBytes))
        assertFalse(isStaticsAvifFile(animatedWebpHeaderBytes))
        assertFalse(isStaticsAvifFile(staticsHeifHeaderBytes))
        assertFalse(isStaticsAvifFile(animatedHeifHeaderBytes))
        assertFalse(isStaticsAvifFile(jpegHeaderBytes))
        assertTrue(isStaticsAvifFile(animatedAvifHeaderBytes.copyOf().apply {
            set(8, 'a'.code.toByte())
            set(9, 'v'.code.toByte())
            set(10, 'i'.code.toByte())
            set(11, 'f'.code.toByte())
        }))

        assertFalse(isAnimatedAvifFile(staticsAvifHeaderBytes))
        assertTrue(isAnimatedAvifFile(animatedAvifHeaderBytes))
        assertFalse(isAnimatedAvifFile(animatedWebpHeaderBytes))
        assertFalse(isAnimatedAvifFile(staticsHeifHeaderBytes))
        assertFalse(isAnimatedAvifFile(animatedHeifHeaderBytes))
        assertFalse(isAnimatedAvifFile(jpegHeaderBytes))
        assertTrue(isAnimatedAvifFile(staticsAvifHeaderBytes.copyOf().apply {
            set(8, 'a'.code.toByte())
            set(9, 'v'.code.toByte())
            set(10, 'i'.code.toByte())
            set(11, 's'.code.toByte())
        }))
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

    private suspend fun ComposeResImageFile.readHeaderBytes(): ByteArray {
        val (context, sketch) = getTestContextAndSketch()
        return ImageRequest(context, this.uri).fetch(sketch).headerBytes
    }
}