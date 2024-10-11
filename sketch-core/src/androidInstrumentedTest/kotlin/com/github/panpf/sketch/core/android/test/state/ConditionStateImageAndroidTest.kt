package com.github.panpf.sketch.core.android.test.state

import android.graphics.drawable.ColorDrawable
import com.github.panpf.sketch.drawable.asEquitable
import com.github.panpf.sketch.state.ColorDrawableStateImage
import com.github.panpf.sketch.state.ConditionStateImage
import com.github.panpf.sketch.state.ConditionStateImage.DefaultCondition
import com.github.panpf.sketch.state.DrawableStateImage
import com.github.panpf.sketch.state.addState
import com.github.panpf.sketch.test.utils.TestColor
import com.github.panpf.sketch.test.utils.UriInvalidCondition
import com.github.panpf.sketch.util.IntColorFetcher
import com.github.panpf.sketch.util.ResColorFetcher
import kotlin.test.Test
import kotlin.test.assertEquals

class ConditionStateImageAndroidTest {

    @Test
    fun testConditionStateImage() {
        ConditionStateImage(ColorDrawable(TestColor.RED).asEquitable()) {
        }.apply {
            assertEquals(1, stateList.size)
            assertEquals(
                expected = DrawableStateImage(ColorDrawable(TestColor.RED).asEquitable()),
                actual = stateList.find { it.first == DefaultCondition }?.second
            )
        }
        ConditionStateImage(ColorDrawable(TestColor.RED).asEquitable()) {
            addState(UriInvalidCondition, ColorDrawable(TestColor.GREEN).asEquitable())
        }.apply {
            assertEquals(2, stateList.size)
            assertEquals(
                expected = DrawableStateImage(ColorDrawable(TestColor.RED).asEquitable()),
                actual = stateList.find { it.first == DefaultCondition }?.second
            )
            assertEquals(
                expected = DrawableStateImage(ColorDrawable(TestColor.GREEN).asEquitable()),
                actual = stateList.find { it.first == UriInvalidCondition }?.second
            )
        }

        ConditionStateImage(android.R.drawable.ic_lock_lock) {
        }.apply {
            assertEquals(1, stateList.size)
            assertEquals(
                expected = DrawableStateImage(android.R.drawable.ic_lock_lock),
                actual = stateList.find { it.first == DefaultCondition }?.second
            )
        }
        ConditionStateImage(android.R.drawable.ic_lock_lock) {
            addState(UriInvalidCondition, android.R.drawable.ic_delete)
        }.apply {
            assertEquals(2, stateList.size)
            assertEquals(
                expected = DrawableStateImage(android.R.drawable.ic_lock_lock),
                actual = stateList.find { it.first == DefaultCondition }?.second
            )
            assertEquals(
                expected = DrawableStateImage(android.R.drawable.ic_delete),
                actual = stateList.find { it.first == UriInvalidCondition }?.second
            )
        }

        ConditionStateImage(IntColorFetcher(TestColor.RED)) {
        }.apply {
            assertEquals(1, stateList.size)
            assertEquals(
                expected = ColorDrawableStateImage(IntColorFetcher(TestColor.RED)),
                actual = stateList.find { it.first == DefaultCondition }?.second
            )
        }
        ConditionStateImage(IntColorFetcher(TestColor.RED)) {
            addState(UriInvalidCondition, IntColorFetcher(TestColor.GREEN))
        }.apply {
            assertEquals(2, stateList.size)
            assertEquals(
                expected = ColorDrawableStateImage(IntColorFetcher(TestColor.RED)),
                actual = stateList.find { it.first == DefaultCondition }?.second
            )
            assertEquals(
                expected = ColorDrawableStateImage(IntColorFetcher(TestColor.GREEN)),
                actual = stateList.find { it.first == UriInvalidCondition }?.second
            )
        }

        ConditionStateImage(ResColorFetcher(android.R.color.holo_purple)) {
        }.apply {
            assertEquals(1, stateList.size)
            assertEquals(
                expected = ColorDrawableStateImage(ResColorFetcher(android.R.color.holo_purple)),
                actual = stateList.find { it.first == DefaultCondition }?.second
            )
        }
        ConditionStateImage(ResColorFetcher(android.R.color.holo_purple)) {
            addState(UriInvalidCondition, ResColorFetcher(android.R.color.holo_orange_light))
        }.apply {
            assertEquals(2, stateList.size)
            assertEquals(
                expected = ColorDrawableStateImage(ResColorFetcher(android.R.color.holo_purple)),
                actual = stateList.find { it.first == DefaultCondition }?.second
            )
            assertEquals(
                expected = ColorDrawableStateImage(ResColorFetcher(android.R.color.holo_orange_light)),
                actual = stateList.find { it.first == UriInvalidCondition }?.second
            )
        }
    }

    @Test
    fun testAddState() {
        assertEquals(
            expected = ConditionStateImage(
                defaultImage = DrawableStateImage(ColorDrawable(TestColor.RED).asEquitable())
            ) {
                addState(
                    UriInvalidCondition,
                    DrawableStateImage(ColorDrawable(TestColor.RED).asEquitable())
                )
            },
            actual = ConditionStateImage(
                defaultImage = DrawableStateImage(ColorDrawable(TestColor.RED).asEquitable())
            ) {
                addState(
                    condition = UriInvalidCondition,
                    drawable = ColorDrawable(TestColor.RED).asEquitable()
                )
            }
        )

        assertEquals(
            expected = ConditionStateImage(
                defaultImage = DrawableStateImage(ColorDrawable(TestColor.RED).asEquitable())
            ) {
                addState(
                    UriInvalidCondition,
                    DrawableStateImage(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated)
                )
            },
            actual = ConditionStateImage(
                defaultImage = DrawableStateImage(ColorDrawable(TestColor.RED).asEquitable())
            ) {
                addState(
                    condition = UriInvalidCondition,
                    resId = com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated
                )
            }
        )

        assertEquals(
            expected = ConditionStateImage(
                defaultImage = DrawableStateImage(ColorDrawable(TestColor.RED).asEquitable())
            ) {
                addState(
                    UriInvalidCondition,
                    ColorDrawableStateImage(IntColorFetcher(TestColor.RED))
                )
            },
            actual = ConditionStateImage(
                defaultImage = DrawableStateImage(ColorDrawable(TestColor.RED).asEquitable())
            ) {
                addState(
                    condition = UriInvalidCondition,
                    color = IntColorFetcher(TestColor.RED)
                )
            }
        )

        assertEquals(
            expected = ConditionStateImage(
                defaultImage = DrawableStateImage(ColorDrawable(TestColor.RED).asEquitable())
            ) {
                addState(
                    UriInvalidCondition,
                    ColorDrawableStateImage(ResColorFetcher(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated))
                )
            },
            actual = ConditionStateImage(
                defaultImage = DrawableStateImage(ColorDrawable(TestColor.RED).asEquitable())
            ) {
                addState(
                    condition = UriInvalidCondition,
                    color = ResColorFetcher(com.github.panpf.sketch.test.utils.core.R.drawable.ic_animated)
                )
            }
        )
    }
}