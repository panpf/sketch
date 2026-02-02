package com.github.panpf.sketch.animated.gif.koral.test

import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.fetch.newFileUri
import com.github.panpf.sketch.images.ComposeResImageFiles
import com.github.panpf.sketch.images.R
import com.github.panpf.sketch.source.AssetDataSource
import com.github.panpf.sketch.source.ByteArrayDataSource
import com.github.panpf.sketch.source.ContentDataSource
import com.github.panpf.sketch.source.DataFrom
import com.github.panpf.sketch.source.DataFrom.LOCAL
import com.github.panpf.sketch.source.DataSource
import com.github.panpf.sketch.source.FileDataSource
import com.github.panpf.sketch.source.ResourceDataSource
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import kotlinx.coroutines.test.runTest
import okio.Path
import okio.Source
import org.junit.runner.RunWith
import pl.droidsonroids.gif.GifInfoHandleHelper
import pl.droidsonroids.gif.GifOptions
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

@RunWith(AndroidJUnit4::class)
class GifInfoHandlerHelperTest {

    @Test
    fun test() = runTest {
        val (context, sketch) = getTestContextAndSketch()

        val dataSource = ComposeResImageFiles.animGif.toDataSource(context)
        dataSource.getFile(sketch)  // Make sure the gif file is cached
        val snapshot = sketch.downloadCache.openSnapshot("${dataSource.key}_data_source")!!
        GifInfoHandleHelper(
            sketch,
            dataSource as ByteArrayDataSource
        ).apply {
            assertEquals(480, width)
            assertEquals(480, height)
            assertEquals(500, duration)
            assertEquals(5, numberOfFrames)
            setOptions(GifOptions().apply {
                setInSampleSize(2)
            })
            assertEquals(240, width)
            assertEquals(240, height)
            assertNotNull(createGifDrawable().apply { recycle() })
        }

        GifInfoHandleHelper(
            sketch,
            FileDataSource(
                path = snapshot.data,
                dataFrom = LOCAL,
            )
        ).apply {
            assertEquals(480, width)
            assertEquals(480, height)
            assertEquals(500, duration)
            assertEquals(5, numberOfFrames)
            setOptions(GifOptions().apply {
                setInSampleSize(2)
            })
            assertEquals(240, width)
            assertEquals(240, height)
            assertNotNull(createGifDrawable().apply { recycle() })
        }

        GifInfoHandleHelper(
            sketch,
            ResourceDataSource(
                resources = context.resources,
                packageName = context.packageName,
                resId = R.raw.sample_anim
            )
        ).apply {
            assertEquals(480, width)
            assertEquals(480, height)
            assertEquals(500, duration)
            assertEquals(5, numberOfFrames)
            setOptions(GifOptions().apply {
                setInSampleSize(2)
            })
            assertEquals(240, width)
            assertEquals(240, height)
            assertNotNull(createGifDrawable().apply { recycle() })
        }

        GifInfoHandleHelper(
            sketch,
            ContentDataSource(
                context = context,
                contentUri = Uri.fromFile(snapshot.data.toFile()),
            )
        ).apply {
            assertEquals(480, width)
            assertEquals(480, height)
            assertEquals(500, duration)
            assertEquals(5, numberOfFrames)
            setOptions(GifOptions().apply {
                setInSampleSize(2)
            })
            assertEquals(240, width)
            assertEquals(240, height)
            assertNotNull(createGifDrawable().apply { recycle() })
        }

        GifInfoHandleHelper(
            sketch,
            FileDataSource(path = snapshot.data)
        ).apply {
            assertEquals(480, width)
            assertEquals(480, height)
            assertEquals(500, duration)
            assertEquals(5, numberOfFrames)
            setOptions(GifOptions().apply {
                setInSampleSize(2)
            })
            assertEquals(240, width)
            assertEquals(240, height)
            assertNotNull(createGifDrawable().apply { recycle() })
        }

        GifInfoHandleHelper(
            sketch,
            AssetDataSource(
                context = context,
                fileName = "sample_anim.gif"
            )
        ).apply {
            assertEquals(480, width)
            assertEquals(480, height)
            assertEquals(500, duration)
            assertEquals(5, numberOfFrames)
            setOptions(GifOptions().apply {
                setInSampleSize(2)
            })
            assertEquals(240, width)
            assertEquals(240, height)
            assertNotNull(createGifDrawable().apply { recycle() })
        }

        assertFailsWith(Exception::class) {
            GifInfoHandleHelper(
                sketch,
                object : DataSource {
                    override val key: String by lazy { newFileUri(snapshot.data) }
                    override val dataFrom: DataFrom = LOCAL

                    override fun openSource(): Source = throw UnsupportedOperationException()

                    override fun getFile(sketch: Sketch): Path =
                        throw UnsupportedOperationException()
                }
            ).width
        }
    }
}