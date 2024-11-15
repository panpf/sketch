package com.github.panpf.sketch.compose.core.common.test.state

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import com.github.panpf.sketch.state.ColorPainterStateImage
import com.github.panpf.sketch.state.ComposableConditionStateImage
import com.github.panpf.sketch.state.ConditionStateImage
import com.github.panpf.sketch.state.ConditionStateImage.DefaultCondition
import com.github.panpf.sketch.state.addState
import com.github.panpf.sketch.test.utils.FakeStateImage
import com.github.panpf.sketch.test.utils.UriInvalidCondition
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class ConditionStateImageComposeTest {

    @Test
    fun testComposableConditionStateImage() {
        runComposeUiTest {
            setContent {
                ComposableConditionStateImage {}.apply {
                    assertEquals(0, stateList.size)
                    assertEquals(
                        expected = null,
                        actual = stateList.find { it.first == DefaultCondition }?.second
                    )
                }

                ComposableConditionStateImage(FakeStateImage()) {}.apply {
                    assertEquals(1, stateList.size)
                    assertEquals(
                        expected = FakeStateImage(),
                        actual = stateList.find { it.first == DefaultCondition }?.second
                    )
                }

                ComposableConditionStateImage(FakeStateImage()) {
                    addState(UriInvalidCondition, Color.Red)
                }.apply {
                    assertEquals(2, stateList.size)
                    assertEquals(
                        expected = FakeStateImage(),
                        actual = stateList.find { it.first == DefaultCondition }?.second
                    )
                    assertEquals(
                        expected = ColorPainterStateImage(Color.Red),
                        actual = stateList.find { it.first == UriInvalidCondition }?.second
                    )
                }

                ComposableConditionStateImage {
                    addState(UriInvalidCondition, Color.Red)
                }.apply {
                    assertEquals(1, stateList.size)
                    assertEquals(
                        expected = null,
                        actual = stateList.find { it.first == DefaultCondition }?.second
                    )
                    assertEquals(
                        expected = ColorPainterStateImage(Color.Red),
                        actual = stateList.find { it.first == UriInvalidCondition }?.second
                    )
                }

                ComposableConditionStateImage(Color.Cyan) {}.apply {
                    assertEquals(1, stateList.size)
                    assertEquals(
                        expected = ColorPainterStateImage(Color.Cyan),
                        actual = stateList.find { it.first == DefaultCondition }?.second
                    )
                }

                ComposableConditionStateImage(Color.Cyan) {
                    addState(UriInvalidCondition, Color.Red)
                }.apply {
                    assertEquals(2, stateList.size)
                    assertEquals(
                        expected = ColorPainterStateImage(Color.Cyan),
                        actual = stateList.find { it.first == DefaultCondition }?.second
                    )
                    assertEquals(
                        expected = ColorPainterStateImage(Color.Red),
                        actual = stateList.find { it.first == UriInvalidCondition }?.second
                    )
                }
            }
        }
    }

    @Test
    fun testAddState() {
        runComposeUiTest {
            setContent {
                ConditionStateImage(FakeStateImage()) {
                    addState(UriInvalidCondition, Color.Red)
                }.apply {
                    assertEquals(2, stateList.size)
                    assertEquals(
                        expected = FakeStateImage(),
                        actual = stateList.find { it.first == DefaultCondition }?.second
                    )
                    assertEquals(
                        expected = ColorPainterStateImage(Color.Red),
                        actual = stateList.find { it.first == UriInvalidCondition }?.second
                    )
                }
            }
        }
    }
}