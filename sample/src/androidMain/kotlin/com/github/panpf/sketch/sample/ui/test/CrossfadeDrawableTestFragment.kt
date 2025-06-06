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
import com.github.panpf.sketch.drawable.CrossfadeDrawable
import com.github.panpf.sketch.sample.resources.Res
import com.github.panpf.sketch.sample.ui.util.SizeColorDrawable
import com.github.panpf.sketch.sample.ui.util.scale
import com.github.panpf.sketch.sample.ui.util.wrappedBackground
import com.github.panpf.sketch.transition.CrossfadeTransition
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.fitScale

class CrossfadeDrawableTestFragment : BaseDrawableTestFragment() {

    override val title: String
        get() = "CrossfadeDrawable"

    override suspend fun buildDrawables(
        context: Context,
        scaleType: ScaleType,
        itemWidth: Float
    ): List<DrawableScaleType> {
        val startDrawableWidth = itemWidth * 0.75f
        val startDrawable = SizeColorDrawable(
            color = Color.BLUE,
            size = Size(startDrawableWidth.fastRoundToInt(), startDrawableWidth.fastRoundToInt())
        )
        val numbersBitmap = Res.readBytes("drawable/numbers.jpg").decodeToImageBitmap()
        val endDrawableWidth = itemWidth * 0.5f
        val endImageBitmap = numbersBitmap.scale(
            size = IntSize(
                width = endDrawableWidth.fastRoundToInt(),
                height = endDrawableWidth.fastRoundToInt()
            )
        ).asAndroidBitmap()
        val endDrawable = endImageBitmap.toDrawable(context.resources)
        return mutableListOf<Pair<String, Drawable>>(
            "Default" to CrossfadeDrawable(
                start = startDrawable,
                end = endDrawable,
                fitScale = scaleType.fitScale,
            ),
            "Long Duration" to CrossfadeDrawable(
                start = startDrawable,
                end = endDrawable,
                fitScale = scaleType.fitScale,
                durationMillis = CrossfadeTransition.DEFAULT_DURATION_MILLIS * 4
            ),
            "No fadeStart" to CrossfadeDrawable(
                start = startDrawable,
                end = endDrawable,
                fitScale = scaleType.fitScale,
                fadeStart = !CrossfadeTransition.DEFAULT_FADE_START
            ),
            "PreferExactIntrinsicSize" to CrossfadeDrawable(
                start = startDrawable,
                end = endDrawable,
                fitScale = scaleType.fitScale,
                preferExactIntrinsicSize = !CrossfadeTransition.DEFAULT_PREFER_EXACT_INTRINSIC_SIZE
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