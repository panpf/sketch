package com.github.panpf.sketch.test.transform

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.panpf.sketch.transform.CircleCropTransformation
import com.github.panpf.sketch.transform.RotateTransformation
import com.github.panpf.sketch.transform.RoundedCornersTransformation
import com.github.panpf.sketch.transform.Transformation
import com.github.panpf.sketch.transform.merge
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TransformationTest {

    @Test
    fun testMerge() {
        val list1 = listOf(CircleCropTransformation(), RoundedCornersTransformation())
        val list2 = listOf(RoundedCornersTransformation(), RotateTransformation(45))
        val nullElement = null as List<Transformation>?

        list1.merge(list1)!!.apply {
            Assert.assertEquals(list1, this)
            Assert.assertNotSame(list1, this)
        }
        list2.merge(list2)!!.apply {
            Assert.assertEquals(list2, this)
            Assert.assertNotSame(list2, this)
        }
        list1.merge(list2)!!.apply {
            Assert.assertEquals(
                listOf(
                    CircleCropTransformation(),
                    RoundedCornersTransformation(),
                    RotateTransformation(45)
                ),
                this
            )
            Assert.assertSame(list1[1], this[1])
            Assert.assertEquals(list2[0], this[1])
            Assert.assertNotSame(list2[0], this[1])
        }

        Assert.assertSame(list2, nullElement.merge(list2))
        Assert.assertSame(list1, list1.merge(nullElement))
    }
}