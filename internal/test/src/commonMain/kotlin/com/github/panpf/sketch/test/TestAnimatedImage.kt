package com.github.panpf.sketch.test

import com.github.panpf.sketch.AnimatedImage
import com.github.panpf.sketch.Bitmap
import com.github.panpf.sketch.test.utils.createBitmap
import com.github.panpf.sketch.util.Rect

class TestAnimatedImage(
    override val width: Int,
    override val height: Int,
    override val repeatCount: Int = -1,
    override val frameCount: Int = 5,
    override val frameDurations: Array<Int> = arrayOf(50, 50, 50, 50, 50),
    override val cacheDecodeTimeoutFrame: Boolean = false,
    override var animatedTransformation: ((Any, Rect) -> Unit)? = null,
    override var animationStartCallback: (() -> Unit)? = null,
    override var animationEndCallback: (() -> Unit)? = null,
) : AnimatedImage {

    override val byteCount: Long = 4L * width * height

    override val shareable: Boolean = false

    override fun createFrameBitmap(width: Int, height: Int): Bitmap = createBitmap(width, height)

    override fun readFrame(bitmap: Bitmap, frameIndex: Int) {

    }

    override fun checkValid(): Boolean = true

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as TestAnimatedImage
        if (repeatCount != other.repeatCount) return false
        if (frameCount != other.frameCount) return false
        if (cacheDecodeTimeoutFrame != other.cacheDecodeTimeoutFrame) return false
        if (width != other.width) return false
        if (height != other.height) return false
        if (byteCount != other.byteCount) return false
        if (shareable != other.shareable) return false
        if (!frameDurations.contentEquals(other.frameDurations)) return false
        if (animatedTransformation != other.animatedTransformation) return false
        if (animationStartCallback != other.animationStartCallback) return false
        if (animationEndCallback != other.animationEndCallback) return false
        return true
    }

    override fun hashCode(): Int {
        var result = repeatCount
        result = 31 * result + frameCount
        result = 31 * result + cacheDecodeTimeoutFrame.hashCode()
        result = 31 * result + width
        result = 31 * result + height
        result = 31 * result + byteCount.hashCode()
        result = 31 * result + shareable.hashCode()
        result = 31 * result + frameDurations.contentHashCode()
        result = 31 * result + (animatedTransformation?.hashCode() ?: 0)
        result = 31 * result + (animationStartCallback?.hashCode() ?: 0)
        result = 31 * result + (animationEndCallback?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "TestAnimatedImage(" +
                "repeatCount=$repeatCount, " +
                "frameCount=$frameCount, " +
                "frameDurations=${frameDurations.contentToString()}, " +
                "cacheDecodeTimeoutFrame=$cacheDecodeTimeoutFrame, " +
                "animatedTransformation=$animatedTransformation, " +
                "animationStartCallback=$animationStartCallback, " +
                "animationEndCallback=$animationEndCallback, " +
                "width=$width, " +
                "height=$height, " +
                "byteCount=$byteCount, " +
                "shareable=$shareable" +
                ")"
    }
}