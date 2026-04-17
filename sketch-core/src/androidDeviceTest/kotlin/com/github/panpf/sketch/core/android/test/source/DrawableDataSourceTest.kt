package com.github.panpf.sketch.core.android.test.source

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.VectorDrawable
import android.os.Build.VERSION
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.github.panpf.sketch.drawable.ResDrawableFetcher
import com.github.panpf.sketch.source.DataFrom
import com.github.panpf.sketch.source.DrawableDataSource
import com.github.panpf.sketch.test.singleton.getTestContextAndSketch
import com.github.panpf.sketch.test.utils.getTestContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class DrawableDataSourceTest {

    @Test
    fun testConstructor() {
        val context = getTestContext()

        DrawableDataSource(
            context = context,
            drawableFetcher = ResDrawableFetcher(android.R.drawable.ic_delete),
            dataFrom = DataFrom.LOCAL,
        ).apply {
            assertTrue(this.drawable is BitmapDrawable)
            assertEquals(DataFrom.LOCAL, this.dataFrom)
        }

        DrawableDataSource(
            context = context,
            drawableFetcher = ResDrawableFetcher(com.github.panpf.sketch.test.R.drawable.ic_cloudy),
            dataFrom = DataFrom.MEMORY,
        ).apply {
            if (VERSION.SDK_INT >= 24) {
                assertTrue(this.drawable is VectorDrawable)
            } else {
                assertTrue(this.drawable is VectorDrawableCompat)
            }
            assertEquals(DataFrom.MEMORY, this.dataFrom)
        }
    }

    @Test
    fun testKey() {
        val context = getTestContext()
        DrawableDataSource(
            context = context,
            drawableFetcher = ResDrawableFetcher(android.R.drawable.ic_delete),
            dataFrom = DataFrom.LOCAL,
        ).apply {
            assertEquals(ResDrawableFetcher(android.R.drawable.ic_delete).key, this.key)
        }
    }

    @Test
    fun testOpenSource() {
        val context = getTestContext()
        assertFailsWith(UnsupportedOperationException::class) {
            DrawableDataSource(
                context = context,
                drawableFetcher = ResDrawableFetcher(android.R.drawable.ic_delete),
                dataFrom = DataFrom.LOCAL,
            ).openSource()
        }
    }

    @Test
    fun testGetFile() {
        val (context, sketch) = getTestContextAndSketch()
        assertFailsWith(UnsupportedOperationException::class) {
            DrawableDataSource(
                context = context,
                drawableFetcher = ResDrawableFetcher(android.R.drawable.ic_delete),
                dataFrom = DataFrom.LOCAL,
            ).getFile(sketch)
        }
    }

    @Test
    fun testEqualsAndHashCode() {
        val context = getTestContext()
        val element1 = DrawableDataSource(
            context = context,
            drawableFetcher = ResDrawableFetcher(android.R.drawable.ic_delete),
            dataFrom = DataFrom.LOCAL,
        )
        val element11 = DrawableDataSource(
            context = context,
            drawableFetcher = ResDrawableFetcher(android.R.drawable.ic_delete),
            dataFrom = DataFrom.LOCAL,
        )
        val element2 = DrawableDataSource(
            context = context,
            drawableFetcher = ResDrawableFetcher(com.github.panpf.sketch.test.R.drawable.ic_cloudy),
            dataFrom = DataFrom.LOCAL,
        )
        val element3 = DrawableDataSource(
            context = context,
            drawableFetcher = ResDrawableFetcher(com.github.panpf.sketch.test.R.drawable.ic_cloudy),
            dataFrom = DataFrom.MEMORY,
        )

        assertEquals(element1, element11)
        assertNotEquals(element1, element2)
        assertNotEquals(element1, element3)
        assertNotEquals(element2, element3)
        assertNotEquals(element1, null as Any?)
        assertNotEquals(element1, Any())

        assertNotEquals(element1.hashCode(), element2.hashCode())
        assertNotEquals(element1.hashCode(), element3.hashCode())
        assertNotEquals(element2.hashCode(), element3.hashCode())
    }

    @Test
    fun testToString() {
        val context = getTestContext()

        DrawableDataSource(
            context = context,
            drawableFetcher = ResDrawableFetcher(android.R.drawable.ic_delete),
            dataFrom = DataFrom.LOCAL,
        ).apply {
            assertEquals(
                expected = "DrawableDataSource(drawable=${drawableFetcher}, from=$dataFrom)",
                actual = toString()
            )
        }
    }
}