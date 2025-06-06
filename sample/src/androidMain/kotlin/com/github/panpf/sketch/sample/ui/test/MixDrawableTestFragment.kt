package com.github.panpf.sketch.sample.ui.test

import android.content.Context
import android.os.Build
import android.widget.ImageView.ScaleType
import com.github.panpf.sketch.asDrawable
import com.github.panpf.sketch.decode.ImageDecoderGifDecoder
import com.github.panpf.sketch.decode.KoralGifDecoder
import com.github.panpf.sketch.decode.MovieGifDecoder
import com.github.panpf.sketch.drawable.CrossfadeDrawable
import com.github.panpf.sketch.drawable.ResizeDrawable
import com.github.panpf.sketch.images.ResourceImages
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.request.ImageResult
import com.github.panpf.sketch.request.execute
import com.github.panpf.sketch.sample.image.decode
import com.github.panpf.sketch.util.Size
import com.github.panpf.sketch.util.fitScale
import com.github.panpf.sketch.util.toScale

class MixDrawableTestFragment : BaseDrawableTestFragment() {

    override val title: String
        get() = "Mix Drawable"

    override suspend fun buildDrawables(
        context: Context,
        scaleType: ScaleType,
        itemWidth: Float,
    ): List<DrawableScaleType> {
        val list = mutableListOf<DrawableScaleType>()

        ImageRequest(context, ResourceImages.clockHor.uri).execute()
            .let { it as ImageResult.Success }
            .image.asDrawable().apply {
                list.add(DrawableScaleType(title = "BitmapDrawable", drawable = this))
            }

        ImageRequest(context, ResourceImages.clockHor.uri).execute()
            .let { it as ImageResult.Success }
            .image.asDrawable().apply {
                val resizeDrawable = ResizeDrawable(this, Size(300, 300), scaleType.toScale())
                list.add(
                    DrawableScaleType(
                        title = "ResizeDrawable\nBitmapDrawable",
                        drawable = resizeDrawable
                    )
                )
            }

        ImageRequest(context, ResourceImages.clockHor.uri).execute()
            .let { it as ImageResult.Success }
            .image.asDrawable().apply {
                val crossfadeDrawable =
                    CrossfadeDrawable(null, this, fitScale = scaleType.fitScale)
                list.add(
                    DrawableScaleType(
                        title = "CrossfadeDrawable\nBitmapDrawable",
                        drawable = crossfadeDrawable
                    )
                )
            }

        ImageRequest(context, ResourceImages.animGif.uri)
            .decode(MovieGifDecoder.Factory())?.image?.asDrawable()?.apply {
                println("$this")
                list.add(
                    DrawableScaleType(
                        title = "AnimatableDrawable\nMovieDrawable",
                        drawable = this
                    )
                )
            }

        ImageRequest(context, ResourceImages.animGif.uri)
            .decode(KoralGifDecoder.Factory())?.image?.asDrawable()?.apply {
                println("$this")
                list.add(
                    DrawableScaleType(
                        title = "AnimatableDrawable\nGifDrawableWrapperDrawable",
                        drawable = this
                    )
                )
            }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageRequest(context, ResourceImages.animGif.uri)
                .decode(ImageDecoderGifDecoder.Factory())?.image?.asDrawable()?.apply {
                    println("$this")
                    list.add(
                        DrawableScaleType(
                            title = "ScaledAnimatableDrawable\nAnimatedImageDrawable",
                            drawable = this
                        )
                    )
                }
        }

        return list
    }
}