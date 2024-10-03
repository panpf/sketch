@file:OptIn(ExperimentalTestApi::class)

package com.github.panpf.sketch.compose.resources.common.test.painter

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.runComposeUiTest
import com.github.panpf.sketch.painter.IconPainter
import com.github.panpf.sketch.painter.asEquitable
import com.github.panpf.sketch.painter.equitablePainterResource
import com.github.panpf.sketch.painter.rememberIconPainter
import com.github.panpf.sketch.test.utils.SizeColorPainter
import com.github.panpf.sketch.test.utils.TestAnimatablePainter
import com.github.panpf.sketch.test.utils.compose.core.resources.Res
import com.github.panpf.sketch.test.utils.compose.core.resources.moon
import org.jetbrains.compose.resources.DrawableResource
import kotlin.test.Test
import kotlin.test.assertEquals

class IconPainterComposeResourcesTest {
    // TODO test

    @Test
    fun testRememberIconPainter() {
        runComposeUiTest {
            setContent {
                assertEquals(
                    expected = IconPainter(
                        icon = TestAnimatablePainter(SizeColorPainter(Color.Gray, Size(100f, 100f)))
                            .asEquitable(Color.Gray),
                        background = null,
                        iconSize = null,
                        iconTint = null
                    ),
                    actual = rememberIconPainter(
                        icon = TestAnimatablePainter(SizeColorPainter(Color.Gray, Size(100f, 100f)))
                            .asEquitable(Color.Gray),
                        background = null as DrawableResource?
                    )
                )
                assertEquals(
                    expected = IconPainter(
                        icon = TestAnimatablePainter(SizeColorPainter(Color.Gray, Size(100f, 100f)))
                            .asEquitable(Color.Gray),
                        background = equitablePainterResource(Res.drawable.moon),
                        iconSize = null,
                        iconTint = null
                    ),
                    actual = rememberIconPainter(
                        icon = TestAnimatablePainter(SizeColorPainter(Color.Gray, Size(100f, 100f)))
                            .asEquitable(Color.Gray),
                        background = Res.drawable.moon
                    )
                )
                assertEquals(
                    expected = IconPainter(
                        icon = TestAnimatablePainter(SizeColorPainter(Color.Gray, Size(100f, 100f)))
                            .asEquitable(Color.Gray),
                        background = equitablePainterResource(Res.drawable.moon),
                        iconSize = Size(101f, 202f),
                        iconTint = null
                    ),
                    actual = rememberIconPainter(
                        icon = TestAnimatablePainter(SizeColorPainter(Color.Gray, Size(100f, 100f)))
                            .asEquitable(Color.Gray),
                        background = Res.drawable.moon,
                        iconSize = Size(101f, 202f)
                    )
                )
                assertEquals(
                    expected = IconPainter(
                        icon = TestAnimatablePainter(SizeColorPainter(Color.Gray, Size(100f, 100f)))
                            .asEquitable(Color.Gray),
                        background = equitablePainterResource(Res.drawable.moon),
                        iconSize = Size(101f, 202f),
                        iconTint = Color.Blue
                    ),
                    actual = rememberIconPainter(
                        icon = TestAnimatablePainter(SizeColorPainter(Color.Gray, Size(100f, 100f)))
                            .asEquitable(Color.Gray),
                        background = Res.drawable.moon,
                        iconSize = Size(101f, 202f),
                        iconTint = Color.Blue
                    )
                )

//                val painterIcon =
//                    Color.Cyan.let { SizeColorPainter(it, Size(100f, 100f)).asEquitable(it) }
//                val resourceIcon = DrawableResource(
//                    "drawable:test_icon",
//                    setOf(
//                        org.jetbrains.compose.resources.ResourceItem(
//                            setOf(),
//                            "composeResources/sketch_root.sample.generated.resources/drawable/ic_info_baseline.xml",
//                            -1,
//                            -1
//                        ),
//                    )
//                )
//                val painterBackground =
//                    Color.Gray.let { SizeColorPainter(it, Size(100f, 100f)).asEquitable(it) }
//                val colorBackground = Color.DarkGray
//                val resourceBackground = DrawableResource(
//                    "drawable:test_background",
//                    setOf(
//                        org.jetbrains.compose.resources.ResourceItem(
//                            setOf(),
//                            "composeResources/sketch_root.sample.generated.resources/drawable/ic_info_baseline.xml",
//                            -1,
//                            -1
//                        ),
//                    )
//                )
//                val iconSize = Size(200f, 200f)
//                val iconTint = Color.Magenta
//                setContent {
//                    rememberIconPainter(
//                        icon = resourceIcon,
//                        background = painterBackground,
//                        iconSize = iconSize,
//                        iconTint = iconTint
//                    )
//                    rememberIconPainter(
//                        icon = resourceIcon,
//                        background = colorBackground,
//                        iconSize = iconSize,
//                        iconTint = iconTint
//                    )
//                    rememberIconPainter(
//                        icon = resourceIcon,
//                        background = resourceBackground,
//                        iconSize = iconSize,
//                        iconTint = iconTint
//                    )
//
//                    rememberIconPainter(
//                        icon = resourceIcon,
//                        background = painterBackground,
//                        iconSize = iconSize,
//                    )
//                    rememberIconPainter(
//                        icon = resourceIcon,
//                        background = colorBackground,
//                        iconSize = iconSize,
//                    )
//                    rememberIconPainter(
//                        icon = resourceIcon,
//                        background = resourceBackground,
//                        iconSize = iconSize,
//                    )
//
//                    rememberIconPainter(
//                        icon = resourceIcon,
//                        background = painterBackground,
//                        iconTint = iconTint
//                    )
//                    rememberIconPainter(
//                        icon = resourceIcon,
//                        background = colorBackground,
//                        iconTint = iconTint
//                    )
//                    rememberIconPainter(
//                        icon = resourceIcon,
//                        background = resourceBackground,
//                        iconTint = iconTint
//                    )
//
//                    rememberIconPainter(
//                        icon = resourceIcon,
//                        iconSize = iconSize,
//                        iconTint = iconTint
//                    )
//
//                    rememberIconPainter(
//                        icon = resourceIcon,
//                        background = painterBackground,
//                    )
//                    rememberIconPainter(
//                        icon = resourceIcon,
//                        background = colorBackground,
//                    )
//                    rememberIconPainter(
//                        icon = resourceIcon,
//                        background = resourceBackground,
//                    )
//
//                    rememberIconPainter(
//                        icon = resourceIcon,
//                        iconSize = iconSize,
//                    )
//                    rememberIconPainter(
//                        icon = resourceIcon,
//                        iconTint = iconTint
//                    )
//
//                    rememberIconPainter(
//                        icon = resourceIcon,
//                    )
//
//                    rememberIconPainter(
//                        icon = painterIcon,
//                        background = resourceBackground,
//                        iconSize = iconSize,
//                        iconTint = iconTint
//                    )
//                    rememberIconPainter(
//                        icon = painterIcon,
//                        background = resourceBackground,
//                        iconSize = iconSize,
//                    )
//                    rememberIconPainter(
//                        icon = painterIcon,
//                        background = resourceBackground,
//                        iconTint = iconTint
//                    )
//                    rememberIconPainter(
//                        icon = painterIcon,
//                        background = resourceBackground,
//                    )
            }
        }
    }
}