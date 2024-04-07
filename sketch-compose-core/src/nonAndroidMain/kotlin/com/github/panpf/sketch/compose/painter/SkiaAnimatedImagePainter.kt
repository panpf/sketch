package com.github.panpf.sketch.compose.painter

import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asComposeImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.toSize
import com.github.panpf.sketch.SkiaAnimatedImage
import com.github.panpf.sketch.SkiaBitmap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.jetbrains.skia.AnimationFrameInfo
import kotlin.math.roundToInt

class SkiaAnimatedImagePainter(
    private val animatedImage: SkiaAnimatedImage,
    private val srcOffset: IntOffset = IntOffset.Zero,
    private val srcSize: IntSize = IntSize(animatedImage.width, animatedImage.height),
    private val filterQuality: FilterQuality = FilterQuality.Low,
) : Painter(), Animatable, RememberObserver {

    /*
     * Why do you need to remember to count?
     *
     * Because when RememberObserver is passed as a parameter of the Composable function, the onRemembered method will be called when the Composable function is executed for the first time, causing it to be remembered multiple times.
     */
    private var rememberedCount = 0
    private val codec = animatedImage.codec
    private var coroutineScope: CoroutineScope? = null
    private val decodeFlow = MutableSharedFlow<Int>()
    private val skiaBitmap: SkiaBitmap = SkiaBitmap().apply { allocPixels(codec.imageInfo) }
    private val composeBitmap: ImageBitmap = skiaBitmap.asComposeImageBitmap()
    private var running = false
    private var frameIndex = -1
    private var alpha: Float = 1.0f
    private var colorFilter: ColorFilter? = null
    private var invalidateTick by mutableIntStateOf(0)
    private var repeatIndex = 0

    /**
     * Number of repeat plays. -1: Indicates infinite repetition. When it is greater than or equal to 0, the total number of plays is equal to '1 + repeatCount'
     */
    private var repeatCount: Int = animatedImage.repeatCount ?: codec.repetitionCount

    override val intrinsicSize: Size = srcSize.toSize()

    init {
        validateSize(srcOffset = srcOffset, srcSize = srcSize)
    }

    override fun DrawScope.onDraw() {
        invalidateTick // Invalidate the scope when invalidateTick changes.
        val dstSize = IntSize(
            this@onDraw.size.width.roundToInt(),
            this@onDraw.size.height.roundToInt()
        )
        drawImage(
            image = composeBitmap,
            srcOffset = srcOffset,
            srcSize = srcSize,
            dstOffset = IntOffset.Zero,
            dstSize = dstSize,
            alpha = alpha,
            colorFilter = colorFilter,
            filterQuality = filterQuality
        )
    }

    private fun startAnimation() {
        coroutineScope ?: return
        if (running) return
        if (codec.frameCount == 0) return

        if (codec.frameCount == 1) {
            coroutineScope?.launch(Dispatchers.IO) {
                codec.readPixels(skiaBitmap, 0)
                invalidateSelf()
            }
            return
        }

        running = true
        repeatIndex = 0
        // When decoding webp animations, readPixels takes a long time, so use the IO thread to decode to avoid getting stuck in the UI thread.
        coroutineScope?.launch(Dispatchers.IO) {
            // TODO Reading frame data in Dispatchers.IO will cause screen confusion on the ios platform.
            decodeFlow.collectLatest { frame ->
                codec.readPixels(skiaBitmap, frame)
                invalidateSelf()
            }
        }
        coroutineScope?.launch {
            while (running && (repeatCount < 0 || repeatIndex <= repeatCount)) {
                frameIndex = (frameIndex + 1) % codec.frameCount
                decodeFlow.emit(frameIndex)
                if (frameIndex == codec.frameCount - 1) {
                    repeatIndex++
                }
                val frameInfo = codec.getFrameInfo(frameIndex)
                val duration = frameInfo.safetyDuration
                delay(duration.toLong())
            }
        }
        animatedImage.animationStartCallback?.invoke()
    }

    private fun stopAnimation() {
        coroutineScope ?: return
        if (!running) return
        running = false
        animatedImage.animationEndCallback?.invoke()
    }

    /**
     * Note: Do not actively call its onRemembered method because this will destroy the rememberedCount count.
     */
    override fun onRemembered() {
        rememberedCount++
        if (rememberedCount != 1) return
        onFirstRemembered()
    }

    private fun onFirstRemembered() {
        coroutineScope = CoroutineScope(Dispatchers.Main)
        startAnimation()
    }

    override fun onAbandoned() = onForgotten()
    override fun onForgotten() {
        if (rememberedCount <= 0) return
        rememberedCount--
        if (rememberedCount != 0) return
        onLastRemembered()
    }

    private fun onLastRemembered() {
        stopAnimation()
        coroutineScope?.cancel()
        coroutineScope = null
    }

    private fun invalidateSelf() {
        if (invalidateTick == Int.MAX_VALUE) {
            invalidateTick = 0
        } else {
            invalidateTick++
        }
    }

    private fun validateSize(srcOffset: IntOffset, srcSize: IntSize): IntSize {
        require(
            srcOffset.x >= 0 &&
                    srcOffset.y >= 0 &&
                    srcSize.width >= 0 &&
                    srcSize.height >= 0 &&
                    srcSize.width <= codec.width &&
                    srcSize.height <= codec.height
        )
        return srcSize
    }

    private val AnimationFrameInfo.safetyDuration: Int
        get() {
            // If the frame does not contain information about a duration, set a reasonable constant duration
            val frameDuration = duration
            return if (frameDuration == 0) DEFAULT_FRAME_DURATION else frameDuration
        }

    override fun applyAlpha(alpha: Float): Boolean {
        this.alpha = alpha
        return true
    }

    override fun applyColorFilter(colorFilter: ColorFilter?): Boolean {
        this.colorFilter = colorFilter
        return true
    }

    override fun start() {
        if (rememberedCount > 0) {
            startAnimation()
        }
    }

    override fun stop() {
        if (rememberedCount > 0) {
            stopAnimation()
        }
    }

    override fun isRunning(): Boolean = running

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SkiaAnimatedImagePainter) return false
        if (codec != other.codec) return false
        if (srcOffset != other.srcOffset) return false
        if (srcSize != other.srcSize) return false
        if (filterQuality != other.filterQuality) return false
        return true
    }

    override fun hashCode(): Int {
        var result = codec.hashCode()
        result = 31 * result + srcOffset.hashCode()
        result = 31 * result + srcSize.hashCode()
        result = 31 * result + filterQuality.hashCode()
        return result
    }

    override fun toString(): String {
        return "SkiaAnimatedImagePainter(codec=$codec, srcOffset=$srcOffset, srcSize=$srcSize, " +
                "filterQuality=$filterQuality)"
    }

    companion object {
        private const val DEFAULT_FRAME_DURATION = 100
    }
}