package com.github.panpf.sketch.request

import androidx.annotation.MainThread
import com.github.panpf.sketch.request.RequestInterceptor.Chain
import com.github.panpf.sketch.stateimage.ErrorStateImage
import com.github.panpf.sketch.util.PauseLoadWhenScrollingMixedScrollListener
import com.github.panpf.sketch.stateimage.pauseLoadWhenScrollingError

/**
 * Pause loading new images while the list is scrolling
 *
 * @see DisplayRequest.Builder.pauseLoadWhenScrolling
 * @see PauseLoadWhenScrollingMixedScrollListener
 * @see ErrorStateImage.Builder.pauseLoadWhenScrollingError
 */
class PauseLoadWhenScrollingDisplayInterceptor : RequestInterceptor {

    companion object {
        var scrolling = false
    }

    var enabled = true

    @MainThread
    override suspend fun intercept(chain: Chain): ImageData {
        val request = chain.request
        val finalRequest = if (
            request is DisplayRequest
            && enabled
            && scrolling
            && request.isPauseLoadWhenScrolling
            && !request.isIgnoredPauseLoadWhenScrolling
            && request.depth < Depth.MEMORY
        ) {
            request.newDisplayRequest {
                depth(Depth.MEMORY)
                setDepthFromPauseLoadWhenScrolling()
            }
        } else {
            request
        }
        return chain.proceed(finalRequest)
    }

    override fun toString(): String = "PauseLoadWhenScrollingDisplayInterceptor"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PauseLoadWhenScrollingDisplayInterceptor) return false

        if (enabled != other.enabled) return false

        return true
    }

    override fun hashCode(): Int {
        return enabled.hashCode()
    }
}