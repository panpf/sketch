package com.github.panpf.sketch.sample.ui.test

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.widget.ImageView.ScaleType
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.decodeToImageBitmap
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.util.fastRoundToInt
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.drawable.toDrawable
import com.github.panpf.sketch.drawable.ResizeDrawable
import com.github.panpf.sketch.sample.resources.Res
import com.github.panpf.sketch.sample.ui.util.scale
import com.github.panpf.sketch.sample.ui.util.wrappedBackground
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.toScale

class ResizeDrawableTestFragment : BaseDrawableTestFragment() {

    override val title: String
        get() = "ResizeDrawable"

    override suspend fun buildDrawables(
        context: Context,
        scaleType: ScaleType,
        itemWidth: Float,
    ): List<DrawableScaleType> {
        val containerWidth = itemWidth * 0.75f
        val numbersBitmap = Res.readBytes("drawable/numbers.jpg").decodeToImageBitmap()
        val smallImageWidth = itemWidth * 0.5f
        val smallImageBitmap = numbersBitmap.scale(
            size = IntSize(
                width = smallImageWidth.fastRoundToInt(),
                height = smallImageWidth.fastRoundToInt()
            )
        ).asAndroidBitmap()
        val smallDrawable = smallImageBitmap.toDrawable(context.resources)
        val bigImageWidth = itemWidth * 1.5f
        val bigImageBitmap = numbersBitmap.scale(
            size = IntSize(
                width = bigImageWidth.fastRoundToInt(),
                height = bigImageWidth.fastRoundToInt()
            )
        ).asAndroidBitmap()
        val bigDrawable = bigImageBitmap.toDrawable(context.resources)
        return mutableListOf<Pair<String, Drawable>>(
            "Small" to ResizeDrawable(
                drawable = smallDrawable,
                size = Size(containerWidth.fastRoundToInt(), containerWidth.fastRoundToInt()),
                scale = scaleType.toScale(),
            ),
            "Big" to ResizeDrawable(
                drawable = bigDrawable,
                size = Size(containerWidth.fastRoundToInt(), containerWidth.fastRoundToInt()),
                scale = scaleType.toScale(),
            ),
        ).map {
            DrawableScaleType(
                title = it.first,
                drawable = it.second.wrappedBackground(
                    color = ColorUtils.setAlphaComponent(Color.GREEN, 120)
                )
            )
        }
    }
}