package com.github.panpf.sketch.extensions.test.request

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.extensions.test.getContext
import com.github.panpf.sketch.fetch.newAssetUri
import com.github.panpf.sketch.request.DEPTH_FROM_KEY
import com.github.panpf.sketch.request.Depth.NETWORK
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DownloadRequest
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.LoadRequest
import com.github.panpf.sketch.request.depthFrom
import com.github.panpf.sketch.request.get
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DepthFromTest {

    @Test
    fun testImageOptionsDepth() {
        ImageOptions().apply {
            Assert.assertNull(depthFrom)
            Assert.assertNull(parameters)
        }

        ImageOptions {
            depthFrom(null)
        }.apply {
            Assert.assertNull(depthFrom)
            Assert.assertNull(parameters)
        }

        ImageOptions {
            depthFrom("testDepthFrom")
        }.apply {
            Assert.assertEquals("testDepthFrom", depthFrom)
            Assert.assertNotNull("testDepthFrom", parameters?.get(DEPTH_FROM_KEY))
        }
    }

    @Test
    fun testImageRequestDepth() {
        val context1 = getContext()
        val uriString1 = newAssetUri("sample.jpeg")
        DisplayRequest(context1, uriString1).apply {
            Assert.assertEquals(NETWORK, depth)
            Assert.assertNull(depthFrom)
            Assert.assertNull(parameters)
        }

        DisplayRequest(context1, uriString1) {
            (this as ImageRequest.Builder).depthFrom("testDepthFrom")
        }.apply {
            Assert.assertEquals(NETWORK, depth)
            Assert.assertEquals("testDepthFrom", depthFrom)
            Assert.assertNotNull("testDepthFrom", parameters?.get(DEPTH_FROM_KEY))
        }

        DisplayRequest(context1, uriString1) {
            (this as ImageRequest.Builder).depthFrom(null)
        }.apply {
            Assert.assertEquals(NETWORK, depth)
            Assert.assertNull(depthFrom)
            Assert.assertNull(parameters)
        }
    }

    @Test
    fun testDisplayRequestDepth() {
        val context1 = getContext()
        val uriString1 = newAssetUri("sample.jpeg")
        DisplayRequest(context1, uriString1).apply {
            Assert.assertEquals(NETWORK, depth)
            Assert.assertNull(depthFrom)
            Assert.assertNull(parameters)
        }

        DisplayRequest(context1, uriString1) {
            depthFrom("testDepthFrom")
        }.apply {
            Assert.assertEquals(NETWORK, depth)
            Assert.assertEquals("testDepthFrom", depthFrom)
            Assert.assertNotNull("testDepthFrom", parameters?.get(DEPTH_FROM_KEY))
        }

        DisplayRequest(context1, uriString1) {
            depthFrom(null)
        }.apply {
            Assert.assertEquals(NETWORK, depth)
            Assert.assertNull(depthFrom)
            Assert.assertNull(parameters)
        }
    }

    @Test
    fun testLoadRequestDepth() {
        val context1 = getContext()
        val uriString1 = newAssetUri("sample.jpeg")
        LoadRequest(context1, uriString1).apply {
            Assert.assertEquals(NETWORK, depth)
            Assert.assertNull(depthFrom)
            Assert.assertNull(parameters)
        }

        LoadRequest(context1, uriString1) {
            depthFrom("testDepthFrom")
        }.apply {
            Assert.assertEquals(NETWORK, depth)
            Assert.assertEquals("testDepthFrom", depthFrom)
            Assert.assertNotNull("testDepthFrom", parameters?.get(DEPTH_FROM_KEY))
        }

        LoadRequest(context1, uriString1) {
            depthFrom(null)
        }.apply {
            Assert.assertEquals(NETWORK, depth)
            Assert.assertNull(depthFrom)
            Assert.assertNull(parameters)
        }
    }

    @Test
    fun testDownloadRequestDepth() {
        val context1 = getContext()
        val uriString1 = newAssetUri("sample.jpeg")
        DownloadRequest(context1, uriString1).apply {
            Assert.assertEquals(NETWORK, depth)
            Assert.assertNull(depthFrom)
            Assert.assertNull(parameters)
        }

        DownloadRequest(context1, uriString1) {
            depthFrom("testDepthFrom")
        }.apply {
            Assert.assertEquals(NETWORK, depth)
            Assert.assertEquals("testDepthFrom", depthFrom)
            Assert.assertNotNull("testDepthFrom", parameters?.get(DEPTH_FROM_KEY))
        }

        DownloadRequest(context1, uriString1) {
            depthFrom(null)
        }.apply {
            Assert.assertEquals(NETWORK, depth)
            Assert.assertNull(depthFrom)
            Assert.assertNull(parameters)
        }
    }
}