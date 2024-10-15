package com.github.panpf.sketch.compose.resources.common.test.state

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import com.github.panpf.sketch.painter.equitablePainterResource
import com.github.panpf.sketch.state.ComposableConditionStateImage
import com.github.panpf.sketch.state.ConditionStateImage.DefaultCondition
import com.github.panpf.sketch.state.PainterStateImage
import com.github.panpf.sketch.state.addState
import com.github.panpf.sketch.test.compose.resources.Res
import com.github.panpf.sketch.test.compose.resources.desert
import com.github.panpf.sketch.test.compose.resources.moon
import com.github.panpf.sketch.test.utils.UriInvalidCondition
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalTestApi::class)
class ConditionStateImageComposeResourcesTest {

    @Test
    fun testComposableConditionStateImage() {
        runComposeUiTest {
            setContent {
                ComposableConditionStateImage(Res.drawable.moon) {}.apply {
                    assertEquals(1, stateList.size)
                    assertEquals(
                        expected = PainterStateImage(equitablePainterResource(Res.drawable.moon)),
                        actual = stateList.find { it.first == DefaultCondition }?.second
                    )
                }

                ComposableConditionStateImage(Res.drawable.moon) {
                    addState(UriInvalidCondition, Res.drawable.desert)
                }.apply {
                    assertEquals(2, stateList.size)
                    assertEquals(
                        expected = PainterStateImage(equitablePainterResource(Res.drawable.moon)),
                        actual = stateList.find { it.first == DefaultCondition }?.second
                    )
                    assertEquals(
                        expected = PainterStateImage(equitablePainterResource(Res.drawable.desert)),
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
                ComposableConditionStateImage(Res.drawable.moon) {
                    addState(UriInvalidCondition, Res.drawable.desert)
                }.apply {
                    assertEquals(2, stateList.size)
                    assertEquals(
                        expected = PainterStateImage(equitablePainterResource(Res.drawable.moon)),
                        actual = stateList.find { it.first == DefaultCondition }?.second
                    )
                    assertEquals(
                        expected = PainterStateImage(equitablePainterResource(Res.drawable.desert)),
                        actual = stateList.find { it.first == UriInvalidCondition }?.second
                    )
                }
            }
        }
    }
}