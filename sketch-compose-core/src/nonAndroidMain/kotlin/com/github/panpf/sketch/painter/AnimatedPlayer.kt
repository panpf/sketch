package com.github.panpf.sketch.painter

import androidx.compose.ui.graphics.asComposeImageBitmap
import com.github.panpf.sketch.ComposeBitmap
import com.github.panpf.sketch.SkiaBitmap
import com.github.panpf.sketch.util.ioCoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.skia.Codec
import org.jetbrains.skia.Color
import kotlin.time.measureTime

class AnimatedPlayer(
    private val codec: Codec,
    private val repeatCount: Int,
    private val onFrame: (ComposeBitmap) -> Unit,
) {

    private val frameInfos = codec.framesInfo
    private val frameCaches = mutableMapOf<Int, SkiaBitmap>()
    private val nextFrameChannel = Channel<Frame>()
    private val renderChannel = Channel<Frame>()
    private val decodeChannel = Channel<Frame>()
    private var nextFrameJob: Job? = null
    private var renderJob: Job? = null
    private var decodeJob: Job? = null
    private var repeatIndex = 0

    private var currentFrame: Frame? = null

    val running: Boolean
        get() = renderJob?.isActive == true || decodeJob?.isActive == true || nextFrameJob?.isActive == true

    fun start(coroutineScope: CoroutineScope) {
        if (running) {
            return
        }
        if (codec.frameCount <= 0) {
            val blackBitmap = SkiaBitmap().apply {
                allocPixels(imageInfo)
                erase(Color.BLACK)
            }
            onFrame(blackBitmap.asComposeImageBitmap())
            return
        }

        repeatIndex = 0
        renderJob = coroutineScope.launch(Dispatchers.Main) {
            for (newFrame in renderChannel) {
                val lastFrame = currentFrame
                currentFrame = newFrame
                onFrame(newFrame.bitmap.composeBitmap)

                // Number of repeat plays. -1: Indicates infinite repetition. When it is greater than or equal to 0, the total number of plays is equal to '1 + repeatCount'
                if ((repeatCount < 0 || repeatIndex <= repeatCount)) {
                    if (newFrame.index == codec.frameCount - 1) {
                        repeatIndex++
                    }

                    /**
                     * Why use [nextFrameChannel] instead of sending the next frame directly in [renderChannel]?
                     * Because you have to wait for the onFrame callback to complete before decoding the next frame,
                     * otherwise the old bitmap may be overwritten by decoding before it has been replaced.
                     */
                    val nextItem = nextFrame(current = newFrame, last = lastFrame)
                    nextFrameChannel.send(nextItem)

                    val fameDuration = frameDuration(newFrame.index)
                    delay(fameDuration)
                }
            }
        }
        decodeJob = coroutineScope.launch(ioCoroutineDispatcher()) {
            for (frame in decodeChannel) {
                frame.bitmap.bitmap.erase(Color.TRANSPARENT)

                val cacheBitmap = frameCaches[frame.index]
                if (cacheBitmap != null) {
                    frame.bitmap.bitmap.installPixels(cacheBitmap.readPixels())
                } else {
                    val decodeElapsedTime = measureTime {
                        try {
                            codec.readPixels(frame.bitmap.bitmap, frame.index)
                        } catch (e: Throwable) {
                            e.printStackTrace()
                        }
                    }
                    val lastFrameIndex = if (frame.index > 0)
                        frame.index - 1 else codec.frameCount - 1
                    val lastFrameDuration = frameDuration(lastFrameIndex)
                    val needCache = decodeElapsedTime.inWholeMilliseconds > lastFrameDuration
                    if (needCache) {
                        val byteArray = frame.bitmap.bitmap.readPixels()
                        val bitmap = SkiaBitmap(codec.imageInfo)
                        bitmap.installPixels(byteArray)
                        frameCaches[frame.index] = bitmap
                    }
                }

                renderChannel.send(frame)
            }
        }
        nextFrameJob = coroutineScope.launch(Dispatchers.Main) {
            decodeChannel.send(nextFrame(current = currentFrame, last = null))

            for (nextFrame in nextFrameChannel) {
                decodeChannel.send(nextFrame)
            }
        }
    }

    fun stop() {
        if (!running) {
            return
        }
        nextFrameJob?.cancel()
        renderJob?.cancel()
        decodeJob?.cancel()
        frameCaches.clear()
    }

    private fun nextFrame(current: Frame?, last: Frame?): Frame {
        val nextFrameIndex = if (current != null) {
            (current.index + 1) % codec.frameCount
        } else {
            0
        }
        return last?.copy(index = nextFrameIndex)
            ?: Frame(
                index = nextFrameIndex,
                bitmap = FrameBitmap(SkiaBitmap(codec.imageInfo))
            )
    }

    private fun frameDuration(index: Int): Long {
        return if (index >= 0 && index < frameInfos.size) {
            frameInfos[index].duration.toLong()
        } else {
            50
        }
    }

    private class FrameBitmap(val bitmap: SkiaBitmap) {
        val composeBitmap: ComposeBitmap = bitmap.asComposeImageBitmap()
    }

    private data class Frame(val index: Int, val bitmap: FrameBitmap)
}